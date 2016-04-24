package com.example.tim.scrabblescorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    int numberOfPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


    }

    public void setNumberPlayers(View v) {
        EditText numberPlayersEditText = ((EditText) findViewById(R.id.editText_num_players));


        try {
            //create intent to store number of players and start enter players activity
            numberOfPlayers = Integer.parseInt(numberPlayersEditText.getText().toString());
            if (numberOfPlayers > 1 && numberOfPlayers < 7) {
                Intent numPlayersIntent = new Intent(WelcomeActivity.this, EnterPlayersActivity.class);
                numPlayersIntent.putExtra("NUM_PLAYERS", numberOfPlayers);
                startActivity(numPlayersIntent);
                finish();
            } else {
                Toast.makeText(this.getBaseContext(), "enter a number 2 - 6", Toast.LENGTH_LONG).show();
                numberPlayersEditText.setText("");
            }
            Log.i("", numberOfPlayers + "is a number");
        } catch (NumberFormatException e) {
            Log.i("", 0 + "is not a number");
        }
    }
}
