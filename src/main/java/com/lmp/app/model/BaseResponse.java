package com.lmp.app.model;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.http.HttpStatus;

import com.lmp.app.entity.ResponseStatus;

public class BaseResponse {

  protected int statusCode;
  protected String message;


  public static CartResponse responseStatus(ResponseStatus status) {
    CartResponse cResponse = new CartResponse();
    cResponse.setStatusCode(status.getCode());
    cResponse.setErrorMessage(status.toString());
    return cResponse;
  }

  public static BaseResponse invalidSearchRequest(String message) {
    BaseResponse response = new BaseResponse();
    response.statusCode = HttpStatus.BAD_REQUEST.value();
    response.message = message;
    return response;
  }

  public static BaseResponse solrErrorResponse(QueryResponse queryResponse) {
    BaseResponse response = new BaseResponse();
    response.statusCode = queryResponse.getStatus();
    return response;
  }

  public static BaseResponse empty() {
    BaseResponse response = new BaseResponse();
    response.statusCode = HttpStatus.OK.value();
    return response;
  }

  public int getStatusCode() {
    return statusCode;
  }
  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }
  public String getMessage() {
    return message;
  }
  public void setErrorMessage(String message) {
    this.message = message;
  }
}
