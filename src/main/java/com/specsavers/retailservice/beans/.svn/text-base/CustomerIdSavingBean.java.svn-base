package com.specsavers.retailservice.beans;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.ws.WebServiceException;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.specsavers.jpa.CustomerId;
import com.specsavers.retailcentral.objectlibrary.common.v1.AbstractResponseType.ResultStatus;
import com.specsavers.retailcentral.servicelibrary.referencedata.xsd.getuniquecustomerid.v1.GetUniqueCustomerIDResponseType;
import com.specsavers.retailcentral.servicelibrary.referencedata.xsd.getuniquecustomerid.v1.GetUniqueCustomerIDResponseType.Result;
/**
 * Camel routing bean which save customer Id's into customer_id_windows table,
 * received from the web service call.
 * 
 * @author Rajiv Kumar
 */
public class CustomerIdSavingBean {

	private static final int ZERO = 0;

	private static final Log LOG = LogFactory
			.getLog(CustomerIdSavingBean.class);

	@Autowired
	private EntityManagerFactory entityFactory;

	/**
	 * Getter to return EntityManagerFactory
	 * 
	 * @return EntityManagerFactory
	 */
	public EntityManagerFactory getEntityFactory() {
		return entityFactory;
	}

	/**
	 * Setter to set the value of EntityManagerFactory
	 * 
	 * @param entityFactory
	 *            JPA EntityManagerFactory object.
	 */
	public void setEntityFactory(final EntityManagerFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	/**
	 * Save customer Id's received from the web service call.
	 * 
	 * @param exchange
	 *            The Camel Exchange object.
	 * @param list
	 *            Customer Id's Object return from web service processing
	 * @throws Exception
	 *             Throws exception for any errors.
	 */
	public void process(final Exchange exchange, final List<GetUniqueCustomerIDResponseType> response)
			throws WebServiceException {

		GetUniqueCustomerIDResponseType getCustomerResponse = null;
		
		if (response == null) {
			LOG.info("value is qual to null..........................");
			throw new WebServiceException(
					"Empty or null response from web service call");
		} else if (!response.isEmpty()) {
			getCustomerResponse = response.get(0);
		} else {
			LOG.info("No Value Return From Webservice Call..........................");
			return;
		}

		final EntityManager entityManager = getEntityFactory()
				.createEntityManager();

		entityManager.getTransaction().begin();
		final DefaultMessage defaultMessage = new DefaultMessage();
		
		final ResultStatus status = getCustomerResponse.getResultStatus();
		if (status != null) {
			LOG.debug("Status :: " + status.getCode() + "Message :: "
					+ status.getMessage());
			if (status.getCode().equals(CustomerIDConstants.STATUS_RCS_001)) {
				final Result value = getCustomerResponse.getResult();
				CustomerId customerId = new CustomerId();

				customerId.setFirstFree(value.getStartId());
				customerId.setLastFree(value.getEndId());

				entityManager.persist(customerId);

				entityManager.getTransaction().commit();
				entityManager.close();
				
				defaultMessage.setHeader(CustomerIDConstants.MESSAGE,
						CustomerIDConstants.RECORD_SAVED_SUCCESSFULLY);

			} else if (status.getCode().equals(
					CustomerIDConstants.STATUS_RCS_017)) {
				defaultMessage.setHeader(CustomerIDConstants.MESSAGE,
						CustomerIDConstants.ERROR_MSG_RCS_017);
			} else if (status.getCode().equals(
					CustomerIDConstants.STATUS_RCS_002)) {
				defaultMessage.setHeader(CustomerIDConstants.MESSAGE,
						CustomerIDConstants.ERROR_MSG_RCS_002);
			}
		}

		exchange.setOut(defaultMessage);

	}
}
