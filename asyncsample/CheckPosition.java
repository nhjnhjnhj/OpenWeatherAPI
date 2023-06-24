package com.websarva.wings.android.asyncsample;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.Calendar;

public class CheckPosition extends AppCompatActivity{

    //場所の表示を行う部品
    private TextView placeparts;
    //時刻の表示を行う部品
    private TextView time;

    //画面内のボタン部品
    private Button language;
    private Button update;
    private Button recommendplace;
    private Button left_transfer;
    private Button right_transfer;
    private Button mail;
    private Button camera;

    //天気の名前
    String[] WN = new String[20];

    //Intentで送られてきた場所の情報
    String[] place = new String[5];
    int i = 0;

    //現在時刻を保存するラベル
    private String currrent;

    //OpenWeatherAPIで情報を取得するための変数
    private String q;

    // 緯度
    private String latitude = "";
    // 経度
    private String longitude = "";
    // 都市名
    private String cityName = "";
    // 天気
    private String weather = "";

    // 天気の詳細情報を表示する文字列を生成。
    String desc;

    //緯度と経度を送るための部品
    private String[] location = new String[4];

    //OpenWeatherAPIで情報を取得する時の言語設定を行うための変数
    int lang = 0;
    //日本語=0、英語=1にしよう


    /**
     * ログに記載するタグ用の文字列。
     */
    private static final String DEBUG_TAG = "asyncsample";
    /**
     * お天気情報のURL。
     */
    //日本語用
    private static final String WEATHERINFO_URL_J = "https://api.openweathermap.org/data/2.5/weather?lang=ja";
    //英語用
    private static final String WEATHERINFO_URL_E = "https://api.openweathermap.org/data/2.5/weather?lang=us";

    /**
     * お天気APIにアクセスすするためのAPIキー。
     * ※※※※※この値は各自のものに書き換える!!※※※※※
     */
    private static final String APP_ID = "2cd5fe528a2091307f0e9a4570f6d618";

    //大本の部分
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_position);

        //Intentで情報取得
        placeparts = findViewById(R.id.placename);
        Intent intent = getIntent();
        place = intent.getStringArrayExtra("place_info");


        //ボタン作成
        language = findViewById(R.id.language);
        update = findViewById(R.id.Lupdate);
        recommendplace = findViewById(R.id.recommendplaceL);
        right_transfer = findViewById(R.id.rightside);
        left_transfer = findViewById(R.id.leftside);
        mail = findViewById(R.id.mailL);
        camera = findViewById(R.id.cameraL);

        // 天気情報を表示するTextViewを取得。
        TextView tvWeatherDESC = findViewById(R.id.weathername);

        //現在時刻の表示
        // Calendarクラスのインスタンス取得
        Calendar cal = Calendar.getInstance();
        // 時を取得
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        // 分を取得
        int mm = cal.get(Calendar.MINUTE);
        //表示するための部品を取得
        time = findViewById(R.id.now);
        currrent = (hh + ":" + mm);
        time.setText(currrent);

        //ボタンの機能割り当て
        //言語切り替え
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lang == 0){
                    //英語に変化
                    lang++;
                    String urlFull = WEATHERINFO_URL_J + "&q=" + q + "&appid=" + APP_ID;
                    receiveWeatherInfo(urlFull);
                }else if(lang == 1){
                    lang--;
                    String urlFull = WEATHERINFO_URL_E + "&q=" + q + "&appid=" + APP_ID;
                    receiveWeatherInfo(urlFull);
                }
            }
        });
        //天候情報と現在時刻を更新する
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calendarクラスのインスタンス取得
                Calendar cal_update = Calendar.getInstance();
                // 時を取得
                int hh = cal_update.get(Calendar.HOUR_OF_DAY);
                // 分を取得
                int mm = cal_update.get(Calendar.MINUTE);
                //表示するための部品を取得
                time = findViewById(R.id.now);
                currrent = (hh + ":" + mm);
                time.setText(currrent);

                //天候情報を表示する
                q = place[i];
                if(lang == 0){
                    String urlFull = WEATHERINFO_URL_E + "&q=" + q + "&appid=" + APP_ID;
                    receiveWeatherInfo(urlFull);
                }else if(lang == 1){
                    String urlFull = WEATHERINFO_URL_J + "&q=" + q + "&appid=" + APP_ID;
                    receiveWeatherInfo(urlFull);
                }
            }
        });

        //おすすめの見学ポイント
        recommendplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //都市名
                location[0] = cityName;
                // 緯度
                location[1] = latitude;
                // 経度
                location[2] = longitude;
                // 天気
                location[3] = weather;


                Intent intent = new Intent(CheckPosition.this , FeatureAppointment.class);
                intent.putExtra("location_info",location);

                startActivity(intent);

                finish();
            }
        });



        //0から4の順
        right_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if(i > 4){
                    i = 0;
                    //場所名を表示する
                    placeparts.setText(place[i]);

                    //天候情報を表示する
                    q = place[i];
                    if(lang == 0){
                        String urlFull = WEATHERINFO_URL_E + "&q=" + q + "&appid=" + APP_ID;
                        receiveWeatherInfo(urlFull);
                    }else if(lang == 1){
                        String urlFull = WEATHERINFO_URL_J + "&q=" + q + "&appid=" + APP_ID;
                        receiveWeatherInfo(urlFull);
                    }
                }else{
                    //場所名を表示する
                    placeparts.setText(place[i]);

                    //天候情報を表示する
                    q = place[i];
                    if(lang == 0){
                        String urlFull = WEATHERINFO_URL_E + "&q=" + q + "&appid=" + APP_ID;
                        receiveWeatherInfo(urlFull);
                    }else if(lang == 1){
                        String urlFull = WEATHERINFO_URL_J + "&q=" + q + "&appid=" + APP_ID;
                        receiveWeatherInfo(urlFull);
                    }
                }
            }
        });

        //4から0の順
        left_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i--;
                if(i < 0){
                    i = 4;
                    //場所名を表示する
                    placeparts.setText(place[i]);

                    //天候情報を表示する
                    q = place[i];
                    if(lang == 0){
                        String urlFull = WEATHERINFO_URL_E + "&q=" + q + "&appid=" + APP_ID;
                        receiveWeatherInfo(urlFull);
                    }else if(lang == 1){
                        String urlFull = WEATHERINFO_URL_J + "&q=" + q + "&appid=" + APP_ID;
                        receiveWeatherInfo(urlFull);
                    }
                }else{
                    //場所名を表示する
                    placeparts.setText(place[i]);

                    //天候情報を表示する
                    q = place[i];
                    if(lang == 0){
                        String urlFull = WEATHERINFO_URL_E + "&q=" + q + "&appid=" + APP_ID;
                        receiveWeatherInfo(urlFull);
                    }else if(lang == 1){
                        String urlFull = WEATHERINFO_URL_J + "&q=" + q + "&appid=" + APP_ID;
                        receiveWeatherInfo(urlFull);
                    }
                }
            }
        });

        //メール機能
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckPosition.this , Gmail.class);

                startActivity(intent);

                finish();
            }
        });
        //カメラ機能
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckPosition.this , Camera.class);

                startActivity(intent);

                finish();
            }
        });

        //初期表示
        if(i == 0){
            //場所名を表示する
            placeparts.setText(place[i]);

            //天候情報を表示する
            q = place[i];
            String urlFull = WEATHERINFO_URL_E + "&q=" + q + "&appid=" + APP_ID;
            receiveWeatherInfo(urlFull);
        }
    }


    /**
     * お天気情報の取得処理を行うメソッド。
     *
     * @param urlFull お天気情報を取得するURL。
     */
    @UiThread
    private void receiveWeatherInfo(final String urlFull) {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);
        WeatherInfoBackgroundReceiver backgroundReceiver = new WeatherInfoBackgroundReceiver(handler, urlFull);
        ExecutorService executorService  = Executors.newSingleThreadExecutor();
        executorService.submit(backgroundReceiver);
    }

    /**
     * 非同期でお天気情報APIにアクセスするためのクラス。
     */
    private class WeatherInfoBackgroundReceiver implements Runnable {
        /**
         * ハンドラオブジェクト。
         */
        private final Handler _handler;
        /**
         * お天気情報を取得するURL。
         */
        private final String _urlFull;

        /**
         * コンストラクタ。
         * 非同期でお天気情報Web APIにアクセスするのに必要な情報を取得する。
         *
         * @param handler ハンドラオブジェクト。
         * @param urlFull お天気情報を取得するURL。
         */
        public WeatherInfoBackgroundReceiver(Handler handler , String urlFull) {
            _handler = handler;
            _urlFull = urlFull;
        }

        @WorkerThread
        @Override
        public void run() {
            // HTTP接続を行うHttpURLConnectionオブジェクトを宣言。finallyで解放するためにtry外で宣言。
            HttpURLConnection con = null;
            // HTTP接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言。同じくtry外で宣言。
            InputStream is = null;
            // 天気情報サービスから取得したJSON文字列。天気情報が格納されている。
            String result = "";
            try {
                // URLオブジェクトを生成。
                URL url = new URL(_urlFull);
                // URLオブジェクトからHttpURLConnectionオブジェクトを取得。
                con = (HttpURLConnection) url.openConnection();
                // 接続に使ってもよい時間を設定。
                con.setConnectTimeout(1000);
                // データ取得に使ってもよい時間。
                con.setReadTimeout(1000);
                // HTTP接続メソッドをGETに設定。
                con.setRequestMethod("GET");
                // 接続。
                con.connect();
                // HttpURLConnectionオブジェクトからレスポンスデータを取得。
                is = con.getInputStream();
                // レスポンスデータであるInputStreamオブジェクトを文字列に変換。
                result = is2String(is);
            }
            catch(MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            }
            // タイムアウトの場合の例外処理。
            catch(SocketTimeoutException ex) {
                Log.w(DEBUG_TAG, "通信タイムアウト", ex);
            }
            catch(IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            }
            finally {
                // HttpURLConnectionオブジェクトがnullでないなら解放。
                if(con != null) {
                    con.disconnect();
                }
                // InputStreamオブジェクトがnullでないなら解放。
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch(IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                    }
                }
            }
            WeatherInfoPostExecutor postExecutor = new WeatherInfoPostExecutor(result);
            _handler.post(postExecutor);
        }

        /**
         * InputStreamオブジェクトを文字列に変換するメソッド。 変換文字コードはUTF-8。
         *
         * @param is 変換対象のInputStreamオブジェクト。
         * @return 変換された文字列。
         * @throws IOException 変換に失敗した時に発生。
         */
        private String is2String(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            char[] b = new char[1024];
            int line;
            while(0 <= (line = reader.read(b))) {
                sb.append(b, 0, line);
            }
            return sb.toString();
        }
    }

    /**
     * 非同期でお天気情報を取得した後にUIスレッドでその情報を表示するためのクラス。
     */
    private class WeatherInfoPostExecutor implements Runnable {
        /**
         * 取得したお天気情報JSON文字列。
         */
        private final String _result;

        /**
         * コンストラクタ。
         *
         * @param result Web APIから取得したお天気情報JSON文字列。
         */
        public WeatherInfoPostExecutor(String result) {
            _result = result;
        }

        @UiThread
        @Override
        public void run() {

            try {
                // ルートJSONオブジェクトを生成。
                JSONObject rootJSON = new JSONObject(_result);
                // 都市名文字列を取得。
                cityName = rootJSON.getString("name");
                // 緯度経度情報JSONオブジェクトを取得。
                JSONObject coordJSON = rootJSON.getJSONObject("coord");
                // 緯度情報文字列を取得。
                latitude = coordJSON.getString("lat");
                // 経度情報文字列を取得。
                longitude = coordJSON.getString("lon");
                // 天気情報JSON配列オブジェクトを取得。
                JSONArray weatherJSONArray = rootJSON.getJSONArray("weather");
                // 現在の天気情報JSONオブジェクトを取得。
                JSONObject weatherJSON = weatherJSONArray.getJSONObject(0);
                // 現在の天気情報文字列を取得。
                weather = weatherJSON.getString("description");
            }
            catch(JSONException ex) {
                Log.e(DEBUG_TAG, "JSON解析失敗", ex);
            }


            // 天気情報を表示するTextViewを取得。
            TextView tvWeatherDESC = findViewById(R.id.weathername);

            // 天気の詳細情報を表示する文字列
            String DESC_OWA = weather;

            //天気イラスト表示
            ImageView imageView = findViewById(R.id.weatherImage);
            AssetManager assets = getResources().getAssets();

            //天気の名前
            WN[0] = "clear sky";
            WN[1] = "few clouds";
            WN[2] = "scattered clouds";
            WN[3] = "broken clouds";
            WN[4] = "shower rain";
            WN[5] = "rain";
            WN[6] = "thunderstorm";
            WN[7] = "snow";
            WN[8] = "mist";
            WN[9] = "light snow";
            WN[10] = "light shower snow";
            WN[11] = "scattered clouds";


            if(weather.equals(WN[0])){
                try (InputStream istream = assets.open("clearsky.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[1])){
                try (InputStream istream = assets.open("fewclouds.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[2])){
                try (InputStream istream = assets.open("scatteredclouds.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[3])){
                try (InputStream istream = assets.open("brokenclouds.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[4])){
                try (InputStream istream = assets.open("showerrain.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[5])){
                try (InputStream istream = assets.open("rain.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[6])){
                try (InputStream istream = assets.open("thunderstorm.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[7])){
                try (InputStream istream = assets.open("snow.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[8])){
                try (InputStream istream = assets.open("mist.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[9])){
                try (InputStream istream = assets.open("snow.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[10])){
                try (InputStream istream = assets.open("lightshowersnow.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(weather.equals(WN[11])){
                try (InputStream istream = assets.open("scatteredclouds.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 天気情報を表示。
            if (place[i].equals("")){
                placeparts.setText("No input");
                desc = "No input";
                tvWeatherDESC.setText(desc);
                try (InputStream istream = assets.open("Noinput.png")){
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                tvWeatherDESC.setText(DESC_OWA);
            }


        }
    }

}