package com.example.michellecheng.happ;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.location.Location;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import com.esri.arcgisruntime.location.LocationDataSource.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private GraphicsOverlay graphicsOverlay;
    private TextView title;
    private TextView numHappies;
    private ImageButton happyButton;
    private EditText enterHappiness;
    private ImageButton goToMap;
    private ImageButton goToComments;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<Address> addresses;
    private int permissionCheckLoc;
    private double lat, lng;
    private ArrayList<Hoint> hoints;
    private String edit_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphicsOverlay = new GraphicsOverlay();
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final  DatabaseReference myRef = database.getReference();

        BitmapDrawable pin = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.pin);
        final PictureMarkerSymbol pinSymbol = new PictureMarkerSymbol(pin);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        permissionCheckLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheckLoc == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                Toast.makeText(this, "Allow access to current location to use current address", Toast.LENGTH_SHORT).show();
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                getLocation();
            }
        }
        else{
            getLocation();
        }

        //read in count from database
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = dataSnapshot.child("count").getValue(Integer.class);
                numHappies.setText(""+count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(postListener);

        //the happ title
        title = (TextView)findViewById(R.id.title);
        //the count increments here
        numHappies = (TextView)findViewById(R.id.numHappies);
        //numHappies.setText();
        enterHappiness = (EditText)findViewById(R.id.happyText);

        //the happy button
        happyButton = (ImageButton)findViewById(R.id.happyImageButton);
        happyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enterHappiness.getText().equals(""))
                    System.out.println("");
                else {
                    //update counter
                    getLocation();
                    int currentNum = Integer.parseInt(numHappies.getText().toString());
                    currentNum++;
                    //write count to database
                    myRef.child("count").setValue(currentNum);
                    numHappies.setText("" + currentNum);
                    //update message
                    //TODO update messages in database
                    String message = enterHappiness.getText().toString();
                    edit_message = enterHappiness.getText().toString();
                    myRef.child("message").push().setValue(message);
                    myRef.child("lat").push().setValue(lat);
                    myRef.child("lng").push().setValue(lng);
                    enterHappiness.setText("");

                }
            }
        });

        goToMap = (ImageButton)findViewById(R.id.goToMap);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read in count from database

                startActivity(new Intent(MainActivity.this, map.class));
            }
        });

        goToComments = (ImageButton)findViewById(R.id.goToComments);
        goToComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message = new Intent(MainActivity.this, happinessThots.class);
                message.putExtra("message", edit_message);
                startActivity(message);
            }
        });
    }

    void getLocation(){
        if(permissionCheckLoc == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(android.location.Location location) {
                            if(location != null){
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                /* If you want to print address to check location, uncomment
                                try{
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                } catch (IOException e){

                                }*/
                                lat = location.getLatitude();
                                lng = location.getLongitude();
                                //Uncomment below to print location
                                //Toast.makeText(getApplicationContext(), addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        else
            Toast.makeText(this, "Nope", Toast.LENGTH_SHORT).show();
    }




}
