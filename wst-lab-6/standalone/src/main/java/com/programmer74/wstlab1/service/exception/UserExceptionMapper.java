package com.programmer74.wstlab1.service.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserExceptionMapper implements ExceptionMapper<UserServiceException> {
  @Override
  public Response toResponse(UserServiceException e) {
    return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
  }
}