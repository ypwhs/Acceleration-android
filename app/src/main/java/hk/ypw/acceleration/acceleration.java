package hk.ypw.acceleration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ypw.acceleration.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.makeText;

public class acceleration extends Activity {
    /*
     * @author ypw
     * 2014-09
     */
    int accdelay = 1000, gpsdelay = 3000, gyrodelay = 1000, magdelay = 1000, oridelay = 1000;
    int wifidelay = 1000;
    boolean isLogging = false;
    String filepath = Environment.getExternalStorageDirectory() + "/Acceleration/output.txt";
    /*
     * (acc代表加速度,gps代表GPS,gyro代表陀螺仪,ori代表方向传感器)
	 * 上面的三个delay分别控制三个传感器的速度,然后filepath代表的是保存的文件路径
	 */

    long nowtimeacc = 0, lasttimeacc = SystemClock.elapsedRealtime();
    long nowtimegps = 0, lasttimegps = SystemClock.elapsedRealtime();
    long nowtimegyro = 0, lasttimegyro = SystemClock.elapsedRealtime();
    long nowtimemag = 0, lasttimemag = SystemClock.elapsedRealtime();
    long nowtimeori = 0, lasttimeori = SystemClock.elapsedRealtime();

	/*
     * 这些是做延迟处理的参数,利用时间戳的差来进行延迟处理
	 */

    public void toggle(View view) {
        ToggleButton tb = (ToggleButton) view;
        isLogging = tb.isChecked();
    }

    Toast toast;

    public void show(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(),
                            str, Toast.LENGTH_SHORT);
                }
                toast.setText(str);
                toast.show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration);

        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            show("找不到SD卡");
            finish();
        }
        /*
         * 检测SD卡是否存在,如果不存在就直接退出,因为程序采集的信息必须保存到SD卡里
		 */


        String SDPATH = Environment.getExternalStorageDirectory() + "/";
        File dir = new File(SDPATH + "Acceleration");
        dir.mkdir();
        /*
		 * 如果文件夹Acceleration不存在就创建一个
		 */


        File file = new File(filepath);
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(System.currentTimeMillis() + ":System start." + "\r\n");
            writer.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		/*
		 * 写一个当前时间戳和System start.
		 * 比如:1411137516765:System start.
		 */

        SensorManager sensorManager;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                nowtimeacc = SystemClock.elapsedRealtime();
				/*
				 * 创建一个加速度sensor,监听sensor改变的事件
				 * 然后取出x,y,z,进行处理之后显示在屏幕上
				 */
                float x = event.values[0], y = event.values[1], z = event.values[2];
                String output = "";
                String output2 = "";
                output += String.format(Locale.CHINA, "AccX=%.2f\r\n", x);
                output += String.format(Locale.CHINA, "AccX=%.2f\r\n", y);
                output += String.format(Locale.CHINA, "AccX=%.2f\r\n", z);

                if (nowtimeacc - lasttimeacc >= accdelay) {
					/*
					 * 取出现在的时间和上一次写入文件的时间点的时间差,如果大于accdelay那么就重新赋值并写入数据到文件中
					 * 格式如下:时间戳,acc,x,y,z
					 * 例子:1411137519350,acc,0.08244324,0.1593628,9.890015
					 */
                    lasttimeacc = nowtimeacc;
                    output2 += System.currentTimeMillis() + ",acc," + x + "," + y + "," + z + "\r\n";
                    appendText(filepath, output2);
                }

                TextView outpuTextView = (TextView) findViewById(R.id.textview_acc);
                outpuTextView.setText(output);
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, accSensor, SensorManager.SENSOR_DELAY_GAME);
		/*
		 * 注册为游戏模式(SENSOR_DELAY_GAME),这样的采样速度是最快的
		 */

        Sensor gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
				/*
				 * 陀螺仪和加速度传感类似,这里是一样的写法
				 */
                nowtimegyro = SystemClock.elapsedRealtime();
                float x = event.values[0], y = event.values[1], z = event.values[2];
                String output = "";
                String output2 = "";
                output += String.format(Locale.CHINA, "GyroX=%.2f\r\n", x);
                output += String.format(Locale.CHINA, "GyroX=%.2f\r\n", y);
                output += String.format(Locale.CHINA, "GyroX=%.2f\r\n", z);

                if (nowtimegyro - lasttimegyro >= gyrodelay) {
                    lasttimegyro = nowtimegyro;
                    output2 += System.currentTimeMillis() + ",gyro," + x + "," + y + "," + z + "\r\n";
					/*
					 * 格式:时间戳,gyro,x,y,z
					 * 例子:1411137519351,gyro,-0.0019989014,0.0012512207,-0.0011444092
					 * 由于大部分时间我们都没有旋转,角速度为0,所以通常陀螺仪的数据都非常小
					 */
                    appendText(filepath, output2);
                }

                TextView outpuTextView = (TextView) findViewById(R.id.textview_gyro);
                outpuTextView.setText(output);

            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, gyroSensor, SensorManager.SENSOR_DELAY_GAME);

        Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
				/*
				 * 磁场和加速度传感类似,这里是一样的写法
				 */
                nowtimeori = SystemClock.elapsedRealtime();

                float x = event.values[0], y = event.values[1], z = event.values[2];
                String output = "";
                String output2 = "";
                output += String.format(Locale.CHINA, "MagX=%.2f\r\n", x);
                output += String.format(Locale.CHINA, "MagX=%.2f\r\n", y);
                output += String.format(Locale.CHINA, "MagX=%.2f\r\n", z);

                if (nowtimemag - lasttimemag >= magdelay) {
                    lasttimemag = nowtimemag;
                    output2 += System.currentTimeMillis() + ",mag," + x + "," + y + "," + z + "\r\n";
                    appendText(filepath, output2);
                }

                TextView outpuTextView = (TextView) findViewById(R.id.textview_mag);
                outpuTextView.setText(output);

            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, magSensor, SensorManager.SENSOR_DELAY_GAME);

        Sensor oriSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
				/*
				 * 方向传感器,这里也是一样的写法
				 */
                nowtimemag = SystemClock.elapsedRealtime();

                float x = event.values[0], y = event.values[1], z = event.values[2];
                String output = "";
                String output2 = "";
                output += String.format(Locale.CHINA, "OriX=%.2f\r\n", x);
                output += String.format(Locale.CHINA, "OriX=%.2f\r\n", y);
                output += String.format(Locale.CHINA, "OriX=%.2f\r\n", z);

                if (nowtimeori - lasttimeori >= oridelay) {
                    lasttimeori = nowtimeori;
                    output2 += System.currentTimeMillis() + ",ori," + x + "," + y + "," + z + "\r\n";
                    appendText(filepath, output2);
                }

                TextView outpuTextView = (TextView) findViewById(R.id.textview_ori);
                outpuTextView.setText(output);

            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, oriSensor, SensorManager.SENSOR_DELAY_GAME);


        openGPSSetting();
		/*
		 * 这里调用了一个函数来判断GPS是否被打开,如果没有打开我们就弹出设置让用户来手动打开GPS
		 */

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
				/*
				 * 这里我们注册了一个当位置变化的时候的事件
				 */
                TextView gpsTextView = (TextView) findViewById(R.id.textview_gps);

				/*
				 * 下面输出了经纬度,如果时间超过了gpsdelay就在文件中添加GPS数据
				 * 时间戳,gps,经度,纬度
				 * 例子:1411137497047,gps,116.158073,39.71972
				 */
                nowtimegps = SystemClock.elapsedRealtime();
                if (location != null) {
                    gpsTextView.setText("经度(Longitude):" + location.getLongitude() + "\r\n纬度(Latitude):"
                            + location.getLatitude());
                    if (nowtimegps - lasttimegps > gpsdelay) {
                        lasttimegps = nowtimegps;
                        String output = System.currentTimeMillis() + ",gps," + location.getLongitude()
                                + "," + location.getLatitude() + "\r\n";
                        appendText(filepath, output);
                    }

                    // Location类的方法：
                    // getAccuracy():精度（ACCESS_FINE_LOCATION／ACCESS_COARSE_LOCATION）
                    // getAltitude():海拨
                    // getBearing():方位，行动方向
                    // getLatitude():纬度
                    // getLongitude():经度
                    // getProvider():位置提供者（GPS／NETWORK）
                    // getSpeed():速度
                    // getTime():时刻
                } else gpsTextView.setText("无法获取到GPS的位置信息");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		/*
		 * 二个参数是通知之间的最小时间间隔（单位微秒），第三个是通知之间最小的距离变化（单位米）。
		 */

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SystemClock.sleep(wifidelay);

                    if (isLogging) {
                        WifiManager wifiManager = (WifiManager) getApplicationContext()
                                .getSystemService(Context.WIFI_SERVICE);
                        wifiManager.startScan();
                        List<ScanResult> results = wifiManager.getScanResults();

                        String output2 = "";
                        for (int i = 0; i < results.size(); i++) {
                            ScanResult result = results.get(i);
                            output2 += String.format(Locale.CHINA, "%d,%s,%s,%d\n",
                                    System.currentTimeMillis(), result.SSID, result.BSSID, result.level);
                        }
                        appendText(filepath, output2);
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void scan(View view) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> results = wifiManager.getScanResults();

        String str = "";
        for (int i = 0; i < results.size(); i++) {
            ScanResult result = results.get(i);
            str += String.format(Locale.CHINA, "SSID: %s, RSSI: %d\n", result.SSID, result.level);
        }
        show(str);
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public void appendText(String filepathString, String outputString) {
        if (isLogging) {
            try {
                final File file = new File(filepathString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.textview_filesize);
                        tv.setText("文件大小：" + readableFileSize(file.length()));
                    }
                });
                FileWriter writer;
                writer = new FileWriter(file, true);
                writer.write(outputString);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openGPSSetting() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            return;
        }
        makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();

        // 跳转到GPS的设置页面
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
    }

    public void openthefile(View v) {
        Uri uri = Uri.parse("file://" + filepath);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "text/html");
        startActivity(intent);
    }

    public void clear(View v) {
        try {
            File file = new File(filepath);
            Writer writer = new FileWriter(file);
            writer.write(System.currentTimeMillis() + ":System start." + "\r\n");
            writer.close();

            TextView tv = (TextView) findViewById(R.id.textview_filesize);
            tv.setText("文件大小：" + readableFileSize(file.length()));

            show("已清空之前的数据。");
        } catch (IOException e) {
            show(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            SharedPreferences sp = getSharedPreferences("data", 0);
            String accString = sp.getString("acc", "1000");
            String gpsString = sp.getString("gps", "3000");
            String gyroString = sp.getString("gyro", "1000");
            String magString = sp.getString("mag", "1000");
            String oriString = sp.getString("ori", "1000");
            String wifiString = sp.getString("wifi", "1000");


            String filenameString = sp.getString("filename", "output.txt");

            accdelay = Integer.valueOf(accString);
            gpsdelay = Integer.valueOf(gpsString);
            gyrodelay = Integer.valueOf(gyroString);
            magdelay = Integer.valueOf(magString);
            oridelay = Integer.valueOf(oriString);
            wifidelay = Integer.valueOf(wifiString);

            filepath = Environment.getExternalStorageDirectory() + "/Acceleration/" + filenameString;

            TextView settingTextView = (TextView) findViewById(R.id.textview_settings);
            settingTextView.setText(String.format(Locale.CHINA,
                    "加速度:%dms\r\nGPS:%dms\r\n陀螺仪:%dms\r\n磁场:%dms\r\n欧拉角:%dms\r\nWiFi:%dms\r\n文件地址:%s",
                    accdelay, gpsdelay, gyrodelay, magdelay, oridelay, wifidelay, filepath));

        } catch (Exception ignored) {
        }

		/*
		 * 上面的都是获取配置信息,filepath就是保存的文件路径,然后延时数据都有默认值1000/3000等
		 */
    }

    @Override
    public void onDestroy() {
        FileWriter writer;
        try {
            writer = new FileWriter(new File(filepath), true);
            writer.write(System.currentTimeMillis() + ":System stop." + "\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
        super.onDestroy();
    }

    public void quit(View v) {
        onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.acceleration, menu);
        return true;
    }

    Activity thisActivity = this;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            if (item.getItemId() == R.id.about) {
                Dialog alertDialog = new AlertDialog.Builder(this).
                        setTitle("开发者").
                        setMessage("杨培文").
                        setIcon(R.drawable.ic_launcher).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).
                        create();
                alertDialog.show();
            } else if (item.getItemId() == R.id.settings) {
                Intent intent = new Intent();
                intent.setClass(thisActivity, settings.class);
                startActivity(intent);
            }
        } catch (Exception ex) {
            show(ex.toString());
        }
        return false;
    }
}