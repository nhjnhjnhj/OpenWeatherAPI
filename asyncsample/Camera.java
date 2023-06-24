package com.websarva.wings.android.asyncsample;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 『Androidアプリ開発の教科書』
 * 第15章
 * カメラアプリ連携サンプル
 *
 * アクティビティクラス。
 *
 * @author Shinzo SAITO
 */
public class Camera extends AppCompatActivity {
	/**
	 * 保存された画像のURI。
	 */
	private Uri _imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 親クラスの同名メソッドの呼び出し。
		super.onActivityResult(requestCode, resultCode, data);
		// カメラアプリからの戻りでかつ撮影成功の場合
		if(requestCode == 200 && resultCode == RESULT_OK) {
			// 撮影された画像のビットマップデータを取得。
//			Bitmap bitmap = data.getParcelableExtra("data");
			// 画像を表示するImageViewを取得。
			ImageView ivCamera = findViewById(R.id.ivCamera);
			// 撮影された画像をImageViewに設定。
//			ivCamera.setImageBitmap(bitmap);
			// フィールドの画像URIをImageViewに設定。
			ivCamera.setImageURI(_imageUri);
		}
	}

	/**
	 * 画像部分がタップされたときの処理メソッド。
	 */
	public void onCameraImageClick(View view) {
		// 日時データを「yyyyMMddHHmmss」の形式に整形するフォーマッタを生成。
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 現在の日時を取得。
		Date now = new Date(System.currentTimeMillis());
		// 取得した日時データを「yyyyMMddHHmmss」形式に整形した文字列を生成。
		String nowStr = dateFormat.format(now);
		// ストレージに格納する画像のファイル名を生成。ファイル名の一意を確保するためにタイムスタンプの値を利用。
		String fileName = "CameraIntentSamplePhoto_" + nowStr +".jpg";

		// ContentValuesオブジェクトを生成。
		ContentValues values = new ContentValues();
		// 画像ファイル名を設定。
		values.put(MediaStore.Images.Media.TITLE, fileName);
		// 画像ファイルの種類を設定。
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

		// ContentResolverオブジェクトを生成。
		ContentResolver resolver = getContentResolver();
		// ContentResolverを使ってURIオブジェクトを生成。
		_imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		// Intentオブジェクトを生成。
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Extra情報として_imageUriを設定。
		intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);
		// アクティビティを起動。
		startActivityForResult(intent, 200);
	}
}
