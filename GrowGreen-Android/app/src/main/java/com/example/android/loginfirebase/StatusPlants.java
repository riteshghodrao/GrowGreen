package com.example.android.loginfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatusPlants extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_plants);

        final Switch motorSwitch = (Switch) findViewById(R.id.switch1);
        final Switch lightSwitch = (Switch) findViewById(R.id.switch2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef1 = database.getReference("motor");
        final DatabaseReference myRef2 = database.getReference("light");

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(int.class);
                if(value ==0){
                    motorSwitch.setChecked(false);
                }
                else {
                    motorSwitch.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(int.class);
                if(value ==0){
                    lightSwitch.setChecked(false);
                }
                else {
                    lightSwitch.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        motorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    myRef1.setValue(1);
                }
                else {
                    myRef1.setValue(0);
                }
                Toast.makeText(getApplicationContext(), "Updating.. Please Wait !!", Toast.LENGTH_LONG).show();
            }
        });

        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    myRef2.setValue(1);
                }
                else {
                    myRef2.setValue(0);
                }
                Toast.makeText(getApplicationContext(), "Updating.. Please Wait !!", Toast.LENGTH_LONG).show();
            }
        });


    }

}
