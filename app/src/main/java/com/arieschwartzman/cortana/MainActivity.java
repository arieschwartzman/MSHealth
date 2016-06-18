package com.arieschwartzman.cortana;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String SECRET_KEY = "KfGudEtrdDMjoT8h8bLSxih";
    public static final String CLIENT_ID = "0000000040192E34";
    public static final String REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf";
    SharedPreferences pref;
    boolean authComplete = false;
    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);

        wv = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebViewClient webViewClient = new WebViewClient();


        class myWebViewClient extends WebViewClient {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("code") && url.contains("/oauth20_desktop.srf") && !authComplete) {
                    authComplete = true;

                    BackgroundTask task = new BackgroundTask();
                    String code = Uri.parse(url).getQueryParameter("code");
                    task.execute(code,"");
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("AuthCode", code);
                    edit.commit();
                }
            }
        }

        wv.setWebViewClient(new myWebViewClient());

        String scopes = URLEncoder.encode("mshealth.ReadDevices mshealth.ReadActivityHistory mshealth.ReadActivityLocation mshealth.ReadDevices");
        String redirect = URLEncoder.encode(REDIRECT_URI);
        String url = "https://login.live.com/oauth20_authorize.srf?redirect_uri=" + redirect + "&client_id=" + CLIENT_ID + "&response_type=code&scope="+scopes;
        String authCode = pref.getString("AuthCode",null);

//        if (authCode != null) {
//            BackgroundTask task = new BackgroundTask();
//            task.execute(authCode, "refresh");
//        }
//        else {
//        }
        wv.loadUrl(url);
    }

    private void setToken(TokenResponse tokenResponse) {
        if (tokenResponse != null) {
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("Token", tokenResponse.getAccess_token());
            edit.putString("RefreshToken", tokenResponse.getRefresh_token());
            edit.commit();
            String summary = "<html><body>Logged In Successfully</body></html>";
            wv.loadData(summary, "text/html", null);
        }
        else {
            wv.loadData("<html><body>Failed to login</body></html>", "text/html", null);
        }
    }

    public class BackgroundTask extends AsyncTask<String, Void,  TokenResponse> {

        private ISummaryService service;

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
            }
            else {
                tokenCall = service.getToken(REDIRECT_URI, CLIENT_ID, SECRET_KEY, strings[0],"authorization_code");
            }
            Response<TokenResponse> response =null;
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

            MainActivity.this.setToken(tokenResponse);
        }
    }


}
