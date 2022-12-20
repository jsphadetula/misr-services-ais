package com.misr.ais.configuration;

public class Routes {

  public static class Land {
    public static final String Index = "/api/v1/lands";
    public static final String ResourceById = Index + "/{id}";
    public static final String Configurations = ResourceById + "/configurations";
  }

}
