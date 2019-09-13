package com.psj.accommodation.Interface;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
	Call<String> reviewInsert(@Field("placeName") String placeName, @Field("placeTime") String placeTime, @Field("placeScore") float placeScore, @Field("placeImage") String placeImage, @Field("writer") String writer);


	@GET("/reviewSelect.php")
	Call<JsonObject> reviewSelect();

} // ApiService 인터페이스 끝
