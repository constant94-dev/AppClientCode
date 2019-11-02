package com.psj.accommodation.Interface;


import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

// TODO Retrofit API를 사용하기 위해 인터페이스를 만들어야 한다
// TODO Retrofit 인터페이스에 API 정의하고, 인터페이스 클래스를 Retrofit 초기화 하는 과정을 거쳐서 사용하게 된다
public interface ApiService {
	/*	@FormUrlEncoded : 서버에 POST 데이터를 보내려면 이것을 선언해야합니다
	 *	@POST : HTTP Post 요청을 사용하고 있기 때문에 이것을 선언해야합니다
	 *	괄호안에 POST 요청을 받을 URL이 있고 루트 URL은 제외한다
	 *	루트 URL은 MainActivity 에서 정의 했습니다
	 *	insertData() : 실제로 요청을 보내는 메소드 입니다
	 *	@Field("key")로 요청을 보내어 index.php 파일에서 $_POST['key'] 명령으로 값을 받을 수 있습니다
	 *	@Field 어노테이션은 POST 방식만 가능
	 */
	@FormUrlEncoded
	@POST("/checkEmail.php")
	Call<String> checkEmail(@Field("userEmail") String userEmail);

	@FormUrlEncoded
	@POST("/login.php")
	Call<String> loginUser(@Field("loginEmail") String loginEmail, @Field("loginPassword") String loginPassword);

	@FormUrlEncoded
	@POST("/mail.php")
	Call<String> mailAuth(@Field("userEmail") String UserEmail);

	@FormUrlEncoded
	@POST("/signUp.php")
	Call<String> signUpUser(@Field("userEmail") String userEmail, @Field("userPassword") String userPassword, @Field("userName") String userName);

	@FormUrlEncoded
	@POST("/reviewInsert.php")
	Call<String> reviewInsert(@Field("placeName") String placeName, @Field("placeTime") String placeTime, @Field("placeScore") float placeScore, @Field("placeImage") String placeImage, @Field("writer") String writer, @Field("placeReview") String placeReview);

	@FormUrlEncoded
	@POST("/commentInsert.php")
	Call<String> commentInsert(@Field("userEmail") String UserEmail, @Field("placeNum") String placeNum, @Field("commentData") String commentData, @Field("writer") String writer);

	@GET("/commentSelect.php")
	Call<String> commentSelect(@Query("placeNum") String placeNum);

	@GET("/commentUpdate.php")
	Call<String> commentUpdate(@Query("commentNum") String commentNum, @Query("commentData") String commentData);

	@GET("/commentDelete.php")
	Call<String> commentDelete(@Query("commentNum") String commentNum);

	@GET("/reviewSelect.php")
	Call<JsonObject> reviewSelect();

	@GET("/reviewSelectScore.php")
	Call<JsonObject> reviewSelectScore();

	@GET("/reviewUpdate.php")
	Call<String> reviewUpdate(@Query("placeNum") String placeNum, @Query("placeName") String placeName, @Query("placeImage") String placeImage, @Query("placeTime") String placeTime, @Query("placeScore") float placeScore, @Query("placeReview") String placeReview);

	@GET("/reviewDelete.php")
	Call<String> reviewDelete(@Query("placeNum") String placeNum);

	@GET("/reviewSearch.php")
	Call<JsonObject> reviewSearch(@Query("keyword") String searchKeyWord);

	@GET("/profileSelect.php")
	Call<String> profileSelect(@Query("profileEmail") String profileEmail);

	@GET("/chatSearch.php")
	Call<String> chatSearch(@Query("email") String email);

	@GET("/chatRoomInsert.php")
	Call<String> chatRoomInsert(@Query("names") String names, @Query("images") String images, @Query("creator") String creator);

	@GET("/chatRoomUpdate.php")
	Call<String> chatRoomUpdate(@Query("chatRoomNum") String chatRoomNum, @Query("lastMessage") String lastMessage);

	@GET("/chatRoomList.php")
	Call<JsonObject> chatRoomList(@Query("creator") String creator);

	@GET("/chattingGetProfile.php")
	Call<JsonObject> chattingGetProfile(@Query("chattingEmail") String chattingEmail);

	@GET("/sendUserGetImage.php")
	Call<String> sendUserGetImage(@Query("sendName") String sendName);

	// @Multipart -> 요청 본문이 여러 부분이 있다 @Part 주석을 달아야한다
	// @Part -> Multipart 요청의 단일 부분을 나타낸다
	// MultipartBody.Part -> 주석에 부품이름이 필요하지 않습니다
	// RequestBody -> 주석에 부품이름이 필요하다
	// String, Image, int -> 주석에 부품이름이 필요하다
	@Multipart
	@POST("/uploadImage.php")
	Call<String> uploadImage(@Part MultipartBody.Part file, @Part("name") RequestBody requestBody, @Part("profileName") String profileName, @Part("profileEmail") String profileEmail);

	@Multipart
	@POST("/multiUploadImage.php")
	Call<String> multiUploadImage(@Part ArrayList<MultipartBody.Part> files, @Part("totalFiles") RequestBody totalFiles);


} // ApiService 인터페이스 끝
