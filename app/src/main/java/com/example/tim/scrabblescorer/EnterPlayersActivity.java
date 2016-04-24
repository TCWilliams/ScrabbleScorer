package com.example.tim.scrabblescorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPlayersActivity extends AppCompatActivity {

    int numberOfPlayers;
    EditText[] playerNames = new EditText[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_players);

        Intent caller = getIntent();
        numberOfPlayers = caller.getIntExtra("NUM_PLAYERS", 0);


        EditText player1 = (EditText) findViewById(R.id.editTextP1);
        EditText player2 = (EditText) findViewById(R.id.editTextP2);
        EditText player3 = (EditText) findViewById(R.id.editTextP3);
        EditText player4 = (EditText) findViewById(R.id.editTextP4);
        EditText player5 = (EditText) findViewById(R.id.editTextP5);
        EditText player6 = (EditText) findViewById(R.id.editTextP6);

        playerNames[0] = player1;
        playerNames[1] = player2;
        playerNames[2] = player3;
        playerNames[3] = player4;
        playerNames[4] = player5;
        playerNames[5] = player6;

        for (int i = 0; i < numberOfPlayers; i++) {
            playerNames[i].setVisibility(View.VISIBLE);
        }
    }

    public void setPlayerNames(View v) {
        Boolean gotNames = true;
        String[] names = new String[numberOfPlayers]; // array to store player names
        for (int i = 0; i < numberOfPlayers; i++) {
            if(playerNames[i].length()!=0)
            names[i] = playerNames[i].getText().toString();
            else{
                gotNames = false;
                Toast.makeText(this.getBaseContext(), "enter all player names", Toast.LENGTH_LONG).show();
            }
        }
        if(gotNames) {
            Intent goToScoring = new Intent(EnterPlayersActivity.this, ScoringActivity.class);
            goToScoring.putExtra("PLAYER_NAMES", names);
            startActivity(goToScoring);
        }

    }
}
