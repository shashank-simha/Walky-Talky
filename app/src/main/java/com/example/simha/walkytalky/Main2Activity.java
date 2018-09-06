package com.example.simha.walkytalky;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    public static final String TEAM = "null";

    //View Objects
    private Button buttonScan;

    //qr code scanner object
    private IntentIntegrator qrScan;

    private ArrayAdapter ClueAdapter;

    private List<String> cluesList = new ArrayList<String>();

    private String path;
    private String start;
    private String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final String team = (String) getIntent().getExtras().get(TEAM);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Data/" + team);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object team = snapshot.getValue();
                Gson gson = new Gson();
                String json = gson.toJson(team);
                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(json);
                    path = jsonObject.getString("path");
                    start = jsonObject.getString("start");
                    score = jsonObject.getString("score");

                    String temp = "Places/path" + path + "/" + start;

                    DatabaseReference refPlace = database.getReference(temp);

                    refPlace.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Object place = snapshot.getValue();
                            Main2Activity.this.cluesList.add(String.valueOf(place));
                            Main2Activity.this.ClueAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);

        ListView clueView = (ListView) findViewById(R.id.clues);
        ClueAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cluesList);
        clueView.setAdapter(ClueAdapter);

    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                String temp = "Places/path" + path + "/" + result.getContents();

                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference refPlace = database.getReference(temp);

                refPlace.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Object place = snapshot.getValue();
                        Main2Activity.this.cluesList.add(String.valueOf(place));
                        Main2Activity.this.ClueAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }
}
