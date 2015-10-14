package eu.vital.reply.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable t) {
    	StatCounter.incrementErrorNumeber();
    	return Response.status(Status.INTERNAL_SERVER_ERROR).entity(t.getMessage()).build();
    }
}
