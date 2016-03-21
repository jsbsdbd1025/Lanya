package com.ypacm.edu.lanya;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private static final int SHOW_DATA = 1;
    private static final int SHOW_MAP = 2;
    private final static int SHOW_MODE = SHOW_MAP;
    static final String TAG = "MainActivity";
    static Activity myThis;
    BluetoothAdapter
            mAdapter = BluetoothAdapter.getDefaultAdapter();
    List<DeviceBean> itemBeanList = new ArrayList<>();
    private static final int REQUEST_ENABLE_BT = 1;
    private Math math;
    int flag = 0;
    float rotate = -45;
    Handler handler = new Handler();


    private SensorManager sensorManager;
    private Sensor acc_sensor;
    private Sensor mag_sensor;
    //加速度传感器数据
    float accValues[] = new float[3];
    //地磁传感器数据
    float magValues[] = new float[3];
    //旋转矩阵，用来保存磁场和加速度的数据
    float r[] = new float[9];
    //模拟方向传感器的数据（原始数据为弧度）
    float values[] = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mag_sensor, SensorManager.SENSOR_DELAY_GAME);
        myThis = this;
        String[] bluetoothDevice = getResources().getStringArray(R.array.bluetooth_device);
//        mlistView = (ListView) findViewById(R.id.list);
        if (!mAdapter.isEnabled()) {
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, REQUEST_ENABLE_BT);
        }

        if (SHOW_MODE == SHOW_DATA) {
            Log.i(TAG, "" + bluetoothDevice.length);
            for (int i = 1; i <= bluetoothDevice.length; i++) {
                itemBeanList.add(new DeviceBean("icon" + i, bluetoothDevice[i - 1], (short) 0, 0));
            }
        }

        Log.i(TAG, mAdapter.getAddress());
        Log.i(TAG, mAdapter.getName());

        DataUpdate();

        BroadcastReceiver deviceFound = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "xiuxiuxiu...");
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Log.i(TAG, "从Intent得到blueDevice对象");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        Log.i(TAG, "获取设备信息");
                        //信号强度。
                        String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                        BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        short remoteDeviceRssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                        double remoteDevicedis = calcDis(remoteDeviceRssi);

                        boolean liveFlag = false;
                        for (DeviceBean bean : itemBeanList) {
                            if (bean.DeviceAddress.equals(remoteDevice.getAddress())) {
                                liveFlag = true;
                                bean.DeviceRSSI = remoteDeviceRssi;
                                bean.DeviceDis = remoteDevicedis;
                            }
                        }
                        if (liveFlag == false)
                            itemBeanList.add(new DeviceBean(remoteDeviceName, remoteDevice.getAddress(), remoteDeviceRssi, remoteDevicedis));

                        Log.i(TAG, "OK");

                        DataUpdate();
                    }
                }
            }
        };
        registerReceiver(deviceFound, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        handler.post(task);
    }

    public void DataUpdate() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        if (SHOW_MODE == SHOW_DATA) {
            FragmentList fragment = new FragmentList();
            fragment.setData(itemBeanList);
            beginTransaction.add(R.id.fragmentlayout, fragment, "Fragment");
        } else {
            FragmentMap fragment = new FragmentMap();
            beginTransaction.add(R.id.fragmentlayout, fragment, "map");
        }
        beginTransaction.commit();
        Log.i(TAG, "提交成功");
    }

    public double calcDis(short rssi) {

        int iRssi = math.abs(rssi);
        double power = (iRssi - 60) / (10 * 3.75);
        return math.pow(10, power);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                Toast.makeText(getBaseContext(), "刷新", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.menu_switch: {

                if (flag == 0) {
                    item.setIcon(R.drawable.ic_action_open);
                    Toast.makeText(getBaseContext(), "开始广播", Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else {
                    Toast.makeText(getBaseContext(), "停止广播", Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_action_close);
                    flag = 0;
                }
                return true;
            }
            // Here we might start a background refresh task
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "蓝牙已打开");
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "蓝牙取消打开");
            }
        }
    }

    private Runnable task = new Runnable() {
        public void run() {
            Log.i(TAG, "duangduangduang");
            // TODO Auto-generated method stub
            //需要执行的代码
            if (mAdapter.isDiscovering())
                mAdapter.cancelDiscovery();
            if (flag == 1) {
                mAdapter.startDiscovery();
            }
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        }
        /**public static boolean getRotationMatrix (float[] R, float[] I, float[] gravity, float[] geomagnetic)
         * 填充旋转数组r
         * r：要填充的旋转数组
         * I:将磁场数据转换进实际的重力坐标中 一般默认情况下可以设置为null
         * gravity:加速度传感器数据
         * geomagnetic：地磁传感器数据
         */
        SensorManager.getRotationMatrix(r, null, accValues, magValues);
        /**
         * public static float[] getOrientation (float[] R, float[] values)
         * R：旋转数组
         * values ：模拟方向传感器的数据
         */

        SensorManager.getOrientation(r, values);

        rotate = (int) Math.toDegrees(values[0]);
        rotate = rotate / 10 * 10;
        Log.i(TAG, String.valueOf(rotate));
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        FragmentDirection direction = new FragmentDirection();
        direction.setRotate(rotate);
        beginTransaction.replace(R.id.directionlayout, direction, "direction");
        beginTransaction.commit();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
