package com.specsavers.retailservice.beans;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.specsavers.retailcentral.objectlibrary.common.v1.AbstractResponseType.ResultStatus;
import com.specsavers.retailcentral.servicelibrary.referencedata.xsd.getuniquecustomerid.v1.GetUniqueCustomerIDResponseType;
import com.specsavers.retailcentral.servicelibrary.referencedata.xsd.getuniquecustomerid.v1.GetUniqueCustomerIDResponseType.Result;
import javax.xml.ws.WebServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-camel-context.xml" })
public class TestCustomerIdSavingBean {

	private static final String RESULTS_ARE_EQUALS = "Results are equals";

	@Autowired
	private EntityManagerFactory entityFactory;

	@Autowired
	private CustomerIdSavingBean customerIdSavingBean;

	@Autowired
	private CamelContext camelContext;

	private static final Log LOG = LogFactory
			.getLog(TestCustomerIdSavingBean.class);

	@Before
	public void setUp() throws Exception {
		camelContext.stop();
	}

	@After
	public void tearDown() {
		clearCustomerRecords();
	}

	@Test
	public void testProcessWhenValidCustomerIdsInResponse()
			throws WebServiceException {

		final Exchange exchange = new DefaultExchange(camelContext);
		customerIdSavingBean.process(exchange, getValidCustomerIdsInResponse());

		assertEquals(RESULTS_ARE_EQUALS, 1, customerRowCount());
	}

	@Test
	public void testProcessWhenInvalidCustomerIdsInResponse()
			throws WebServiceException {

		final Exchange exchange = new DefaultExchange(camelContext);
		customerIdSavingBean.process(exchange,
				getInvalidCustomerIdsInResponse());

		assertEquals(RESULTS_ARE_EQUALS, 0, customerRowCount());
	}

	@Test(expected = WebServiceException.class)
	public void testProcessWhenNullCustomerIdsInResponse()
			throws WebServiceException {

		final Exchange exchange = new DefaultExchange(camelContext);
		customerIdSavingBean.process(exchange, null);

		assertEquals(RESULTS_ARE_EQUALS, 0, customerRowCount());
	}

	@Test(expected = WebServiceException.class)
	public void testProcessWhenResponseIsNull() throws WebServiceException {

		final Exchange exchange = new DefaultExchange(camelContext);
		customerIdSavingBean.process(exchange, null);

		assertEquals(RESULTS_ARE_EQUALS, 0, customerRowCount());
	}

	private List<GetUniqueCustomerIDResponseType> getValidCustomerIdsInResponse() {

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

	private List<GetUniqueCustomerIDResponseType> getInvalidCustomerIdsInResponse() {
		final List<GetUniqueCustomerIDResponseType> response = new ArrayList<GetUniqueCustomerIDResponseType>();
		GetUniqueCustomerIDResponseType responseType = new GetUniqueCustomerIDResponseType();
		final ResultStatus status = new ResultStatus();
		status.setCode("RCS-017");
		responseType.setResultStatus(status);
		response.add(0, responseType);
		return response;
	}

	private List<GetUniqueCustomerIDResponseType> getInvalidCustomerIdsInResponseHavingLargeSize() {
		final List<List<Long>> list = new ArrayList<List<Long>>();

		final GetUniqueCustomerIDResponseType response = new GetUniqueCustomerIDResponseType();
		response.getResult().setStartId(2168947892374902l);
		response.getResult().setEndId(2168947892374905l);

		return (List<GetUniqueCustomerIDResponseType>) response;
	}

	private List<GetUniqueCustomerIDResponseType> getNullCustomerIdsInResponse() {

		return (new ArrayList<GetUniqueCustomerIDResponseType>());
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
		} finally {
			eManager.close();
		}
	}

	private long customerRowCount() {

		final EntityManager eManager = entityFactory.createEntityManager();
		BigInteger count = BigInteger.ZERO;

		try {
			count = (BigInteger) eManager
					.createNativeQuery(
							"select count(*) from customer_id_windows")
					.getResultList().get(0);
		} catch (final Exception e) {
			LOG.error(e.getLocalizedMessage());
		}

		return count.longValue();
	}

	public void setEntityFactory(final EntityManagerFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public void setCustomerIdSavingBean(
			final CustomerIdSavingBean customerIdSavingBean) {
		this.customerIdSavingBean = customerIdSavingBean;
	}

	public void setCamelContext(final CamelContext camelContext) {
		this.camelContext = camelContext;
	}

}
