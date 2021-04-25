package com.example.taizhongoldtownguideapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Base64;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsInfo extends AppCompatActivity {

    private String postTitle;
    private TextView titleTextView;
    private TextView contentTextView;
    private String postContext = "";
    private Handler messageHandler;
    private LinearLayout postLayout;
    private String postContent = "";
    private String postID = "";
    private String postIndex = "";
    private String responseJsonString = "";
    private String url ="http://140.134.48.76/USR/API/API/Default/APPGetData?name=main&token=2EV7tVz0Pv6bLgB/aXRURg==";
    private WebView postWebView;
    private ProgressBar newsInfoProgressBar;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info);

        postTitle = getIntent().getStringExtra("title");
        postID = getIntent().getStringExtra("id");
        postIndex = getIntent().getStringExtra("index");

        postWebView = findViewById(R.id.postWebView);
        titleTextView = findViewById(R.id.info_post_title);
        titleTextView.setText(postTitle);
        newsInfoProgressBar = findViewById(R.id.newsInfo_progressBar);

        getPointJson(url);

        messageHandler = new Handler(){
            @Override
            public void handleMessage(final Message msg) {
                if(msg.what == 1){
                    String jsonString = JsonParser.parseString(responseJsonString).getAsString();

                    try {
                        JSONArray jsonArray = new JSONArray(jsonString);
                        JSONObject dataObject = jsonArray.getJSONObject(Integer.parseInt(postIndex));
                        String rawContent = dataObject.getString("MA_CONTENT");
                        postContent = EscapeUnescape.unescape(rawContent);
                        Document doc = (Document) Jsoup.parse(postContent);
                        postWebView.setWebViewClient(new WebViewClient() {
                            public void onPageFinished(WebView view, String url) {
                              newsInfoProgressBar.setVisibility(View.INVISIBLE);
                              newsInfoProgressBar.setEnabled(false);
                            }
                        });
                        postWebView.loadDataWithBaseURL(null,String.valueOf(doc) , "text/html", "base64", null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    void getPointJson(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    responseJsonString = response.body().string();
                    Message msg = new Message();
                    msg.what = 1;
                    messageHandler.sendMessage(msg);
                }
            }
        });

    }

}
