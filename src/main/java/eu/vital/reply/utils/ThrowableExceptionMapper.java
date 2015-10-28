package eu.vital.reply.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

	private Logger logger = LogManager.getLogger(RequestEvent.class);

    @Override
    public Response toResponse(Throwable t) {
    	StatCounter.incrementErrorNumber();
    	logger.error("ERROR " + t.getMessage());
    	return Response.status(Status.INTERNAL_SERVER_ERROR).entity(t.getMessage()).build();
    }
}
