package com.specsavers.retailservice.beans;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.cxf.CxfConstants;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.specsavers.jpa.CustomerId;
import com.specsavers.retailcentral.objectlibrary.common.v1.AbstractResponseType.ResultStatus;
import com.specsavers.retailcentral.servicelibrary.referencedata.xsd.getuniquecustomerid.v1.GetUniqueCustomerIDResponseType;
import com.specsavers.retailcentral.servicelibrary.referencedata.xsd.getuniquecustomerid.v1.GetUniqueCustomerIDResponseType.Result;
import com.specsavers.retailservice.exceptions.WebServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-camel-context.xml" })
public class TestEndToEnd {

	private static final String NULL_VALUE = "Null value";
	private final static String CALL_WEB_SERVICE = "yes";
	private final static long count = 15l;
	private final static long storeNumber = 5656l;
	private final static String operationName = "getUniqueCustomerID";
	private final static String SUCCESS_FULL_MESSAGE = "Record(s) Saved Successfully!!!";
	private static final String RESULTS_ARE_EQUALS = "Results are equals";
	private static final Log LOG = LogFactory.getLog(TestEndToEnd.class);

	@Autowired
	private EntityManagerFactory entityFactory;

	@Autowired
	private ThresholdCheckBean thresholdCheckBean;

	@Autowired
	private CustomerIdSavingBean customerIdSavingBean;

	@Autowired
	protected CamelContext camelContext;

	@Test
	public void endToEnd() throws WebServiceException {

		populateDatabaseWithTempData();

		final ProducerTemplate<Exchange> producerTemplate = camelContext
				.createProducerTemplate();
		final JdbcProcessor jdbcProcessor = new JdbcProcessor();

		final Exchange exchangeProducer = producerTemplate.request(
				"jdbc:mysqlSOADataSource", jdbcProcessor);

		final List<Map<String, Long>> result = (List<Map<String, Long>>) exchangeProducer
				.getOut().getBody();

		final Map<String, Long> map = result.get(0);

		assertEquals(RESULTS_ARE_EQUALS, Long.valueOf(5), map.get("count"));

		thresholdCheckBean.setThresholdLimit(6);

		thresholdCheckBean.process(exchangeProducer, result);

		final String callWebService = (String) exchangeProducer.getOut()
				.getHeader(CustomerIDConstants.WEBSERVICE_HEADER);

		final String outOperationName = (String) exchangeProducer.getOut()
				.getHeader(CxfConstants.OPERATION_NAME);
		final List<Long> outParam = (List<Long>) exchangeProducer.getOut()
				.getBody();

		if (callWebService.equals(CALL_WEB_SERVICE)) {

			assertEquals(RESULTS_ARE_EQUALS, operationName, outOperationName);
			assertArrayEquals(new Long[] { count }, outParam.toArray());

			customerIdSavingBean.setEntityFactory(entityFactory);
			customerIdSavingBean.process(exchangeProducer,
					getDummyCustomerIdsResponse());

			assertEquals(SUCCESS_FULL_MESSAGE, exchangeProducer.getOut()
					.getHeader("message"));
		} else {
			assertNull(NULL_VALUE, outOperationName);
			assertNull(NULL_VALUE, outParam);
		}

	}

	private class JdbcProcessor implements Processor {
		public void process(final Exchange exchange) {
			exchange.getIn().setBody(
					"select count(*) as count from customer_id_windows");
		}
	}

	private List<GetUniqueCustomerIDResponseType> getDummyCustomerIdsResponse() {
		final List<GetUniqueCustomerIDResponseType> response = new ArrayList<GetUniqueCustomerIDResponseType>();
		GetUniqueCustomerIDResponseType responseType = new GetUniqueCustomerIDResponseType();
		final ResultStatus status = new ResultStatus();
		status.setCode("RCS-001");
		responseType.setResultStatus(status);
		Result result = new Result();
		result.setStartId(1);
		result.setEndId(10);
		responseType.setResult(result);
		response.add(0, responseType);
		return response;
	}

	// Populate data with 5 Test customers.

	private void populateDatabaseWithTempData() {
		final CustomerId[] customerId = new CustomerId[5];

		final EntityManager eManger = entityFactory.createEntityManager();

		int tempId = 0;

		eManger.getTransaction().begin();

		for (CustomerId customer : customerId) {

			tempId = RandomUtils.nextInt();

			customer = new CustomerId();
			customer.setFirstFree(tempId);
			customer.setLastFree(tempId);
			eManger.persist(customer);
		}

		eManger.getTransaction().commit();
		eManger.clear();
		eManger.close();
	}

	@Before
	public void setUp() throws Exception {
		camelContext.stop();
	}

	@After
	public void tearDown() {
		clearCustomerRecords();
	}

	private void clearCustomerRecords() {
		final EntityManager eManager = entityFactory.createEntityManager();

		try {
			eManager.getTransaction().begin();
			eManager.createNativeQuery("delete from customer_id_windows")
					.executeUpdate();
			eManager.getTransaction().commit();

		} catch (final Exception e) {
			LOG.error(e.getLocalizedMessage());
		}

		eManager.close();
	}

}