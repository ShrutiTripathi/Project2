package com.specsavers.retailservice.beans;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.specsavers.jpa.CustomerId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-camel-context.xml" })
public class TestThresholdCheckBean {

	@Autowired
	private EntityManagerFactory entityFactory;

	@Autowired
	private ThresholdCheckBean thresholdCheckBean;

	@Autowired
	protected CamelContext camelContext;

	@Test
	public void testProcessWhenTreshHoldReached() {

		populateDatabaseWithTempData();

		final ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
		final JdbcProcessor jdbcProcessor = new JdbcProcessor();
		final Exchange exchangeProducer = producerTemplate.request("jdbc:mysqlSOADataSource", jdbcProcessor);
		final List<Map<String, Long>> result = (List<Map<String, Long>>) exchangeProducer.getOut().getBody();
		final Map<String, Long> map = result.get(0);

		assertEquals(new Long(5), map.get("count"));
		thresholdCheckBean.setThresholdLimit(6);
		thresholdCheckBean.process(exchangeProducer, result);

		assertEquals(CustomerIDConstants.YES, exchangeProducer.getOut().getHeader("callWebService"));
	}

	@Test
	public void testProcessWhenTreshHoldLessThanIdCount() {

		populateDatabaseWithTempData();
		final ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
		final JdbcProcessor jdbcProcessor = new JdbcProcessor();
		final Exchange exchangeProducer = producerTemplate.request("jdbc:mysqlSOADataSource", jdbcProcessor);
		final List<Map<String, Long>> result = (List<Map<String, Long>>) exchangeProducer.getOut().getBody();
		final Map<String, Long> map = result.get(0);
		assertEquals(new Long(5), map.get("count"));

		thresholdCheckBean.setThresholdLimit(4);
		thresholdCheckBean.process(exchangeProducer, result);

		assertEquals(CustomerIDConstants.NO, exchangeProducer.getOut().getHeader("callWebService"));
	}

	@Test
	public void testProcessWhenTreshHoldEqualToIdCount() {

		populateDatabaseWithTempData();
		final ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
		final JdbcProcessor jdbcProcessor = new JdbcProcessor();
		final Exchange exchangeProducer = producerTemplate.request("jdbc:mysqlSOADataSource", jdbcProcessor);

		final List<Map<String, Long>> result = (List<Map<String, Long>>) exchangeProducer.getOut().getBody();
		final Map<String, Long> map = result.get(0);
		assertEquals(new Long(5), map.get("count"));
		thresholdCheckBean.setThresholdLimit(5);
		thresholdCheckBean.process(exchangeProducer, result);

		assertEquals(CustomerIDConstants.NO, exchangeProducer.getOut().getHeader("callWebService"));
	}

	private class JdbcProcessor implements Processor {
		public void process(final Exchange exchange) throws Exception {
			exchange.getIn().setBody(CustomerIDConstants.SELECT_COUNT_QUERY);
		}
	}

	private void populateDatabaseWithTempData() {
		final CustomerId[] customerId = new CustomerId[5];

		final EntityManager eManger = entityFactory.createEntityManager();

		eManger.getTransaction().begin();
		for (CustomerId customer : customerId) {
			final int tempId = RandomUtils.nextInt();

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
		final EntityManager eManger = entityFactory.createEntityManager();
		eManger.getTransaction().begin();
		eManger.createNativeQuery("delete from customer_id_windows").executeUpdate();
		eManger.getTransaction().commit();
		eManger.close();
	}

	public void setEntityFactory(final EntityManagerFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public void setThresholdCheckBean(final ThresholdCheckBean thresholdCheckBean) {
		this.thresholdCheckBean = thresholdCheckBean;
	}

	public void setCamelContext(final CamelContext camelContext) {
		this.camelContext = camelContext;
	}

}
