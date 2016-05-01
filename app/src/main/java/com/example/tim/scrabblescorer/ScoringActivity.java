package com.example.tim.scrabblescorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class ScoringActivity extends Activity implements SensorEventListener {

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
    Button backToResultsButton;

    // sensor stuff for BoardShake accelerometer
    private SensorManager senSensorManager;
    private Sensor senAcceleromter;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);

        getIntentExtras();

        setNameTextViews();
        setCurrentScoreTextViews();
        setAllScoreListViews();
        setAddScoreEditText();
        setCurrentPlayer(currentPlayerNumber);
        setUpAccelerometer();
    }

    // gets list of names and sets number of players
    public void getIntentExtras() {
        Intent getNames = getIntent();
        receivedNames = getNames.getStringArrayExtra("PLAYER_NAMES");
        numberOfPlayers = receivedNames.length;
    }

    // create TextViews to display player names
    public void setNameTextViews() {
        namesArrayTextView = new TextView[numberOfPlayers];
        LinearLayout namesLayout = (LinearLayout) findViewById(R.id.namesLinearLayout);

        for (int i = 0; i < numberOfPlayers; i++) {
            TextView nameTextView = new TextView(this);
            nameTextView.setText(receivedNames[i]);
            nameTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            nameTextView.setGravity(Gravity.CENTER);
            nameTextView.setTypeface(Typeface.SERIF, Typeface.BOLD);
            nameTextView.setTextColor(getResources().getColor(R.color.text_colour));
            nameTextView.setTextSize(25 - (numberOfPlayers * 2));
            namesLayout.setWeightSum(numberOfPlayers);
            namesLayout.addView(nameTextView);
            namesArrayTextView[i] = nameTextView;  // store to use later
        }
    }

    // creates the TextViews to display current scores at bottom of screen
    public void setCurrentScoreTextViews() {
        currentScoresTextViews = new TextView[numberOfPlayers];
        LinearLayout scoresLayout = (LinearLayout) findViewById(R.id.scoresLinearLayout);
        currentScores = new int[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            TextView scoreTextView = new TextView(this);
            scoreTextView.setText(R.string.initialise_scores_0);
            scoreTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            scoreTextView.setGravity(Gravity.CENTER);
            // set text properties
            scoreTextView.setTextSize(25 - (numberOfPlayers * 2));
            scoreTextView.setTypeface(Typeface.SERIF, Typeface.BOLD);
            scoreTextView.setTextColor(getResources().getColor(R.color.text_colour));
            scoresLayout.setWeightSum(numberOfPlayers);
            scoresLayout.addView(scoreTextView);
            currentScoresTextViews[i] = scoreTextView; // store to use later
        }
    }

    // create scrolling ListViews to display ongoing lists of score for each player.
    // 2d List stored
    public void setAllScoreListViews() {
        playerAllScoresListViews = new ListView[numberOfPlayers];
        LinearLayout allScoresLayout = (LinearLayout) findViewById(R.id.allScoresLinearLayout);

        pastScores = new ArrayList();  // List of ArrayLists
        arrayAdapters = new ArrayAdapter[numberOfPlayers];

        for (int i = 0; i < numberOfPlayers; i++) {
            // create ListView for each player for ongoing scores
            final ListView allScoresListView = new ListView(this);
            allScoresListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            allScoresLayout.setWeightSum(numberOfPlayers);
            allScoresLayout.addView(allScoresListView);
            playerAllScoresListViews[i] = allScoresListView;
            pastScores.add(new ArrayList<Integer>()); // add List to each player for scores
            currentScores[i] = 0; // initialise all player scores to 0
            // create Adapter for each player and store ListView in it to show scores
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.activity_listview, pastScores.get(i));
            arrayAdapters[i] = adapter;
            allScoresListView.setAdapter(adapter);
        }
    }

    // set up the score EditText to add new score to current player.
    public void setAddScoreEditText() {
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
    }

    // method called to update the current player score
    public void updateScore(View v) {
        try {
            int newScore = Integer.parseInt(editTextScore.getText().toString());
            currentScores[currentPlayerNumber] += newScore; // update players score in array
            pastScores.get(currentPlayerNumber).add(newScore); // adds latest score to list of all scores

            playerAllScoresListViews[currentPlayerNumber].setAdapter(arrayAdapters[currentPlayerNumber]);
            scrollDown(playerAllScoresListViews[currentPlayerNumber]);
            currentScoresTextViews[currentPlayerNumber].setText(String.valueOf(currentScores[currentPlayerNumber])); // update players current score

            setCurrentPlayer(currentPlayerNumber + 1); // set player to next in turn
            editTextScore.setText(R.string.clear_edit_text);

            Log.i("", newScore + "is a number");
        } catch (NumberFormatException e) {
            Log.i("", 0 + "is not a number");
        }
    }

    // sets the current player to the argument passed - nextPlayer, changes text colours appropriately
    public void setCurrentPlayer(int nextPlayer) {
        namesArrayTextView[currentPlayerNumber].setTextColor(getResources().getColor(R.color.text_colour));
        currentScoresTextViews[currentPlayerNumber].setTextColor(getResources().getColor(R.color.text_colour));

        if (nextPlayer < numberOfPlayers) currentPlayerNumber = nextPlayer;
        else currentPlayerNumber = 0; // restart at player 1

        currentPlayerName = namesArrayTextView[currentPlayerNumber].getText().toString();
        namesArrayTextView[currentPlayerNumber].setTextColor(Color.YELLOW);
        currentScoresTextViews[currentPlayerNumber].setTextColor(Color.YELLOW);
    }

    public void openDictionary(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://scrabblewordfinder.org/dictionary-checker"));
        startActivity(intent);
    }

    // removes the last score added and sets current player back one. Keeps going back to game start
    public void undoLastScore(View v) {
        if (pastScores.get(0).size() == 0)
            Toast.makeText(this.getBaseContext(), R.string.toast_nothing_to_undo, Toast.LENGTH_LONG).show();
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

    // finish scoring and go to results activity
    public void finishGame(View v) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you really finished?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    goToResults();
                    setUpPostGameScreen();

                    dialog.dismiss();
                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    public void goToResults(){

        Intent goToResults = new Intent(ScoringActivity.this, GameResultsActivity.class);
        goToResults.putExtra("SCORES", currentScores);
        goToResults.putExtra("NAMES", receivedNames);

        startActivity(goToResults);

    }

    // removes game play views and replaces with a 'back to results' button
    public void setUpPostGameScreen() {
        backToResultsButton = new Button(this); // button to display after game finish
        backToResultsButton.setTextColor(getResources().getColor(R.color.text_colour));
        backToResultsButton.setBackgroundColor(getResources().getColor(R.color.button_colour));
        // delay for 1 second so user does not see the buttons disappear
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonRowLinearLayout);
                buttonLayout.removeAllViews();
                editTextScore.setVisibility(View.GONE);
                namesArrayTextView[currentPlayerNumber].setTextColor(getResources().getColor(R.color.text_colour));
                currentScoresTextViews[currentPlayerNumber].setTextColor(getResources().getColor(R.color.text_colour));
                backToResultsButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                backToResultsButton.setText(R.string.back_to_results);
                buttonLayout.setGravity(Gravity.CENTER);
                buttonLayout.addView(backToResultsButton);

                backToResultsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToResults(); // better way to do this??
                    }
                });
            }
        }, 1000);

    }

    // helper methods for keyboard and scrolling
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

    public void setUpAccelerometer() {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAcceleromter = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAcceleromter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // watch accelerometer to see if someone has shaken the scrabble board
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    Intent goToBoardShake = new Intent(ScoringActivity.this, BoardShakeActivity.class);
                    startActivity(goToBoardShake);
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAcceleromter, SensorManager.SENSOR_DELAY_NORMAL);
    }

}

