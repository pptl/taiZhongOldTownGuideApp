package com.example.taizhongoldtownguideapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class newUser extends AppCompatActivity {
    private EditText editText;
    private String newUserName;
    private String userIconPath;
    private ImageView userIcon;
    private SharedPreferences pref;
    final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        pref = getSharedPreferences("userData",MODE_PRIVATE);

        editText = findViewById(R.id.newUser_editText);
        userIcon = findViewById(R.id.userIcon);
        userIconPath = "user_icon1";

    }

    public void goSelect(View view) {
        newUserName = editText.getText().toString();

        pref.edit().putString("userName",newUserName).apply();

        Intent intent = new Intent(this,notInTeam.class);
        startActivity(intent);

    }

    public void changeUserIcon(View view) {
        Intent intent = new Intent(this,chooseUserIcon.class);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            userIconPath = data.getStringExtra("userPickedIcon");
            pref.edit().putString("userIconPath",userIconPath).apply();
            int imageResource = getResources().getIdentifier("@drawable/" + userIconPath, null, getPackageName());
            userIcon.setImageResource(imageResource);
        }
    }
}
