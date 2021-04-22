package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info);

        postTitle = getIntent().getStringExtra("title");
        postID = getIntent().getStringExtra("id");
        postIndex = getIntent().getStringExtra("index");

        titleTextView = findViewById(R.id.info_post_title);
        postLayout = findViewById(R.id.postLayout);
        contentTextView = findViewById(R.id.info_post_content);
        titleTextView.setText(postTitle);

        getPointJson(url);

        messageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    String jsonString = JsonParser.parseString(responseJsonString).getAsString();

                    try {
                        JSONArray jsonArray = new JSONArray(jsonString);
                        JSONObject dataObject = jsonArray.getJSONObject(Integer.parseInt(postIndex));
                        String rawContent = dataObject.getString("MA_CONTENT");
                        postContent = EscapeUnescape.unescape(rawContent);
                        Log.d("seeJson",postContent);

                        Document doc = (Document) Jsoup.parse(postContent);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            contentTextView.setText(Html.fromHtml(String.valueOf(doc),Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            contentTextView.setText(Html.fromHtml(String.valueOf(doc)));
                        }

                        //newTextView(postContext);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
    /*
    private void newTextView(String text){
        TextView tv = new TextView(this);
        Document doc = (Document) Jsoup.parse(text);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tv.setText(Html.fromHtml(String.valueOf(doc),Html.FROM_HTML_MODE_LEGACY));
        } else {
            tv.setText(Html.fromHtml(String.valueOf(doc)));
        }

        tv.setText(text);
        tv.setTextSize(20);
        postLayout.addView(tv);
    }
    */

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
