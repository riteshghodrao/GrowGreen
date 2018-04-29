package com.example.android.loginfirebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String emailId;

    private Button sendPasswordResetEmail, signOut;
    private TextView nameTextView, emailTextView, genderTextView, dobTextView, bloodGroupTextView,
            mobileNumberTextView, addressTextView;
    private TextView viewMoisture, viewTemperature, viewHumidity;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private DatabaseReference userdb;
    private DatabaseReference moisture,temperature,humidity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        userdb = firebaseDatabase.getReference("users");
        DatabaseReference myRef1 = firebaseDatabase.getReference("Moisture");
        DatabaseReference myRef2 = firebaseDatabase.getReference("Temperature");
        DatabaseReference myRef3 = firebaseDatabase.getReference("Humidity");
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            emailId = user.getEmail();
        }

        progressBar = findViewById(R.id.profile_progressBar);
        nameTextView = findViewById(R.id.profile_name_textView);
        emailTextView = findViewById(R.id.profile_email_textView);
//        genderTextView = findViewById(R.id.profile_gender_textView);
//        dobTextView = findViewById(R.id.profile_dob_textView);
//        bloodGroupTextView = findViewById(R.id.profile_blood_group_textView);
//        mobileNumberTextView = findViewById(R.id.profle_mobile_number_textView);
//        addressTextView = findViewById(R.id.profile_address_textView);
        sendPasswordResetEmail = findViewById(R.id.send_password_reset_email);
        signOut = findViewById(R.id.sign_out);

        displayUserInformation();

        sendPasswordResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail();
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        viewMoisture = findViewById(R.id.moisture);
        viewTemperature = findViewById(R.id.temperature);
        viewHumidity = findViewById(R.id.humidity);
        // Moisture Value
        DatabaseReference dbref1 = firebaseDatabase.getReference("Moisture");
        Query q = dbref1.orderByKey().limitToLast(1);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double res=0.0;
                String msgtext="";
                Iterable<DataSnapshot> msg = dataSnapshot.getChildren();
                for (DataSnapshot x : msg){
                  //  Log.d("child --", x.toString());
                    msgtext = x.getValue().toString();
                    Double f = Double.parseDouble(msgtext);
                    res = (1024-f)/1024;
                }
                Double rounded = Math.round(res * 100.0)/100.0;
                viewMoisture.setText(rounded.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Temperature
        DatabaseReference dbref2 = firebaseDatabase.getReference("Temperature");
        Query q2 = dbref2.orderByKey().limitToLast(1);
        q2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msgtext="";
                Iterable<DataSnapshot> msg = dataSnapshot.getChildren();
                for (DataSnapshot x : msg){
                    //  Log.d("child --", x.toString());
                    msgtext = x.getValue().toString();
                }
                msgtext+=" Celsius";
                viewTemperature.setText(msgtext);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Humidity
        DatabaseReference dbref3 = firebaseDatabase.getReference("Humidity");
        Query q3 = dbref3.orderByKey().limitToLast(1);
        q3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msgtext="";
                Iterable<DataSnapshot> msg = dataSnapshot.getChildren();
                for (DataSnapshot x : msg){
                    //  Log.d("child --", x.toString());
                    msgtext = x.getValue().toString();
                }
                msgtext+=" %";
                viewHumidity.setText(msgtext);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button controlPanel = findViewById(R.id.controlPanel);
        controlPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this , StatusPlants.class);
                if(viewHumidity.getText().toString().equals("No Internet")){
                    Toast.makeText(getApplicationContext(), "Connect Internet to Proceed!", Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(myIntent);
                }
            }
        });
    }

    private void displayUserInformation() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userdb.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);

                    // Check for null
                    if (userInformation == null) {
                        Log.e(TAG, "User data is null!");
                    } else {
                        // Display user information
                        nameTextView.setText(userInformation.name);
                        emailTextView.setText(userInformation.email);
                       // genderTextView.setText(userInformation.gender);
                       // dobTextView.setText(userInformation.dob);

                       // mobileNumberTextView.setText(userInformation.mobile);
                       // addressTextView.setText(userInformation.address);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read user", databaseError.toException());
                }
            });
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    // Send password reset email
    public void sendPasswordResetEmail() {
        progressBar.setVisibility(View.VISIBLE);
        if (!emailId.equals("")) {
            auth.sendPasswordResetEmail(emailId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }



    public void refresh (View v){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


}
