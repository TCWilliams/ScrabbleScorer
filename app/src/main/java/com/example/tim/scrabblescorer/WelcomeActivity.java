package com.example.tim.scrabblescorer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView.OnItemSelectedListener;


public class WelcomeActivity extends Activity implements OnItemSelectedListener {

    int numberOfPlayers;
    Spinner numberPlayersSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        numberPlayersSpinner = ((Spinner) findViewById(R.id.selectNumberSpinner));
        numberPlayersSpinner.setOnItemSelectedListener(this);

        List<String> numbers = new ArrayList<>();
        numbers.add("2");
        numbers.add("3");
        numbers.add("4");
        numbers.add("5");
        numbers.add("6");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numbers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberPlayersSpinner.setAdapter(dataAdapter);

    }

    public void goToNamesActivity(View v) {
        Intent numPlayersIntent = new Intent(WelcomeActivity.this, EnterPlayersActivity.class);
        numPlayersIntent.putExtra("NUM_PLAYERS", numberOfPlayers);
        startActivity(numPlayersIntent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        numberOfPlayers = Integer.parseInt(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

