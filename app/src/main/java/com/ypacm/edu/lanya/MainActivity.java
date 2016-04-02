package com.ypacm.edu.lanya;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
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
    BluetoothAdapter mBluetoothAdapter;
    List<DeviceBean> itemBeanList = new ArrayList<>();
    Math math;
    int flag = 0;
    float rotate = -45;

    private double Pi = Math.acos(-1);
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
    float locationX = 0;
    float locationY = 0;
    float showX = 0;
    float showY = 0;
    double tileMap[][];

    float displayWidth;
    float displayHeigth;
    FragmentMap fragment = new FragmentMap();
    FragmentLocation location = new FragmentLocation();
    FragmentDirection fragmentDirection = new FragmentDirection();
    float density;


    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowManager wm = this.getWindowManager();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        density = dm.density;
        displayWidth = widthPixels / density;
        displayHeigth = heightPixels / density;
        myThis = this;

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            finish();
            return;
        }

        initSensor();
        UIDirection();
        initBluetooth();
        UIUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        scanLeDevice(true);
    }


    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "duangduangduang");
                            String remoteDeviceName = device.getName();
                            String remoteDevice = device.getAddress();
                            short remoteDeviceRssi = (short) rssi;
                            double remoteDevicedis = calcDis(remoteDeviceRssi);

                            boolean liveFlag = false;
                            for (DeviceBean bean : itemBeanList) {
                                if (bean.DeviceAddress.equals(device.getAddress())) {
                                    liveFlag = true;
                                    bean.DeviceRSSI = (short) rssi;
                                    bean.DeviceDis = remoteDevicedis;
                                    bean.count++;
                                    bean.sumDis += remoteDevicedis;
                                    if (remoteDevicedis > bean.maxnDis)
                                        bean.maxnDis = remoteDevicedis;
                                    if (remoteDevicedis < bean.minnDis)
                                        bean.minnDis = remoteDevicedis;

                                }
                            }
//                            if (liveFlag == false)
//                                itemBeanList.add(new DeviceBean(remoteDeviceName, remoteDevice, remoteDeviceRssi, remoteDevicedis, 0, 0,1,remoteDevicedis,remoteDevicedis,remoteDevicedis));
                            int effectiveSignalNum = 0;
                            for (DeviceBean bean : itemBeanList) {
                                if (bean.count >= 3) {
                                    effectiveSignalNum++;
                                }
                            }
                            if (effectiveSignalNum >= 3) {
                                calc();
                            }
                        }
                    });
                }
            };

    private void UIDirection() {
        //指南方向图标
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.add(R.id.directionlayout, fragmentDirection, "direction");
        beginTransaction.commit();
    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mag_sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void initBluetooth() {
        String[] bluetoothDevice = getResources().getStringArray(R.array.bluetooth_device);
        Log.i(TAG, "" + bluetoothDevice.length);
        for (int i = 1; i <= bluetoothDevice.length; i++) {
            if (bluetoothDevice[i - 1].equals("7C:EC:79:E0:BD:CA")) {
                itemBeanList.add(new DeviceBean("icon" + i, bluetoothDevice[i - 1], (short) 0, 0, 13, 38, 0, 0, 0, 1000));//507

            } else if (bluetoothDevice[i - 1].equals("7C:EC:79:E0:BD:BA")) {
                itemBeanList.add(new DeviceBean("icon" + i, bluetoothDevice[i - 1], (short) 0, 0, 48, 31, 0, 0, 0, 1000));//506东
            } else if (bluetoothDevice[i - 1].equals("7C:EC:79:E0:BB:67")) {
                itemBeanList.add(new DeviceBean("icon" + i, bluetoothDevice[i - 1], (short) 0, 0, 40, 38, 0, 0, 0, 1000));//503
            } else if (bluetoothDevice[i - 1].equals("D0:B5:C2:C1:92:6B")) {
                itemBeanList.add(new DeviceBean("icon" + i, bluetoothDevice[i - 1], (short) 0, 0, 20, 31, 0, 0, 0, 1000));//508东
            } else if (bluetoothDevice[i - 1].equals("7C:EC:79:E0:BB:75")) {
                itemBeanList.add(new DeviceBean("icon" + i, bluetoothDevice[i - 1], (short) 0, 0, 27, 31, 0, 0, 0, 1000));//506西
            }
        }

        Log.i(TAG, mBluetoothAdapter.getAddress());
        Log.i(TAG, mBluetoothAdapter.getName());
    }

    public void UIUpdate() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        if (SHOW_MODE == SHOW_DATA) {
            FragmentList fragment = new FragmentList();
            fragment.setData(itemBeanList);
            beginTransaction.add(R.id.fragmentlayout, fragment, "Fragment");
        } else {
            beginTransaction.add(R.id.fragmentlayout, fragment, "map");
            location.upDateLocation(rotate - 45, 50, 50);
            beginTransaction.add(R.id.locationlayout, location, "location");
        }
        beginTransaction.commit();
        Log.i(TAG, "提交成功");
    }

    private void calc() {
        //计算locationX，locationY
        int tempX1 = 0, tempY1 = 0;
        int tempX2 = 0, tempY2 = 0;
        Toast.makeText(MainActivity.myThis, "开始计算位置", Toast.LENGTH_SHORT).show();
        tileMap = new double[100][100];
        int tileX, tileY;
        for (DeviceBean bean : itemBeanList) {
            if (bean.count >= 3) {
                bean.DeviceDis = (bean.sumDis - bean.maxnDis - bean.minnDis) / (bean.count - 2);
                for (int angle = 0; angle < 360; angle += 2) {
                    for (int dis = 0; dis <= 5; dis++) {

                        if (bean.DeviceDis + dis <= 8) {
                            tileX = bean.X + (int) (Math.sin(angle / 180 * Pi) * (bean.DeviceDis + dis) / 0.8);
                            tileY = bean.Y + (int) (Math.cos(angle / 180 * Pi) * (bean.DeviceDis + dis) / 0.3);
                            if (tileX >= 0 && tileX < 100 && tileY >= 0 && tileY < 100) {
                                if (tileX == tempX1 && tileY == tempY1)
                                    continue;
                                tempX1 = tileX;
                                tempY1 = tileY;
                                tileMap[tileX][tileY] += 1.0 - ((float) dis) / 5.0;
                            }
                        }

                        if (bean.DeviceDis - dis > 0) {
                            tileX = bean.X + (int) (Math.sin(angle / 180 * Pi) * (bean.DeviceDis - dis) / 0.8);
                            tileY = bean.Y + (int) (Math.cos(angle / 180 * Pi) * (bean.DeviceDis - dis) / 0.4);
                            if (tileX >= 0 && tileX < 100 && tileY >= 0 && tileY < 100) {
                                if (tileX == tempX2 && tileY == tempY2)
                                    continue;
                                tempX2 = tileX;
                                tempY2 = tileY;
                                tileMap[tileX][tileY] += 1.0 - ((float) dis) / 5.0;
                            }
                        }
                    }
                }
                bean.count = 0;
                bean.maxnDis = 0;
                bean.minnDis = 1000;
            }
        }
        double maxn = 0;
        int tempShowX = 0;
        int tempShowY = 0;
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (tileMap[i][j] > maxn) {
                    maxn = tileMap[i][j];
                    tempShowX = i;
                    tempShowY = j;
                }
            }
        }
        locationX = tempShowX;
        locationY = tempShowY;
        Toast.makeText(MainActivity.myThis, "位置改变，X= " + locationX + " Y= " + locationY, Toast.LENGTH_SHORT).show();

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
                    scanLeDevice(true);
                } else {
                    Toast.makeText(getBaseContext(), "停止广播", Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_action_close);
                    flag = 0;
                    scanLeDevice(false);
                }
                return true;
            }
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

        locationCalc();
        location.upDateLocation(rotate - 45, showX, showY);

    }

    private void locationCalc() {

        float[] matrixValues = new float[9];
        Matrix tempMatrix = fragment.getMatrix();
        tempMatrix.getValues(matrixValues);
        BitmapFactory.Options ops = fragment.getImageSize();
        showX = (locationX * (ops.outWidth * matrixValues[0] * density) / 100.0f + matrixValues[2]);
        showY = (locationY * (ops.outHeight * matrixValues[4] * density) / 100.0f + matrixValues[5]);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
