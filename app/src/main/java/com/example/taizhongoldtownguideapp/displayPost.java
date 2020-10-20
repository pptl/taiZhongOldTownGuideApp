package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class displayPost extends AppCompatActivity {

    private String postTitle;
    private String postURL;
    private TextView titleTextView;
    private TextView contextTextView;
    private String postContext = "";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post);

        postTitle = getIntent().getStringExtra("title");
        postURL = getIntent().getStringExtra("url");

        titleTextView = findViewById(R.id.info_post_title);
        contextTextView = findViewById(R.id.info_post_contexxt);

        titleTextView.setText(postTitle);
        //contextTextView.setText(postURL);
        getPostContext();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                  contextTextView.setText(postContext);
                }
            }
        };
    }
    private void getPostContext(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                        Document doc = Jsoup.connect(postURL).get();
                        Elements context = doc.select("div.sppb-addon-content");

                        for(int i = 0; i<2 ;i++){
                            Log.d("seePost",context.get(i).toString());

                        }
                        postContext = context.text();
                        //Log.d("seePost",context.toString());
                        /*分割的工作之后再说 **需要每个text产生一个textView
                        String posts[] = postContext.split(" ");
                        for(int i =0; i<posts.length;i++){
                            Log.d("seePost",posts[i]);
                        }
                        */

                        if(postContext.equals("")){

                            context = doc.select("div > p");

                            postContext = context.text();
                            //postContext.replace("<br>","/n");
                            Log.d("seeIsEmpty",postContext);
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
