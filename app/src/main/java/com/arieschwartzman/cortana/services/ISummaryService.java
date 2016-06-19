package com.arieschwartzman.cortana.services;

import com.arieschwartzman.cortana.auth.TokenResponse;
import com.arieschwartzman.cortana.model.SummaryResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ariesch on 16-Jun-16.
 */

public interface ISummaryService {
    @GET("/v1/me/Summaries/daily")
    @Headers({
            "Content-Type: application/json"
    })
    Call<SummaryResponse> getSummaries(@Header("Authorization") String authorization,
                                       @Query("startTime") String startTime,
                                       @Query("endTime") String endTime);

    @FormUrlEncoded
    @POST("/oauth20_token.srf")
    Call<TokenResponse> getToken(@Field("redirect_uri") String redirect_uri,
                                 @Field("client_id") String client_id,
                                 @Field("client_secret") String client_secret,
                                 @Field("code") String code,
                                 @Field("grant_type") String grantType);

    @FormUrlEncoded
    @POST("/oauth20_token.srf")
    Call<TokenResponse> getRefreshToken(@Field("redirect_uri") String redirect_uri,
                                        @Field("client_id") String client_id,
                                        @Field("client_secret") String client_secret,
                                        @Field("refresh_token") String refreshToken,
                                        @Field("grant_type") String grantType);
}
