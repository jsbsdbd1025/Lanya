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
    public int count;
    public double sumDis;
    public double maxnDis;
    public double minnDis;

    public DeviceBean(String deciveName, String deviceAddress, short deviceRSSI, double deviceDis, int x, int y, int count, double sumDis, double maxnDis, double minnDis) {
        DeciveName = deciveName;
        DeviceAddress = deviceAddress;
        DeviceRSSI = deviceRSSI;
        DeviceDis = deviceDis;
        X = x;
        Y = y;
        this.count = count;
        this.sumDis = sumDis;
        this.maxnDis = maxnDis;
        this.minnDis = minnDis;
    }

    public String getName()
    {
        return DeciveName;
    }
}
