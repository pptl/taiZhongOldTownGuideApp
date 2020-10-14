package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class crawlersPage extends AppCompatActivity {

    List<String> title = new ArrayList<>();
    List<String> url = new ArrayList<>();
    private Handler handler;
    private RecyclerView mRecyclerView;
    private postListRecycleViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawlers_page);
        mRecyclerView = findViewById(R.id.showInfo);
        getPosts();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    mAdapter = new postListRecycleViewAdapter(crawlersPage.this, title, url);
                    mRecyclerView.setAdapter(mAdapter);

                }
            }
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getPosts(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //获取虎扑新闻20页的数据，网址格式为：https://voice.hupu.com/nba/第几页


                    Document doc = Jsoup.connect("http://www.genedu.fcu.edu.tw/index.php/tw/component/sppagebuilder/68-").get();
                    Elements titleLinks = doc.select("a.mod-articles-category-title ");    //解析来获取每条新闻的标题与链接地址
                    for(int i=0; i < titleLinks.size(); i++){
                        title.add(titleLinks.get(i).text());
                        url.add("http://www.genedu.fcu.edu.tw" + titleLinks.get(i).attr("href"));
                    }
                    //Log.d("seeElement", String.valueOf(titleLinks.size()));
                    //Log.d("seeElement", String.valueOf(titleLinks.get(0).text()));
                    Log.d("seeElement", url.toString());
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                    // Elements descLinks = doc.select("div.list-content");//解析来获取每条新闻的简介
                    //Elements timeLinks = doc.select("div.otherInfo");   //解析来获取每条新闻的时间与来源
                    /*
                        Log.e("title",Integer.toString(titleLinks.size()));
                        for(int j = 0;j < titleLinks.size();j++){
                            String title = titleLinks.get(j).select("a").text();
                            String uri = titleLinks.get(j).select("a").attr("href");
                            //   String desc = descLinks.get(j).select("span").text();
                            String time = timeLinks.get(j).select("span.other-left").select("a").text();

                        }

                     */


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
