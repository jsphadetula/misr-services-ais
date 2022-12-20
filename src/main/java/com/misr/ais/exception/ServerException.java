package com.misr.ais.exception;

public class ServerException extends RuntimeException {

  public ServerException(String message, Throwable e) {
    super(message, e);
  }

}
