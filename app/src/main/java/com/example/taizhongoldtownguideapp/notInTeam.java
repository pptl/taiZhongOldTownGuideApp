package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class notInTeam extends AppCompatActivity {

    private TextView textView;
    private String userName;
    private String teamID;
    private int isUnique = 0;
    private String userIconPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_in_team);

        SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        userName = pref.getString("userName","error");

        textView = findViewById(R.id.notInTeam_textView);
        String wellcomeText = "歡迎你，" + userName;

        textView.setText(wellcomeText);

    }



    public void goCreateTeam(View view) {
        Map<String, Object> user = new HashMap<>();

        teamID = teamIDGenerator();
        final SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        userIconPath = pref.getString("userIconPath","error");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference teamIDDocumentReference = db.collection("teamID").document(teamID);
        /*
        while(true){
            if(isUnique == 1){
                Log.d("firebaseProgress","brakele");
                break;
            }

            teamIDDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            teamID = teamIDGenerator();
                            Log.d("firebaseProgress", "Document exists!");
                        } else {
                            isUnique = 1;
                            Log.d("firebaseProgress", "Document does not exist!");
                        }
                    } else {
                        Log.d("firebaseProgress", "Failed with: ", task.getException());
                    }
                }
            });
        }
    */



        user.put("userIconPath", userIconPath);
        user.put("userName",userName);
        user.put("isLeader",true);
        //这里要对teamID进行查看有没有重复的
        db.collection("teamID").document(teamID).collection("userData").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("firebaseProgress", "DocumentSnapshot added with ID: " + documentReference.getId());

                pref.edit().putString("teamID",teamID).putBoolean("isLeader",true).putString("userID",documentReference.getId()).commit();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebaseProgress", "Error adding document", e);
                    }
                });
        //可能要回传userID

        Intent intent = new Intent(this,whereIsMyFriend.class);
        startActivity(intent);

    }
    public void goJoinTeam(View view) {
        Intent intent = new Intent(this,joinTeam.class);
        startActivity(intent);

    }
    public String teamIDGenerator(){

        double rand = Math.random();
        String teamID = Double.toString(rand);
        teamID = teamID.substring(2,8);
        //这里要到firebase检查有没有这个房号ifno=>

        return teamID;
    }
}
