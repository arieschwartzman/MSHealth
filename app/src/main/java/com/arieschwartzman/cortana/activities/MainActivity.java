package com.arieschwartzman.cortana.activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.arieschwartzman.cortana.R;
import com.arieschwartzman.cortana.auth.ITokenSetter;
import com.arieschwartzman.cortana.auth.TokenAsyncTask;
import com.arieschwartzman.cortana.auth.TokenResponse;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements ITokenSetter {

    public static final String SCOPE = "mshealth.ReadDevices mshealth.ReadActivityHistory mshealth.ReadActivityLocation mshealth.ReadDevices offline_access";
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
                    TokenAsyncTask task = new TokenAsyncTask(MainActivity.this);
                    String code = Uri.parse(url).getQueryParameter("code");
                    task.execute(code,"");
                }
            }
        }

        wv.setWebViewClient(new myWebViewClient());

        String scopes = URLEncoder.encode(SCOPE);
        String redirect = URLEncoder.encode(TokenAsyncTask.REDIRECT_URI);
        String url = "https://login.live.com/oauth20_authorize.srf?redirect_uri=" + redirect + "&client_id=" + TokenAsyncTask.CLIENT_ID + "&response_type=code&scope="+scopes;

        // Check if already got token once before
        String refreshToken = pref.getString("RefreshToken",null);
        if (refreshToken != null) {
            TokenAsyncTask task = new TokenAsyncTask(this);
            task.execute(refreshToken, "refresh");
        }
        else {
            wv.loadUrl(url);
        }
    }

    public void setToken(TokenResponse tokenResponse) {
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
}
