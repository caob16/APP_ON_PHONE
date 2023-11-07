package com.example.app_on_phone.signin.util;

public class MapUtils {

    /**
     * 地球半径
     */
    private static double EARTH_RADIUS = 6378138.0;

    /**
     * @param radius 半径（m）
     * @param lat1 要判断是否在圆内的点的纬度
     * @param lng1 要判断是否在圆内的点的经度
     * @param lat2 中心点纬度
     * @param lng2 中心点经度
     * @return
     */
    public static boolean isInCircle(double radius, double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        if (s > radius) {// 不在圆上
            return false;
        } else {
            return true;
        }
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
