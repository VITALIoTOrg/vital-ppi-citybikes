package eu.vital.reply.utils;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by f.deceglia on 24/03/2015.
 */

public class PpiApplicationEventListener implements ApplicationEventListener {

    private volatile int requestCount = 0;

    private Logger logger = LogManager.getLogger(PpiApplicationEventListener.class);

    @Override
    public void onEvent(ApplicationEvent applicationEvent) {
        switch (applicationEvent.getType()) {
            case INITIALIZATION_FINISHED:
                logger.info("Application "
                        + applicationEvent.getResourceConfig().getApplicationName()
                        + " was initialized.");
                break;
            case DESTROY_FINISHED:
                logger.info("Application "
                        + applicationEvent.getResourceConfig().getApplicationName() + " destroyed.");
                break;
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        requestCount++;
        logger.info("Request " + requestCount + " started.");
        // return the listener instance that will handle this request.
        return new PpiRequestEventListener(requestCount);

    }
}
