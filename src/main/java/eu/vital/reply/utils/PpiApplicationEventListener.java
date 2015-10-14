package eu.vital.reply.utils;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by f.deceglia on 24/03/2015.
 * Update by l.bracco on 14/10/2015.
 */

@Provider
public class PpiApplicationEventListener implements ContainerResponseFilter {

    private volatile int requestCount = 0;

    private Logger logger = LogManager.getLogger(PpiApplicationEventListener.class);

	@Override
	public void filter(ContainerRequestContext arg0, ContainerResponseContext arg1) throws IOException {
		if(arg1.getStatus() != 500) {
	        requestCount++;
	        logger.info("Request " + requestCount + " started.");
	        StatCounter.setRequestedNumber(requestCount);
		}
	}
}
