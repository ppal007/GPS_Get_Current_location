package com.ppal007.gps_background;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ppal007.gps_background.services.GPS_Service;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button buttonStart,buttonStop;
    private TextView textView;

    private int flag = 0;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();

        if (broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    textView.append("\n"+ Objects.requireNonNull(intent.getExtras()).get("coordinates"));

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_updates"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart=findViewById(R.id.btnStart);
        buttonStop=findViewById(R.id.btnStop);
        textView=findViewById(R.id.tv_result);



        if (!runtime_permission()){
            enable_buttons();
        }

    }

    private void enable_buttons() {

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag == 0){
                    Intent intent = new Intent(getApplicationContext(), GPS_Service.class);
                    startService(intent);
                    flag = 1;
                }else {
                    Toast.makeText(MainActivity.this, "Already start!", Toast.LENGTH_SHORT).show();
                }


            }
        });


        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag != 0){
                    Intent intent = new Intent(getApplicationContext(),GPS_Service.class);
                    stopService(intent);

                    textView.setText("");
                    flag = 0;
                }else {
                    Toast.makeText(MainActivity.this, "Not start Yet!", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private boolean runtime_permission() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001);

            return true;

        }
        return false;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permission();
            }
        }
    }
}
