package com.usrProject.taizhongoldtownguideapp.component;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.usrProject.taizhongoldtownguideapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsList extends AppCompatActivity {

    private List<String> titleList = new ArrayList<>();
    private List<String> url = new ArrayList<>();
    private Handler handler = null;
    private RecyclerView mRecyclerView;
    private NewsRecycleViewAdapter mAdapter;
    private int currentPage = 1;
    private int fromPage;
    private int toPage;
    private TextView currentPageTab;
    private Button prePageBtn;
    private Button nextPageBtn;
    private String responseJsonString = "";
    private JSONArray dataList;
    private ProgressBar newsListProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        mRecyclerView = findViewById(R.id.showInfo);
        currentPageTab = findViewById(R.id.currentPageTab);
        prePageBtn = findViewById(R.id.prePageBtn);
        newsListProgressBar = findViewById(R.id.newsList_progressBar);

        if(currentPage <= 1){
            prePageBtn.setClickable(false);
            prePageBtn.setAlpha((float)0.5);
        }
        nextPageBtn = findViewById(R.id.nextPageBtn);

        String url = "http://140.134.48.76/USR/API/API/Default/APPGetData?name=main&token=2EV7tVz0Pv6bLgB/aXRURg==";
        if(dataList == null){
            getPostJson(url);
        }

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    //資料載入完成後就移除loading標識
                    newsListProgressBar.setVisibility(View.INVISIBLE);
                    newsListProgressBar.setEnabled(false);
                    currentPage = 1;
                    currentPageTab.setText(String.valueOf(currentPage));
                    fromPage = (currentPage - 1) * 10;
                    toPage = currentPage * 10;
                    mAdapter = new NewsRecycleViewAdapter(NewsList.this, titleList.subList(fromPage, toPage), dataList);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    void getPostJson(String url){
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
                    String jsonString = JsonParser.parseString(responseJsonString).getAsString();
                    try {
                        dataList = new JSONArray(jsonString);
                        for(int i =0 ;i<dataList.length();i++){
                            JSONObject jsonObject = dataList.getJSONObject(i);
                            String title = jsonObject.get("MA_TITLE").toString();
                            titleList.add(title);
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void prePage(View view) {
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
        mAdapter = new NewsRecycleViewAdapter(NewsList.this, titleList.subList(fromPage, toPage), dataList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void nextPage(View view) {
        if(titleList.size() - (currentPage + 1) * 10 > 0){
            currentPage += 1;
            currentPageTab.setText(String.valueOf(currentPage));
            fromPage = (currentPage - 1) * 10;
            toPage = currentPage * 10;
            if(currentPage > 1){
                prePageBtn.setClickable(true);
                prePageBtn.setAlpha((float)1);
            }
        } else if ((titleList.size() - (currentPage + 1) * 10 < 0) && (titleList.size() - (currentPage + 1) * 10 > -9)){
            currentPage += 1;
            currentPageTab.setText(String.valueOf(currentPage));
            fromPage = (currentPage - 1) * 10;
            toPage = titleList.size();
            prePageBtn.setClickable(true);
            prePageBtn.setAlpha((float)1);
            nextPageBtn.setClickable(false);
            nextPageBtn.setAlpha((float)0.5);
        }

        mAdapter = new NewsRecycleViewAdapter(NewsList.this, titleList.subList(fromPage, toPage), dataList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
