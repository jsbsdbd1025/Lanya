package com.ypacm.edu.lanya;


/**
 * Created by DB on 2016/3/9.
 */
public class DeviceBean {
    public String DeciveName;
    public String DeviceAddress;
    public short DeviceRSSI;
    public double DeviceDis;

    public DeviceBean(String deciveName, String deviceAddress, short deviceRSSI, double deviceDis) {
        DeciveName = deciveName;
        DeviceAddress = deviceAddress;
        DeviceRSSI = deviceRSSI;
        DeviceDis = deviceDis;
    }

    public String getName()
    {
        return DeciveName;
    }
}
