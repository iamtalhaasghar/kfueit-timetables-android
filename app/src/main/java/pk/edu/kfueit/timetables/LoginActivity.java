package pk.edu.kfueit.timetables;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText etUsername = findViewById(R.id.username);
        final EditText etPassword = findViewById(R.id.password);
        final EditText etCaptcha = findViewById(R.id.captcha_field);
        final Button btnLogin = findViewById(R.id.login);
        final WebView wvLogin = findViewById(R.id.login_webview);
        final WebView wvCaptcha = findViewById(R.id.captcha_webview);

        wvLogin.setX(-1000);
        wvLogin.loadUrl("https://my.kfueit.edu.pk");
        WebSettings webSettings = wvLogin.getSettings();
        webSettings.setJavaScriptEnabled(true);

        final Data appData = new Data(LoginActivity.this);
        etUsername.setText(appData.getRollNumber());
        etPassword.setText(appData.getPassword());

        wvLogin.setWebViewClient(new WebViewClient() {
            boolean pageLoaded = true;


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);
                wvLogin.loadUrl(url);
                if(url.contains("dashboard")){

                    appData.saveCookieValue(getCookie(url, appData.getCookieName()));
                    appData.saveLoginData(etUsername.getText().toString(), etPassword.getText().toString());
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    if (appData.isTimeTablePresent()) {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }

                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Invalid Login Credentials or Invalid Captcha", Toast.LENGTH_SHORT).show();
                }
                return true;
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
               pageLoaded = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(pageLoaded){
                    wvCaptcha.loadUrl("https://my.kfueit.edu.pk/main/cap");
                }

            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                if(view.getUrl().contains("kfueit")){
                    System.out.println(error);
                    handler.proceed();
                }
                else{
                    handler.cancel();
                }

            }
        });


        wvCaptcha.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                if(view.getUrl().contains("kfueit")){
                    handler.proceed();
                }
                else{
                    handler.cancel();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String js = "javascript:document.getElementById('username').value = '"+etUsername.getText().toString()+"';" +
                        "document.getElementById('password').value='"+etPassword.getText().toString()+"';"+
                        "document.getElementsByName('captcha')[0].value='"+etCaptcha.getText().toString()+"';"+
                        "document.getElementById('submit').click();";

                if (Build.VERSION.SDK_INT >= 19) {
                    wvLogin.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                } else {
                    wvLogin.loadUrl(js);
                }

            }
        });

        if(!LoginActivity.isConnectedToInternet(LoginActivity.this)){
            Toast.makeText(LoginActivity.this, "Connect to Internet and Restart Application.", Toast.LENGTH_SHORT).show();
        }

    }

    private String getCookie(String siteName,String cookieName){
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp=cookies.split(";");
        for (String ar1 : temp ){
            if(ar1.contains(cookieName)){
                String[] temp1=ar1.split("=");
                return temp1[1];

            }
        }
        return null;
    }

    public static boolean isConnectedToInternet(Context context){
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }

}
