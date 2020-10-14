package com.psj.welfare.api;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

	@FormUrlEncoded
	@POST("/backend/android/and_register.php")
	Call<String> googleUser(@Field("userPlatform") String userPlatform, @Field("userEmail") String userEmail, @Field("userName") String userName, @Field("userToken") String userToken);

	@FormUrlEncoded
	@POST("/backend/android/and_register.php")
	Call<String> naverUser(@Field("userPlatform") String userPlatform, @Field("userEmail") String userEmail, @Field("userName") String userName, @Field("userToken") String userToken);

	@FormUrlEncoded
	@POST("/backend/android/and_login.php")
	Call<String> ReUser(@Field("userEmail") String userEmail, @Field("userToken") String userToken);

	@FormUrlEncoded
	@POST("/backend/android/and_token_check.php")
	Call<String> tokenCheck(@Field("userEmail") String userEmail, @Field("localToken") String userToken);

	@FormUrlEncoded
	@POST("/backend/android/and_logout_check.php")
	Call<String> logoutUser(@Field("userEmail") String userEmail, @Field("localToken") String localToken);

	@FormUrlEncoded
	@POST("/backend/android/and_like.php")
	Call<String> UserFavor(@Field("userEmail") String userEmail, @Field("like") String like);

	@FormUrlEncoded
	@POST("/backend/android/and_detail.php")
	Call<JsonObject> detailData(@Field("be_name") String detail);

	@FormUrlEncoded
	@POST("/backend/android/and_category_result.php")
	Call<String> mainFavor(@Field("reqBody") String mainFavor);

	@FormUrlEncoded
	@POST("/backend/android/and_search.php")
	Call<String> search(@Field("reqBody") String search);

	@FormUrlEncoded
	@POST("/backend/android/and_level_select.php")
	Call<String> category1(@Field("category1_name") String category, @Field("level") String level);

	@FormUrlEncoded
	@POST("/backend/android/and_level_select.php")
	Call<String> category2(@Field("category1_name") String category_first, @Field("category2_name") String category_second, @Field("level") String level);

	@FormUrlEncoded
	@POST("/backend/android/and_level_select.php")
	Call<String> category3(@Field("category1_name") String category_first, @Field("category2_name") String category_second, @Field("category3_name") String category_third, @Field("level") String level);

	@FormUrlEncoded
	@POST("/backend/android/and_fcm_token_save.php")
	Call<String> fcmToken(@Field("userEmail") String userEmail, @Field("fcm_token") String fcm_token);

}
