package eu.vital.reply.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Exception> {

	private Logger logger = LogManager.getLogger(RequestEvent.class);

    @Override
    public Response toResponse(Exception e) {
    	StatCounter.incrementErrorNumber();
    	logger.error(System.lineSeparator() + e.getMessage());
    	return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
}
