package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class newUser extends AppCompatActivity {
    private EditText editText;
    private Button btn;
    private String newUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        editText = findViewById(R.id.newUser_editText);

    }

    public void goSelect(View view) {
        newUserName = editText.getText().toString();
        SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        pref.edit().putString("userName",newUserName).putBoolean("inTeam",true).commit();

        Intent intent = new Intent(this,notInTeam.class);
        startActivity(intent);
    }
}
