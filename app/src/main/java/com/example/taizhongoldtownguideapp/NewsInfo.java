package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsInfo extends AppCompatActivity {

    private String postTitle;
    private String postURL;
    private TextView titleTextView;
    private String postContext = "";
    private List<String> imgUrl  = new ArrayList<>();
    private Handler handler;
    private LinearLayout postLayout;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post);

        postTitle = getIntent().getStringExtra("title");
        postURL = getIntent().getStringExtra("url");

        titleTextView = findViewById(R.id.info_post_title);
        postLayout = findViewById(R.id.postLayout);

        titleTextView.setText(postTitle);

        getPostContext();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    newTextView(postContext);
                    Log.d("seeImgUrl", String.valueOf(imgUrl.size()));
                    if(imgUrl.size() > 0){
                        for(int i = 0; i< imgUrl.size(); i++){
                            newImageView(imgUrl.get(i));
                            //Log.d("seeImgUrl",imgUrl.get(i));
                        }
                    }
                }
            }
        };
    }
    private void newTextView(String text){
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(20);
        postLayout.addView(tv);
    }
    private void newImageView(String url){
        ImageView iv = new ImageView(this);
        Picasso.with(this).load(url).into(iv);
        postLayout.addView(iv);
    }
    private void getPostContext(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                        Document doc = Jsoup.connect(postURL).get();
                        Elements context = doc.select("div.sppb-addon-content");
                        String imgLink = "";
                        postContext = context.text();
                        if(!postContext.equals("")){
                            imgLink = context.select("img").attr("src");
                            if(!imgLink.equals("")){
                                Log.d("seeImgUrl", String.valueOf(imgLink));
                                imgUrl.add("http://www.genedu.fcu.edu.tw" + imgLink);
                            } else{
                                Log.d("seeImgUrl","noPic");
                            }
                        }
                        else{
                            context = doc.select("article > div > p");
                            postContext = context.text();
                            Log.d("seePostContext",postContext);
                            imgLink = context.select("img").attr("src");
                            if(!imgLink.equals("")){
                                Log.d("seeImgUrl", String.valueOf(imgLink));
                                imgUrl.add("http://www.genedu.fcu.edu.tw" + imgLink);
                            } else {
                                Log.d("seeImgUrl","noPic");
                            }
                        }
                        //contextTextView.setText(postURL);
                        //Log.d("seeContext",context.text().toString());
                    /*
                        for(int j = 0;j < titleLinks.size();j++){
                            String title = titleLinks.get(j).select("a").text();
                            String uri = titleLinks.get(j).select("a").attr("href");
                            String desc = descLinks.get(j).select("span").text();
                            String time = timeLinks.get(j).select("span.other-left").select("a").text();
                            News news = new News(title,uri,desc,time);
                            newsList.add(news);

                     */
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
