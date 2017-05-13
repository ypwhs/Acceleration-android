package hk.ypw.acceleration;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ypw.acceleration.R;


public class settings extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        EditText acc = (EditText) findViewById(R.id.edit_acc);
        EditText gps = (EditText) findViewById(R.id.edit_gps);
        EditText gyro = (EditText) findViewById(R.id.edit_gyro);
        EditText mag = (EditText) findViewById(R.id.edit_mag);
        EditText ori = (EditText) findViewById(R.id.edit_ori);
        EditText wifi = (EditText) findViewById(R.id.edit_wifi);

        EditText filename = (EditText) findViewById(R.id.edit_filename);


        try {
            SharedPreferences sp = getSharedPreferences("data", 0);
            String accString = sp.getString("acc", "1000");
            String gpsString = sp.getString("gps", "3000");
            String gyroString = sp.getString("gyro", "1000");
            String magString = sp.getString("mag", "1000");
            String oriString = sp.getString("ori", "1000");
            String wifiString = sp.getString("wifi", "1000");

            String filenameString = sp.getString("filename", "output.txt");
            acc.setText(accString);
            gps.setText(gpsString);
            gyro.setText(gyroString);
            mag.setText(magString);
            ori.setText(oriString);
            wifi.setText(wifiString);

            filename.setText(filenameString);

        } catch (Exception ignored) {
        }
    }

    public void save(View v) {
        SharedPreferences.Editor sp = getSharedPreferences("data", 0).edit();

        EditText acc = (EditText) findViewById(R.id.edit_acc);
        EditText gps = (EditText) findViewById(R.id.edit_gps);
        EditText gyro = (EditText) findViewById(R.id.edit_gyro);
        EditText mag = (EditText) findViewById(R.id.edit_mag);
        EditText ori = (EditText) findViewById(R.id.edit_ori);
        EditText wifi = (EditText) findViewById(R.id.edit_wifi);
        EditText filename = (EditText) findViewById(R.id.edit_filename);

        String accString = acc.getText().toString();
        String gpsString = gps.getText().toString();
        String gyroString = gyro.getText().toString();
        String magString = mag.getText().toString();
        String oriString = ori.getText().toString();
        String wifiString = wifi.getText().toString();
        String filenameString = filename.getText().toString();

        sp.putString("acc", accString);
        sp.putString("gps", gpsString);
        sp.putString("gyro", gyroString);
        sp.putString("mag", magString);
        sp.putString("ori", oriString);
        sp.putString("wifi", wifiString);

        sp.putString("filename", filenameString);
        sp.apply();

        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
