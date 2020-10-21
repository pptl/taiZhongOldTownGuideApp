package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class crawlersPage extends AppCompatActivity {

    private List<String> title = new ArrayList<>();
    private List<String> url = new ArrayList<>();
    private Handler handler;
    private RecyclerView mRecyclerView;
    private postListRecycleViewAdapter mAdapter;
    private int currentPage = 1;
    private int fromPage;
    private int toPage;
    private TextView currentPageTab;
    private Button prePageBtn;
    private Button nextPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawlers_page);
        mRecyclerView = findViewById(R.id.showInfo);
        currentPageTab = findViewById(R.id.currentPageTab);
        prePageBtn = findViewById(R.id.prePageBtn);


        if(currentPage <= 1){
            prePageBtn.setClickable(false);
            prePageBtn.setAlpha((float)0.5);
        }
        nextPageBtn = findViewById(R.id.nextPageBtn);
        getPosts();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    currentPage = 1;
                    currentPageTab.setText(String.valueOf(currentPage));
                    fromPage = (currentPage - 1) * 10;
                    toPage = currentPage * 10;
                    Log.d("seeCurrPage",currentPage+":"+fromPage+":"+toPage);
                    mAdapter = new postListRecycleViewAdapter(crawlersPage.this, title.subList(fromPage, toPage), url.subList(fromPage, toPage));
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

    public void prePage(View view) {
        //Log.d("seeclick","prewasclick");
        if(currentPage - 1 >= 1){
            currentPage -= 1;
            currentPageTab.setText(String.valueOf(currentPage));
            fromPage = (currentPage - 1) * 10;
            toPage = currentPage * 10;

            if(!nextPageBtn.isClickable()){
                nextPageBtn.setClickable(true);
                nextPageBtn.setAlpha((float)1);
            }
            if(currentPage <= 1){
                prePageBtn.setClickable(false);
                prePageBtn.setAlpha((float)0.5);
            }

        }
        //Log.d("seeCurrPage", String.valueOf(title.size()));
        mAdapter = new postListRecycleViewAdapter(crawlersPage.this, title.subList(fromPage, toPage), url.subList(fromPage, toPage));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void nextPage(View view) {
        if(title.size() - (currentPage + 1) * 10 > 0){
            currentPage += 1;
            currentPageTab.setText(String.valueOf(currentPage));
            fromPage = (currentPage - 1) * 10;
            toPage = currentPage * 10;
            if(currentPage >= 2){
                prePageBtn.setClickable(true);
                prePageBtn.setAlpha((float)1);
            }
        } else if((title.size() - (currentPage + 1) * 10 < 0) && (title.size() - (currentPage + 1) * 10 > -9)){
            currentPage += 1;
            currentPageTab.setText(String.valueOf(currentPage));
            fromPage = (currentPage - 1) * 10;
            toPage = title.size();
            nextPageBtn.setClickable(false);
            nextPageBtn.setAlpha((float)0.5);
        }

        //Log.d("seeCurrPage", String.valueOf(title.size()));
        mAdapter = new postListRecycleViewAdapter(crawlersPage.this, title.subList(fromPage, toPage), url.subList(fromPage, toPage));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
