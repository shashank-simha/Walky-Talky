package com.example.simha.walkytalky;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


//implementing onclicklistener
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button teamBtn = (Button) findViewById(R.id.teamBtn);
        teamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                final String team = ((EditText) findViewById(R.id.team)).getText().toString();

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (team.equals("")) {
                            Toast.makeText(MainActivity.this, "Team name can not be empty", Toast.LENGTH_LONG).show();
                        } else if (snapshot.hasChild("Data/" + team)) {
                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            intent.putExtra(Main2Activity.TEAM, (String) team);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "No such Team exists", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }
        });
    }

}