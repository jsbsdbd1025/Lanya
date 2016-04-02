package com.ypacm.edu.lanya;


/**
 * Created by DB on 2016/3/9.
 */
public class DeviceBean {
    public String DeciveName;
    public String DeviceAddress;
    public short DeviceRSSI;
    public double DeviceDis;
    public int X;
    public int Y;

    public DeviceBean(String deciveName, String deviceAddress, short deviceRSSI, double deviceDis, int x, int y) {
        DeciveName = deciveName;
        DeviceAddress = deviceAddress;
        DeviceRSSI = deviceRSSI;
        DeviceDis = deviceDis;
        X = x;
        Y = y;
    }

    public String getName()
    {
        return DeciveName;
    }
}
