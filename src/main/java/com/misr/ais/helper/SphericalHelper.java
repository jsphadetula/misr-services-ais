package com.misr.ais.helper;

import java.util.List;

public class SphericalHelper {

  final static double EARTH_RADIUS = 6371009;

  static double toRadians(double input) {
    return input / 180.0 * Math.PI;
  }

  public static double computeSignedArea(List<LatLng> path) {
    return computeSignedArea(path, EARTH_RADIUS);
  }

  static double computeSignedArea(List<LatLng> path, double radius) {
    int size = path.size();
    if (size < 3) {
      return 0;
    }

    double total = 0;
    var prev = path.get(size - 1);
    double prevTanLat = Math.tan((Math.PI / 2 - toRadians(prev.latitude())) / 2);
    double prevLng = toRadians(prev.longitude());

    for (var point : path) {
      double tanLat = Math.tan((Math.PI / 2 - toRadians(point.latitude())) / 2);
      double lng = toRadians(point.longitude());
      total += polarTriangleArea(tanLat, lng, prevTanLat, prevLng);
      prevTanLat = tanLat;
      prevLng = lng;
    }

    return total * (radius * radius);
  }

  static double polarTriangleArea(double tan1, double lng1, double tan2, double lng2) {
    double deltaLng = lng1 - lng2;
    double t = tan1 * tan2;
    return 2 * Math.atan2(t * Math.sin(deltaLng), 1 + t * Math.cos(deltaLng));
  }

  public static record LatLng(float latitude, float longitude) {
  }

}
