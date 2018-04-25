package com.example.michellecheng.happ;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class happinessThots extends AppCompatActivity {
    private TextView comments;
    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happiness_thots);

        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        text = getIntent().getStringExtra("message");

        comments = (TextView)findViewById(R.id.comments);
        //read in count from database
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //write the data
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    //String message = ds.child("message").getValue(String.class);
                    //String comms = comments.getText().toString();
                    //String newComms = comms + "/n" + message;
                    //comments.setText(newComms);
                    comments.setText(text);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(postListener);
    }
}
