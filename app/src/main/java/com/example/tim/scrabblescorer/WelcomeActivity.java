package com.example.tim.scrabblescorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class WelcomeActivity extends Activity {

    int numberOfPlayers;
    Spinner numberPlayersSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setUpDropDownNumberSelect();
    }

    public void setUpDropDownNumberSelect() {
        numberPlayersSpinner = ((Spinner) findViewById(R.id.selectNumberSpinner));
        numberPlayersSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberOfPlayers = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void goToNamesActivity(View v) {
        Intent numPlayersIntent = new Intent(WelcomeActivity.this, EnterPlayersActivity.class);
        numPlayersIntent.putExtra("NUM_PLAYERS", numberOfPlayers);
        startActivity(numPlayersIntent);
    }
}

