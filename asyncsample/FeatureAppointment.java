package com.websarva.wings.android.asyncsample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class FeatureAppointment extends AppCompatActivity {

    //場所の表示を行う部品
    private TextView cityparts;
    private TextView latitudeparts;
    private TextView longitudecityparts;

    //ボタン部品
    private Button googlemap;
    private Button restaurant;
    private Button amusement;
    private Button historicmonument;

    //Intentで送られてきた場所の情報
    private String[] location = new String[4];

    //Google Mapで使う情報の部品
    private String googlemaps;

    /**
     * 緯度フィールド。
     */
    private double _latitude = 0;
    /**
     * 経度フィールド
     */
    private double _longitude = 0;
    /**
     * FusedLocationProviderClientオブジェクトフィールド。
     */
    private FusedLocationProviderClient _fusedLocationClient;
    /**
     * LocationRequestオブジェクトフィールド。
     */
    private LocationRequest _locationRequest;
    /**
     * 位置情報が変更された時の処理を行うコールバックオブジェクトフィールド。
     */
    private OnUpdateLocation _onUpdateLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_appointment);

        //Intentで情報取得
        cityparts = findViewById(R.id.cityName);
        latitudeparts = findViewById(R.id.latitude);
        longitudecityparts = findViewById(R.id.longitude);
        Intent intent = getIntent();
        location = intent.getStringArrayExtra("location_info");

        //ボタン部品情報の取得
        restaurant = findViewById(R.id.see_restaurant);
        amusement = findViewById(R.id.see_amusement);
        historicmonument = findViewById(R.id.see_historicmonument);
        googlemap = findViewById(R.id.VGM);

        //Intentの情報を表示する
        cityparts.setText(location[0]);
        latitudeparts.setText(location[1]);
        longitudecityparts.setText(location[2]);

        //おすすめのレストラン表示
        googlemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googlemaps = ("geo:" + location[1] + "," + location[2] + "?z=14");

                Uri uri = Uri.parse(googlemaps);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        //おすすめのレストラン表示
        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googlemaps = ("geo:" + location[1] + "," + location[2] + "?q=restaurants");

                Uri uri = Uri.parse(googlemaps);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        //おすすめの遊園地表示
        amusement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googlemaps = ("geo:" + location[1] + "," + location[2] + "?q=amusementpark");

                Uri uri = Uri.parse(googlemaps);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        //おすすめの歴史的建造物表示
        historicmonument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googlemaps = ("geo:" + location[1] + "," + location[2] + "?q=Historic monuments");

                Uri uri = Uri.parse(googlemaps);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // FusedLocationProviderClientオブジェクトを取得。
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(FeatureAppointment.this);
        // LocationRequestオブジェクトを生成。
        _locationRequest = LocationRequest.create();
        // 位置情報の更新間隔を設定。
        _locationRequest.setInterval(5000);
        // 位置情報の最短更新間隔を設定。
        _locationRequest.setFastestInterval(1000);
        // 位置情報の取得精度を設定。
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // 位置情報が変更された時の処理を行うコールバックオブジェクトを生成。
        _onUpdateLocation = new OnUpdateLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ACCESS_FINE_LOCATIONの許可が下りていないなら…
        if (ActivityCompat.checkSelfPermission(FeatureAppointment.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ACCESS_FINE_LOCATIONの許可を求めるダイアログを表示。その際、リクエストコードを1000に設定。
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(FeatureAppointment.this, permissions, 1000);
            // onResume()メソッドを終了。
            return;
        }
        // 位置情報の追跡を開始。
        _fusedLocationClient.requestLocationUpdates(_locationRequest, _onUpdateLocation, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // ACCESS_FINE_LOCATIONに対するパーミションダイアログでかつ許可を選択したなら…
        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 再度ACCESS_FINE_LOCATIONの許可が下りていないかどうかのチェックをし、降りていないなら処理を中止。
            if (ActivityCompat.checkSelfPermission(FeatureAppointment.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // 位置情報の追跡を開始。
            _fusedLocationClient.requestLocationUpdates(_locationRequest, _onUpdateLocation, Looper.getMainLooper());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 位置情報の追跡を停止。
        _fusedLocationClient.removeLocationUpdates(_onUpdateLocation);
    }

    /**
     * 現在地の地図表示ボタンがタップされたときの処理メソッド。
     */
    public void onMapShowCurrentButtonClick(View view) {
        googlemaps = ("geo:" + _latitude + "," + _longitude + "?z=14");

        Uri uri = Uri.parse(googlemaps);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

        /*// フィールドの緯度と経度の値をもとにマップアプリと連携するURI文字列を生成。
        String uriStr = "geo:" + _latitude + "," + _longitude + "?z=16";
        // URI文字列からURIオブジェクトを生成。
        Uri uri = Uri.parse(uriStr);
        // Intentオブジェクトを生成。
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // アクティビティを起動。
        startActivity(intent);*/
    }

    /**
     * 位置情報が変更された時の処理を行うコールバッククラス。
     */
    private class OnUpdateLocation extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                // 直近の位置情報を取得。
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    // locationオブジェクトから緯度を取得。
                    _latitude = location.getLatitude();
                    // locationオブジェクトから経度を取得。
                    _longitude = location.getLongitude();
                   /* googlemaps = ("geo:" + _latitude + "," + _longitude + "?z=14");

                    Uri uri = Uri.parse(googlemaps);

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }*/
                }
            }
        }
    }
}
