package com.example.login.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.login.R;
/**
 * This is the activity of the autofill function.
 * The entire class is fully developed by Chun Jiang and Chi Zhang
 */
public class PasswordAutofillActivity extends AppCompatActivity {

    private String url, account, password;
    private WebView mWebView;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_autofill);

        Intent intent = getIntent();
        if(intent != null){
            url = intent.getStringExtra("url");
            account = intent.getStringExtra("account");
            password = intent.getStringExtra("password");
        }
        mWebView = findViewById(R.id.web_view);

        mWebView.loadUrl(url);

        mWebView.getSettings().setJavaScriptEnabled(true);

        if(url.contains("facebook")){
            flag = 1;
        } else if(url.contains("snapchat")){
            flag = 2;
        } else if(url.contains("linkedin")) {
            flag = 3;
        } else if(url.contains("indeed")) {
            flag = 4;
        }

        final String js;
        switch(flag){
            case 1:
                js = "javascript:document.getElementById('m_login_email').value='" + account + "';" +
                        "javascript:document.getElementById('m_login_password').value='" + password + "';";
                break;
            case 2:
                js = "javascript:document.getElementById('username').value='" + account + "';" +
                        "javascript:document.getElementById('password').value='" + password + "';";
                break;
            case 3:
                js = "javascript:document.getElementById('session_key').value='" + account + "';" +
                        "javascript:document.getElementById('session_password').value='" + password + "';";
                break;
            case 4:
                js = "javascript:document.getElementById('login-username').value='" + account + "';" +
                        "javascript:document.getElementById('password-input').value='" + password + "';";
                break;
            default:
                js = "javascript:document.getElementById('email').value='" + account + "';" +
                        "javascript:document.getElementById('password').value='" + password + "';";
                break;
        }
























        mWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                if(Build.VERSION.SDK_INT >= 19){
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                        }
                    });
                }
            }
        });
    }
}