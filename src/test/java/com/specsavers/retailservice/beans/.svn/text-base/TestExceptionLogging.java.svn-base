package com.specsavers.retailservice.beans;

import static org.easymock.EasyMock.expect;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.specsavers.retailservice.exceptions.WebServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-camel-context.xml" })
public class TestExceptionLogging {

	private ExceptionLogger exceptionLogger;

	private IMocksControl mocks;
	private Exchange mockExchange;
	private Message mockMessage;

	@Before
	public void setUp() throws Exception {
		mocks = EasyMock.createControl();
		mockExchange = mocks.createMock(Exchange.class);
		mockMessage = mocks.createMock(Message.class);

		exceptionLogger = new ExceptionLogger();
	}

	@Test
	public void testLoggingDataAccessException() {

		final Map<String, Object> exceptionMap = createExceptionMap();

		expect(mockMessage.getHeaders()).andReturn(exceptionMap);
		expect(mockExchange.getIn()).andReturn(mockMessage);

		mocks.replay();

		exceptionLogger.process(mockExchange);

		mocks.verify();

	}

	private Map<String, Object> createExceptionMap() {
		final Map<String, Object> exceptionMap = new HashMap<String, Object>();

		final Exception exception = new WebServiceException(
				"Empty or null response from web service call.");

		exceptionMap.put("caught.exception", exception);

		return exceptionMap;
	}

}
