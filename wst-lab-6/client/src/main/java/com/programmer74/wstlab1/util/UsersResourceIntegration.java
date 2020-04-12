package com.programmer74.wstlab1.util;

import com.programmer74.wstlab1.client.service.User;
import com.programmer74.wstlab1.database.dto.UserDTO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.slf4j.Slf4j;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class UsersResourceIntegration {
  private final static String BASE_URL = "http://localhost:8081/users/";

  private final static String FIND_ALL_URL = BASE_URL + "all";

  private final static String FILTER_URL = BASE_URL + "filter";

  private final static String UPDATE_URL = BASE_URL + "%d";

  private final static String DELETE_URL = BASE_URL + "%d";

  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  public List<User> findAll() throws Exception {
    Client client = Client.create();
    WebResource webResource = client.resource(FIND_ALL_URL);
    return getUsers(webResource);
  }

  private List<User> getUsers(final WebResource webResource) throws Exception {
    ClientResponse response =
        webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
    if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
      throwNecessaryException(response);
    }
    GenericType<List<User>> type = new GenericType<List<User>>() {
    };
    return response.getEntity(type);
  }

  public List<User> findWithFilters(
      Long id,
      String login,
      String password,
      String email,
      Boolean gender,
      String registerDate
  ) throws Exception {
    Client client = Client.create();
    WebResource webResource = client.resource(FILTER_URL);
    if (id != null) {
      webResource = webResource.queryParam("id", id + "");
    }
    if (login != null) {
      webResource = webResource.queryParam("login", login);
    }
    if (password != null) {
      webResource = webResource.queryParam("password", password);
    }
    if (email != null) {
      webResource = webResource.queryParam("email", email);
    }
    if (gender != null) {
      webResource = webResource.queryParam("gender", gender.toString());
    }
    if (registerDate != null) {
      webResource = webResource.queryParam("registerDate", registerDate);
    }
    return getUsers(webResource);
  }

  public Long insert(UserDTO userDTO) throws Exception {
    Client client = Client.create();
    WebResource webResource = client.resource(BASE_URL);
    ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN)
        .entity(userDTO)
        .post(ClientResponse.class);
    if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
      throwNecessaryException(response);
    }
    GenericType<String> type = new GenericType<String>() {
    };
    return Long.parseLong(response.getEntity(type));
  }

  public int update(Long id, UserDTO userDTO) throws Exception {
    Client client = Client.create();
    if (id == null) {
      return -1;
    }
    WebResource webResource = client.resource(String.format(UPDATE_URL, id));
    ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN)
        .entity(userDTO)
        .put(ClientResponse.class);
    return clientIntResponseOrException(response);
  }

  public int delete(Long id) throws Exception {
    Client client = Client.create();
    if (id == null) {
      return -1;
    }
    WebResource webResource = client.resource(String.format(DELETE_URL, id));
    ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN).delete(ClientResponse.class);
    return clientIntResponseOrException(response);
  }

  private int clientIntResponseOrException(final ClientResponse response) throws Exception {
    if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
      throwNecessaryException(response);
    }
    GenericType<String> type = new GenericType<String>() {
    };
    return Integer.parseInt(response.getEntity(type));
  }

  private void throwNecessaryException(final ClientResponse response) throws Exception {
    final GenericType<String> type = new GenericType<String>() {};
    if (response.getStatus() == Status.BAD_REQUEST.getStatusCode()) {
      throw new ServerBadRequestException(response.getStatus(), response.getEntity(type));
    }
    if (response.getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
      throw new ServerSQLException(response.getStatus(), response.getEntity(type));
    }
    throw new IllegalStateException(
        "Request failed. HTTP code: " + response.getStatus() + " " + response.getEntity(type));
  }
}