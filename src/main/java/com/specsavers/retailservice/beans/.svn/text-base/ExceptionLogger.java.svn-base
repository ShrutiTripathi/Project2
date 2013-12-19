package com.specsavers.retailservice.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExceptionLogger implements Processor {

	private static final Log LOG = LogFactory.getLog(ExceptionLogger.class);

	/**
	 * Logging of the exception
	 */
	public void process(final Exchange exchange) {

		final Exception exception = (Exception) exchange.getIn().getHeaders().get("caught.exception");

		LOG.error("Fault :::  "+exception.getLocalizedMessage());
	}

}
