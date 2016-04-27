package com.example.tim.scrabblescorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class EnterPlayersActivity extends AppCompatActivity {

    int numberOfPlayers;
    EditText[] nameEditTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_players);

        getIntentExtras();
        setupNameTextEdits();
    }

    public void getIntentExtras() {
        Intent caller = getIntent();
        numberOfPlayers = caller.getIntExtra("NUM_PLAYERS", 0);
    }

    // create numberOfPlayers EditTexts for player name entry
    public void setupNameTextEdits() {
        nameEditTexts = new EditText[numberOfPlayers];
        LinearLayout namesLayout = (LinearLayout) findViewById(R.id.linearLayoutNames);
        String[] nameHints = getResources().getStringArray(R.array.player_name_hints);

        for (int i = 0; i < numberOfPlayers; i++) {
            EditText nameEditText = new EditText(this);
            nameEditText.setHint(nameHints[i]);
            namesLayout.addView(nameEditText);
            nameEditTexts[i] = nameEditText;
        }
    }

    public void setPlayerNames(View v) {
        Boolean gotNames = true;
        String[] names = new String[numberOfPlayers]; // array to store player names
        for (int i = 0; i < numberOfPlayers; i++) {
            if (nameEditTexts[i].length() != 0)
                names[i] = nameEditTexts[i].getText().toString();
            else {
                gotNames = false;
                Toast.makeText(this.getBaseContext(), "enter all player names", Toast.LENGTH_LONG).show();
            }
        }
        if (gotNames) {
            Intent goToScoring = new Intent(EnterPlayersActivity.this, ScoringActivity.class);
            goToScoring.putExtra("PLAYER_NAMES", names);
            startActivity(goToScoring);
        }
    }
}
