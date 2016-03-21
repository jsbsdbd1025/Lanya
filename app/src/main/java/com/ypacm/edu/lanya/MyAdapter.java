package com.ypacm.edu.lanya;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DB on 2016/3/9.
 */
public class MyAdapter extends BaseAdapter {

    private List<DeviceBean> mlist;
    private LayoutInflater mInflater;

    public MyAdapter(Context context, List<DeviceBean> list) {
        mlist = list;
        mInflater = LayoutInflater.from(context);
    }

    public int addDevice(DeviceBean device) {
        Log.i("MyAdapter","aaaaa");
        Log.i("MyAdapter","设备数量："+mlist.size());
        int pos=0;
        Log.i("MyAdapter","设备数量："+mlist.size());
        if(mlist.size()==0)
            return 0;
        for(int i =0;i<mlist.size();i++) {
            DeviceBean temp;
            temp = mlist.get(i);
            if (temp.DeviceAddress == device.DeviceAddress)
                pos = i + 1;
        }
        mlist.add(device);
        if(pos>0)
        {
            mlist.remove(pos-1);
        }
        return pos;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.device_item, null);
            viewHolder.DeciveName = (TextView) convertView.findViewById(R.id.device_name);
            viewHolder.DeviceAddress = (TextView) convertView.findViewById(R.id.device_address);
            viewHolder.DeviceRssi = (TextView) convertView.findViewById(R.id.device_rssi);
            viewHolder.DeviceDis = (TextView) convertView.findViewById(R.id.device_dis);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DeviceBean bean = mlist.get(position);

        viewHolder.DeciveName.setText("设备名称：" + bean.DeciveName);
        viewHolder.DeviceAddress.setText("MAC：" + bean.DeviceAddress);
        viewHolder.DeviceRssi.setText("RSSI：" + String.valueOf(bean.DeviceRSSI));
        viewHolder.DeviceDis.setText("距离：" + String.valueOf(bean.DeviceDis));

        return convertView;
    }

    class ViewHolder {
        public TextView DeciveName;
        public TextView DeviceAddress;
        public TextView DeviceRssi;
        public TextView DeviceDis;
    }
}
