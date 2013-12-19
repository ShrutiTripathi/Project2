package com.specsavers.retailservice.beans;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSPasswordCallback;

public class ClientPasswordCallBackEvent implements CallbackHandler {
	private static final Log LOG = LogFactory.getLog(ExceptionLogger.class);

    public void handle(Callback[] callbacks) throws IOException, 
            UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        LOG.info("////////////////////////////////////////////" );

        if ("storeclient".equals(pc.getIdentifier())) {
            pc.setPassword("welcome1");
        }
    }
}
