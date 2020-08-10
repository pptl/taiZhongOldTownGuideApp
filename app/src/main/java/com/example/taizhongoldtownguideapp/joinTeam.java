package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class joinTeam extends AppCompatActivity {
    private EditText editText;
    private String teamID;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        editText = (EditText)findViewById(R.id.joinTeam_editText);

    }

    public void quickJoin(View view) {
        Map<String, Object> user = new HashMap<>();
        SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        teamID = editText.getText().toString();

        userName = pref.getString("userName","error");
        user.put("userName",userName);
        user.put("isLeader",false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        /*
        DocumentReference teamIDDocumentReference = db.collection("teamID").document(teamID);
        teamIDDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("firebaseProgress", "Document exists!");
                    } else {
                        Log.d("firebaseProgress", "Document does not exist!");
                    }
                } else {
                    Log.d("firebaseProgress", "Failed with: ", task.getException());
                }
            }
        });
    */
        db.collection("teamID").document(teamID).collection("userData").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("firebaseProgress", "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebaseProgress", "Error adding document", e);
                    }
                });



        pref.edit().putString("teamID",teamID).putBoolean("isLeader",false).commit();

        Intent intent = new Intent(this,whereIsMyFriend.class);
        startActivity(intent);
    }
}
