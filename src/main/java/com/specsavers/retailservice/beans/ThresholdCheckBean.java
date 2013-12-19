package com.specsavers.retailservice.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.CxfConstants;
import org.apache.camel.impl.DefaultMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * It enables web service call when threshold limit reached.
 * 
 * @author Randhir Singh
 */
public class ThresholdCheckBean {

	private static final int ZERO = 0;
	private static final String COUNT = "count";
	private static final Log LOG = LogFactory.getLog(ThresholdCheckBean.class);

	private int thresholdLimit;
	private long storeNumberParam;
	private long countParam;
	private String operationName = null;

	/**
	 * It compares customer ids count with threshold and enables web service
	 * call when threshold limit reached.
	 * 
	 * @param exchange
	 *            Camel Exchange Object
	 * @param value
	 *            List of Map having the number of records that came from
	 *            database query.
	 * @throws Exception
	 *             Any Exception while processing
	 */
	public void process(final Exchange exchange, final List<Map<String, Long>> value) {

		
		LOG.info("value ::  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +value);
		final Long count = value.get(ZERO).get(COUNT);
		LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +count);
		final DefaultMessage message = new DefaultMessage();

		LOG.info(count + "<" + getThresholdLimit() + ": " + (count < getThresholdLimit()));
		if (count < getThresholdLimit()) {

			final List<Long> params = new ArrayList<Long>();
			//params.add(getStoreNumberParam());
			params.add(getCountParam());

			LOG.info("Params added : " + getStoreNumberParam() + "/" + getCountParam());
			message.setHeader(CustomerIDConstants.WEBSERVICE_HEADER, CustomerIDConstants.YES);
			message.setHeader(CxfConstants.OPERATION_NAME, getOperationName());
			message.setBody(params);

		} else {
			message.setHeader(CustomerIDConstants.WEBSERVICE_HEADER, CustomerIDConstants.NO);
		}

		LOG.info("Header value : " + CustomerIDConstants.WEBSERVICE_HEADER + "="
				+ message.getHeader(CustomerIDConstants.WEBSERVICE_HEADER));
		exchange.setOut(message);
		LOG.info("********************************" +message);

	}

	/**
	 * @return Current Threshold value.
	 */
	public int getThresholdLimit() {
		return thresholdLimit;
	}

	/**
	 * @param thresholdLimit
	 *            Current Threshold limit, after when to check if web service
	 *            need to be called.
	 */
	public void setThresholdLimit(final int thresholdLimit) {
		this.thresholdLimit = thresholdLimit;
	}

	/*
	 * Other Setters and Getters
	 */
	public long getStoreNumberParam() {
		return storeNumberParam;
	}

	public void setStoreNumberParam(final long storeNumberParam) {
		this.storeNumberParam = storeNumberParam;
	}

	public long getCountParam() {
		return countParam;
	}

	public void setCountParam(final long countParam) {
		this.countParam = countParam;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(final String operationName) {
		this.operationName = operationName;
	}

}
