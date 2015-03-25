package eu.vital.reply.utils;

import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by f.deceglia on 24/03/2015.
 */
public class PpiRequestEventListener implements RequestEventListener {

    private volatile int count = 0;
    private String aux = "";

    private Logger logger = LogManager.getLogger(PpiRequestEventListener.class);

    public PpiRequestEventListener(int count) {
        this.count = count;
    }


    @Override
    public void onEvent(RequestEvent requestEvent) {
        switch (requestEvent.getType()) {
            case RESOURCE_METHOD_START:
                logger.info("Resource method "
                        + requestEvent.getUriInfo().getMatchedResourceMethod()
                        .getHttpMethod()
                        + " started for request " + count);
                aux = requestEvent.getUriInfo().getPath();
                break;
            case RESOURCE_METHOD_FINISHED:
                logger.info("Request " + requestEvent
                        + " finished.");
                StatCounter.setRequestedNumber(count);
                break;
            case ON_EXCEPTION:
                logger.info("Request " + requestEvent + " has thrown an Exception");
                StatCounter.incrementErrorNumeber();
                break;
        }
    }
}
