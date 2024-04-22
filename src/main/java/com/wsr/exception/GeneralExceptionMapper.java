package com.wsr.exception;

import com.stripe.exception.StripeException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOG = Logger.getLogger(GeneralExceptionMapper.class);
    
    @Override
    public Response toResponse(Exception exception) {
        
        if (exception instanceof StripeException) {
            
            StripeException se = (StripeException) exception;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Stripe Exception\n")
                    .append("RequestId: ").append(se.getRequestId())
                    .append("code: ").append(se.getStripeError().getCode())
                    .append("message: ").append(se.getStripeError().getMessage());
            LOG.debug(sb.toString(), exception);
            return Response
                    .status(se.getStatusCode())
                    .entity(se.getUserMessage())
                    .build();
        } else if (exception instanceof NotFoundException) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Resource not found.")
                    .build();
        } else if (exception instanceof BadRequestException) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Bad request.")
                    .build();
        } else if (exception instanceof ForbiddenException) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("Access forbidden.")
                    .build();
        } else if (exception instanceof NotAuthorizedException) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Not authorized.")
                    .build();
        } else if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        } else {
            LOG.error("Internal server error", exception);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(exception.getMessage())
                    .build();
        }
    }
}