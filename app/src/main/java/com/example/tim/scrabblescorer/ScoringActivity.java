package com.example.tim.scrabblescorer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ScoringActivity extends AppCompatActivity {

    String receivedNames[]; // stores names from previous activity
    int numberOfPlayers;        // size of receivedNames
    TextView[] namesArrayTextView;      // to display names
    TextView[] currentScoresTextViews; // to display current scores

    int currentPlayerNumber = 0;  //starts at player 1 (0)
    String currentPlayerName;

    ArrayAdapter<Integer>[] arrayAdapters; // for ListViews to display scores
    ListView[] playerAllScoresListViews;
    int[] currentScores;
    List<List<Integer>> pastScores; // 2d List for keeping track of scores
    EditText editTextScore;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);

        Intent getNames = getIntent();
        receivedNames = getNames.getStringArrayExtra("PLAYER_NAMES");
        numberOfPlayers = receivedNames.length;

        namesArrayTextView = new TextView[numberOfPlayers];
        currentScoresTextViews = new TextView[numberOfPlayers];
        playerAllScoresListViews = new ListView[numberOfPlayers];

        LinearLayout namesLayout = (LinearLayout) findViewById(R.id.namesLinearLayout);
        LinearLayout scoresLayout = (LinearLayout) findViewById(R.id.scoresLinearLayout);
        LinearLayout allScoresLayout = (LinearLayout) findViewById(R.id.allScoresLinearLayout);

        currentScores = new int[numberOfPlayers];
        pastScores = new ArrayList();  // List of ArrayLists
        arrayAdapters = new ArrayAdapter[numberOfPlayers];


        for (int i = 0; i < numberOfPlayers; i++) {

            // Player names
            TextView nameTextView = new TextView(this);
            nameTextView.setText(receivedNames[i]);
            nameTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            nameTextView.setGravity(Gravity.CENTER);
            namesLayout.setWeightSum(numberOfPlayers);
            nameTextView.setTextSize(25 - (numberOfPlayers * 2));
            nameTextView.setTypeface(nameTextView.getTypeface(), Typeface.BOLD);
            namesLayout.addView(nameTextView);
            namesArrayTextView[i] = nameTextView;  // store to use later

            // create TextViews for each player total score
            TextView scoreTextView = new TextView(this);
            scoreTextView.setText("0");
            scoreTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            scoreTextView.setGravity(Gravity.CENTER);
            // set text properties
            scoreTextView.setTypeface(nameTextView.getTypeface(), Typeface.BOLD);
            scoreTextView.setTextSize(25 - (numberOfPlayers * 2));
            scoresLayout.setWeightSum(numberOfPlayers);
            scoresLayout.addView(scoreTextView);
            currentScoresTextViews[i] = scoreTextView; // store to use later

            // create ListView for each player for ongoing scores
            final ListView allScoresListView = new ListView(this);
            allScoresListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            allScoresLayout.setWeightSum(numberOfPlayers);
            allScoresLayout.addView(allScoresListView);

            //?????????????????????????????//
            playerAllScoresListViews[i] = allScoresListView;


            pastScores.add(new ArrayList<Integer>()); // add List to each player for scores
            currentScores[i] = 0; // initialise all player scores to 0

            // create Adapter for each player and store ListView in it to show scores
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.activity_listview, pastScores.get(i));
            arrayAdapters[i] = adapter;
            allScoresListView.setAdapter(adapter);

            scrollView = (ScrollView) findViewById(R.id.scrollView);
            // for entering new score
            editTextScore = (EditText) findViewById(R.id.editTextNewScore);
            editTextScore.setOnKeyListener(new View.OnKeyListener() {


                // set soft-input to update score when click enter
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        updateScore(editTextScore);
                        return true;
                    }
                    return false;
                }
            });
            // set soft-input to update score when user clicks outside keyboard
            editTextScore.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                        updateScore(editTextScore);
                    }
                }
            });

            setCurrentPlayer(currentPlayerNumber);
        }
    }

    public void updateScore(View v) {
        try {
            // update the current players score - add new score to total
            int newScore = Integer.parseInt(editTextScore.getText().toString());
            currentScores[currentPlayerNumber] += newScore; // update players score in array
            pastScores.get(currentPlayerNumber).add(newScore); // adds latest score to list of all scores

            playerAllScoresListViews[currentPlayerNumber].setAdapter(arrayAdapters[currentPlayerNumber]);
            scrollDown(playerAllScoresListViews[currentPlayerNumber]);
            currentScoresTextViews[currentPlayerNumber].setText(String.valueOf(currentScores[currentPlayerNumber])); // update players current score


            setCurrentPlayer(currentPlayerNumber + 1); // set player to next in turn
            editTextScore.setText("");

            Log.i("", newScore + "is a number");
        } catch (NumberFormatException e) {
            Log.i("", 0 + "is not a number");
        }
    }

    public void setCurrentPlayer(int nextPlayer) {
        namesArrayTextView[currentPlayerNumber].setTextColor(Color.BLACK);
        currentScoresTextViews[currentPlayerNumber].setTextColor(Color.BLACK);

        if (nextPlayer < numberOfPlayers) currentPlayerNumber = nextPlayer;
        else currentPlayerNumber = 0; // restart at player 1

        currentPlayerName = namesArrayTextView[currentPlayerNumber].getText().toString();
        namesArrayTextView[currentPlayerNumber].setTextColor(Color.YELLOW);
        currentScoresTextViews[currentPlayerNumber].setTextColor(Color.YELLOW);


        //  Toast toast = Toast.makeText(this.getBaseContext(), currentPlayerName + "'s turn!", Toast.LENGTH_LONG);
        //   toast.setGravity(Gravity.CENTER, 0, 0);
        // toast.show();
    }

    public void undoLastScore(View v) {
        if (pastScores.get(0).size() == 0)
            Toast.makeText(this.getBaseContext(), "Nothing to undo", Toast.LENGTH_LONG).show();
        else {
            if (currentPlayerNumber == 0) {
                setCurrentPlayer(numberOfPlayers - 1);
            } else setCurrentPlayer(currentPlayerNumber - 1);

            int scoreToRemove = pastScores.get(currentPlayerNumber).get(pastScores.get(currentPlayerNumber).size() - 1);
            currentScores[currentPlayerNumber] -= scoreToRemove; //update current score in array
            currentScoresTextViews[currentPlayerNumber].setText(String.valueOf(currentScores[currentPlayerNumber])); // update current score text
            // remove the score from list of player scores
            pastScores.get(currentPlayerNumber).remove(pastScores.get(currentPlayerNumber).size() - 1);
            // update listView
            playerAllScoresListViews[currentPlayerNumber].setAdapter(arrayAdapters[currentPlayerNumber]);

        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void scrollDown(View v) {
        playerAllScoresListViews[currentPlayerNumber].post(new Runnable() {

            public void run() {
                playerAllScoresListViews[currentPlayerNumber].setSelection(playerAllScoresListViews[currentPlayerNumber].getCount() - 1);
            }
        });

    }
}

