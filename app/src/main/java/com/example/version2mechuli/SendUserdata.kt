package com.example.version2mechuli

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SendUserdata {

    @FormUrlEncoded
    @POST("/registration")
    suspend fun requestData(
        @Field("id") id : String,
        @Field("pw") pw : String,
        @Field("sex") sex : String,
        @Field("age") user : String,
        @Field("signUpRatings") ratings : MutableMap<String, Float>
    ): GetData // 서버에서 받아올 데이터 형식

}
