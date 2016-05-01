package com.example.tim.scrabblescorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameResultsActivity extends AppCompatActivity {

    String names[];
    int[] scores;

    String winnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);

        getIntentExtras();
        getResults();

    }

    public void getIntentExtras() {
        Intent caller = getIntent();
        names = caller.getStringArrayExtra("NAMES");
        scores = caller.getIntArrayExtra("SCORES");
    }

    public void getResults() {
        int winnerPlayerNumber = 0;
        int highestScore = 0;
        int lowestScore = Integer.MAX_VALUE;
        int loserPlayerNumber = 0;
        boolean draw = false;

        for (int i = 0; i < names.length; i++) {
            if (scores[i] > highestScore) {
                highestScore = scores[i];
                winnerPlayerNumber = i;
                for (int j = 0; j < names.length; j++) {
                    if (scores[j] == scores[i] && j != winnerPlayerNumber) draw = true;
                }
            }
            if (scores[i] < lowestScore) {
                lowestScore = scores[i];
                loserPlayerNumber = i;
            }
        }

        if (!draw) winnerName = names[winnerPlayerNumber].toString();
        else winnerName = getResources().getString(R.string.draw_message);
        String loserPLayerName = names[loserPlayerNumber].toString();
        int winnersScore = scores[winnerPlayerNumber];
        if (scores[winnerPlayerNumber] > 0) showWinner(draw, false);
        else showWinner(draw, true);
    }

    public void showWinner(boolean draw, boolean allZero) {
        TextView winnerNameTextView = (TextView) findViewById(R.id.winnerNameTextView);
        if (allZero) winnerNameTextView.setText(R.string.no_scores);
        else {
            if (!draw) winnerNameTextView.append(winnerName);
            else winnerNameTextView.setText(R.string.draw_message);
        }
    }

    public void goBackToScores(View v) {
        onBackPressed();
    }
}

