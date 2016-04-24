package com.example.tim.scrabblescorer;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ScoringActivity extends AppCompatActivity {

    String receivedNames[];
    int numberOfPlayers;
    TextView[] playerNames;
    TextView[] playerCurrentScores;
    TextView[] playerAllScores;

    int currentPlayerNumber = 0;  //starts at player 1 (0)
    String currentPlayerName;
    int[] currentScores;
    List<List<Integer>> pastScores; // 2d List for keeping track of scores
    EditText editTextScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);

        Intent getNames = getIntent();
        receivedNames = getNames.getStringArrayExtra("PLAYER_NAMES");
        numberOfPlayers = receivedNames.length;
        playerNames = new TextView[numberOfPlayers];
        playerCurrentScores = new TextView[numberOfPlayers];
        playerAllScores = new TextView[numberOfPlayers];

        TextView p1Name = (TextView) findViewById(R.id.textViewPlayer1);
        TextView p2Name = (TextView) findViewById(R.id.textViewPlayer2);
        TextView p3Name = (TextView) findViewById(R.id.textViewPlayer3);
        TextView p4Name = (TextView) findViewById(R.id.textViewPlayer4);
        TextView p5Name = (TextView) findViewById(R.id.textViewPlayer5);
        TextView p6Name = (TextView) findViewById(R.id.textViewPlayer6);

        TextView p1Score = (TextView) findViewById(R.id.textViewP1Score);
        TextView p2Score = (TextView) findViewById(R.id.textViewP2Score);
        TextView p3Score = (TextView) findViewById(R.id.textViewP3Score);
        TextView p4Score = (TextView) findViewById(R.id.textViewP4Score);
        TextView p5Score = (TextView) findViewById(R.id.textViewP5Score);
        TextView p6Score = (TextView) findViewById(R.id.textViewP6Score);

        TextView p1AllScores = (TextView) findViewById(R.id.textViewP1ScoreList);
        TextView p2AllScores = (TextView) findViewById(R.id.textViewP2ScoreList);
        TextView p3AllScores = (TextView) findViewById(R.id.textViewP3ScoreList);
        TextView p4AllScores = (TextView) findViewById(R.id.textViewP4ScoreList);
        TextView p5AllScores = (TextView) findViewById(R.id.textViewP5ScoreList);
        TextView p6AllScores = (TextView) findViewById(R.id.textViewP6ScoreList);

        editTextScore = (EditText) findViewById(R.id.EditTextNewScore);

        TextView[] allPlayerNameTextViews = new TextView[6]; // this stores all 6 possible name textviews
        TextView[] allPlayerScoreTextView = new TextView[6];
        TextView[] allPlayerScoreListTextView = new TextView[6];

        allPlayerNameTextViews[0] = p1Name;
        allPlayerNameTextViews[1] = p2Name;
        allPlayerNameTextViews[2] = p3Name;
        allPlayerNameTextViews[3] = p4Name;
        allPlayerNameTextViews[4] = p5Name;
        allPlayerNameTextViews[5] = p6Name;

        allPlayerScoreTextView[0] = p1Score;
        allPlayerScoreTextView[1] = p2Score;
        allPlayerScoreTextView[2] = p3Score;
        allPlayerScoreTextView[3] = p4Score;
        allPlayerScoreTextView[4] = p5Score;
        allPlayerScoreTextView[5] = p6Score;

        allPlayerScoreListTextView[0] = p1AllScores;
        allPlayerScoreListTextView[1] = p2AllScores;
        allPlayerScoreListTextView[2] = p3AllScores;
        allPlayerScoreListTextView[3] = p4AllScores;
        allPlayerScoreListTextView[4] = p5AllScores;
        allPlayerScoreListTextView[5] = p6AllScores;

        currentScores = new int[numberOfPlayers];
        pastScores = new ArrayList();

        for (int i = 0; i < numberOfPlayers; i++) { // adds the chosen number of textviews to playerNames
            playerNames[i] = allPlayerNameTextViews[i];
            playerCurrentScores[i] = allPlayerScoreTextView[i];
            playerAllScores[i] = allPlayerScoreListTextView[i];
            playerCurrentScores[i].setVisibility(View.VISIBLE);
            playerNames[i].setVisibility(View.VISIBLE);
            playerAllScores[i].setVisibility(View.VISIBLE);
            playerNames[i].setText(receivedNames[i].toString());
            currentScores[i] = 0;
            pastScores.add(i, new ArrayList<Integer>());

        }

        setCurrentPlayer(currentPlayerNumber);

    }

    public void updateScore(View v) {
        try {
            // update the current players score - add new score to total
            int newScore = Integer.parseInt(editTextScore.getText().toString());
            int currentScore = currentScores[currentPlayerNumber] += newScore;
            pastScores.get(currentPlayerNumber).add(newScore); // adds latest score to list of all scores
            playerCurrentScores[currentPlayerNumber].setText(String.valueOf(currentScore)); // update current score
            playerAllScores[currentPlayerNumber].append("\n" + String.valueOf(newScore));
            setCurrentPlayer(currentPlayerNumber + 1); // set player to next in turn

            editTextScore.setText("");
            Log.i("", newScore + "is a number");
        } catch (NumberFormatException e) {
            Log.i("", 0 + "is not a number");
        }
    }

    public void setCurrentPlayer(int nextPlayer) {
        playerNames[currentPlayerNumber].setTextColor(Color.BLACK);
        playerCurrentScores[currentPlayerNumber].setTextColor(Color.BLACK);

        if (nextPlayer < numberOfPlayers) currentPlayerNumber = nextPlayer;
        else currentPlayerNumber = 0; // restart at player 1

        currentPlayerName = playerNames[currentPlayerNumber].getText().toString();
        playerNames[currentPlayerNumber].setTextColor(Color.YELLOW);
        playerCurrentScores[currentPlayerNumber].setTextColor(Color.YELLOW);
        Toast toast = Toast.makeText(this.getBaseContext(), currentPlayerName + "'s turn!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

    }
}
