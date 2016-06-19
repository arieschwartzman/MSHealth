package com.arieschwartzman.cortana.auth;

import android.os.AsyncTask;
import android.util.Log;

import com.arieschwartzman.cortana.services.ISummaryService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ariesch on 19-Jun-16.
 */
public class TokenAsyncTask extends AsyncTask<String, Void, TokenResponse> {

    private ISummaryService service;
    private ITokenSetter tokenSetter;

    public static final String SECRET_KEY = "WVkmaKf1WPPXUYcmEmPmwAJ";
    public static final String CLIENT_ID = "000000004C197112";
    public static final String REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf";
    public static final String SCOPE = "mshealth.ReadDevices mshealth.ReadActivityHistory mshealth.ReadActivityLocation mshealth.ReadDevices offline_access";

    public TokenAsyncTask(ITokenSetter tokenSetter) {
        this.tokenSetter = tokenSetter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://login.live.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ISummaryService.class);
    }

    @Override
    protected TokenResponse doInBackground(String... strings) {

        Call<TokenResponse> tokenCall;
        if (strings[1].equals("refresh")) {
            tokenCall = service.getRefreshToken(REDIRECT_URI, CLIENT_ID, SECRET_KEY, strings[0], "refresh_token");
        } else {
            tokenCall = service.getToken(REDIRECT_URI, CLIENT_ID, SECRET_KEY, strings[0], "authorization_code");
        }
        Response<TokenResponse> response = null;
        try {
            response = tokenCall.execute();
            if (response.errorBody() != null) {
                String err = new String(response.errorBody().bytes());
                Log.e("", err);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.body();
    }

    @Override
    protected void onPostExecute(TokenResponse tokenResponse) {
        super.onPostExecute(tokenResponse);
        tokenSetter.setToken(tokenResponse);
    }
}