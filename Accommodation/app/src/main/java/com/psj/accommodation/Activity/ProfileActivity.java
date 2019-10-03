package com.psj.accommodation.Activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

	public static final String TAG = "ProfileActivity";
	public static final int PROFILE_REQUEST_CODE = 8000;

	ImageView profileImage, profileBack;
	TextView profileName;
	Button profileSaveBtn;

	String sessionEmail = "";
	String sessionName = "";
	String ServerImagePath = "";
	String gallaryImagePath = "";


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		Log.i(TAG, "onCreate : 실행");

		profileImage = findViewById(R.id.profileImage);
		profileName = findViewById(R.id.profileName);
		profileBack = findViewById(R.id.profileBack);
		profileSaveBtn = findViewById(R.id.profileSaveBtn);

		profileImage.setBackground(new ShapeDrawable(new OvalShape()));
		if (Build.VERSION.SDK_INT >= 21) {
			profileImage.setClipToOutline(true);
		}

		// 쉐어드에 저장된 정보 가져오기 (이름, 이메일)
		getShard();

		getProfileData();

		profileName.setText(sessionName);


	}


	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume : 실행");

		profileBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});

		profileSaveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "프로필 정보 저장 버튼 클릭!!");

				uploadToServer(gallaryImagePath);

			}
		});


		profileImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "프로필 이미지 수정..");

				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, PROFILE_REQUEST_CODE);
			}
		});


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PROFILE_REQUEST_CODE) {

			Log.i(TAG, "갤러리 접근 후 돌아왔어..");

			if (resultCode == RESULT_OK) {

				Log.i(TAG, "사진을 선택하고 왔어..");
				Log.i(TAG, "갤러리에서 가져온 사진 경로 : " + data.getDataString());
				gallaryImagePath = getPath(this, data.getData());

				Glide.with(this).load(data.getDataString()).centerCrop().into(profileImage);

			} else {

				Log.i(TAG, "사진을 선택하지 않고 왔어..");

			}

		}
	} // onActivityResult 끝


	// 서버에 이미지 업로드 기능
	private void uploadToServer(String filePath) {

		Log.i(TAG, "uploadToServer : 실행");

		Log.i(TAG, "gallaryImagePath : " + gallaryImagePath);

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		Log.i(TAG, "filePath : " + filePath);

		//Create a file object using file path
		File file = new File(filePath);
		Log.i(TAG, "file : " + file);

		// Create a request body with file and image media type
		RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
		Log.i(TAG, "fileReqBody : " + fileReqBody);


		// Create MultipartBody.Part using file request-body,file name and part name
		MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
		Log.i(TAG, "part : " + part);


		//Create request body with text description and text media type
		RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
		Log.i(TAG, "description : " + description);

		// 인터페이스 ApiService에 선언한 uploadImage()를 호출합니다
		Call<String> call = apiService.uploadImage(part, description, sessionName, sessionEmail);
		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				Log.i(TAG, "onResponse : 실행");
				Log.i(TAG, "서버에서 받은 응답 값 : " + response.body().toString());
				ServerImagePath += response.body().toString();
				Toast.makeText(ProfileActivity.this,"프로필 정보 저장되었습니다",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(Call<String> call, Throwable throwable) {
				Log.i("onFailure", "" + throwable);
				Toast.makeText(ProfileActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	} // 서버에 이미지 업로드 기능 끝

	// 쉐어드에 저장된 사용자 이름 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
		sessionEmail = sharedPreferences.getString("email", "noEmail");
	} // getShard 끝

	private void getProfileData() {

		Log.i(TAG, "getProfileData : 실행");

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 profileSelect()를 호출합니다
		Call<String> call = apiService.profileSelect(sessionEmail);

		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {

				Log.i(TAG, "onResponse : 실행");
				Log.i(TAG, "서버에서 받은 응답 값 : " + response.body().toString());

				if (response.body().equals("")){

					Log.i(TAG, "프로필 데이터 없다.");

				} else {

					String[] profileData = response.body().split(" ");

					profileImage.setBackground(new ShapeDrawable(new OvalShape()));
					if (Build.VERSION.SDK_INT >= 21) {
						profileImage.setClipToOutline(true);
					}

					Glide.with(ProfileActivity.this).load(Uri.parse("http://54.180.152.167/" + profileData[1])).centerCrop().into(profileImage);

				}

			}

			@Override
			public void onFailure(Call<String> call, Throwable throwable) {
				Log.i("onFailure", "" + throwable);
				Toast.makeText(ProfileActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}
		});


	}

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri     The Uri to query.
	 * @author paulburke
	 */
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

} // ChatActivity 클래스 끝
