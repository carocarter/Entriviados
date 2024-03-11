package com.example.entriviados10;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GameActivity extends AppCompatActivity {
    private Question[] questions;
    private int questionIndex;
    private int correctButtonIndex;
    private Button[] buttons;
    private Button correctButton;
    private ConstraintLayout constraintLayout;
    private ProgressBar progressBar;
    private int progress;
    private CountDownTimer timer;
    private long totalTime = 10000; //10 seconds per question
    private long remainingTime = 0;
    private boolean timerRunning = false;
    private CountDownTimer delayTimer;
    private long delay = 0;
    private int score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        progressBar = findViewById(R.id.progressBar);
        constraintLayout = findViewById(R.id.constraintlayout);

        //Retrieve values from the intent
        Intent intent = getIntent();
        if (intent != null) {
            questions = (Question[]) intent.getSerializableExtra("questions");
            questionIndex = intent.getIntExtra("questionIndex", 0);
            score = intent.getIntExtra("score", 0);

            TextView textView = findViewById(R.id.textView);
            TextView scoreTextView = findViewById(R.id.scoreTextView);

            Button button1 = findViewById(R.id.button1);
            Button button2 = findViewById(R.id.button2);
            Button button3 = findViewById(R.id.button3);
            Button button4 = findViewById(R.id.button4);

            //Set the texts
            textView.setText(questions[questionIndex].getQuestion().getText());
            scoreTextView.setText("Score: " + score);

            List<String> answerOptions = new ArrayList<>(); //List with all answer options
            answerOptions.add(questions[questionIndex].getCorrectAnswer());
            answerOptions.addAll(questions[questionIndex].getIncorrectAnswers());

            //Shuffle the list of answer options
            Collections.shuffle(answerOptions);

            //Set the text for each button
            button1.setText(answerOptions.get(0));
            button2.setText(answerOptions.get(1));
            button3.setText(answerOptions.get(2));
            button4.setText(answerOptions.get(3));
            buttons = new Button[]{button1, button2, button3, button4};

            //Store the correct question index
            correctButtonIndex = answerOptions.indexOf(questions[questionIndex].getCorrectAnswer());
            correctButton = buttons[correctButtonIndex];

            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setTag(i);
                buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int clickedButtonIndex = (int) v.getTag();
                        handleButtonClick(clickedButtonIndex);
                    }
                });
            }
            startTimer();
        }
    }

    private void startTimer(long remainingTime) {
        timerRunning = true;
        timer = new CountDownTimer(remainingTime, 100) {
            public void onTick(long millisUntilFinished) {
                progress = (int) (((float) (millisUntilFinished) / totalTime) * 100);
                progressBar.setProgress(progress);
            }

            public void onFinish() {
                correctButton.setBackgroundTintList(ContextCompat.getColorStateList(GameActivity.this, R.color.green));
                constraintLayout.setBackgroundColor(Color.parseColor("#A1FDB1AF"));
                timeBetweenQuestions();
                timerRunning = false;
            }
        }.start();
    }

    private void startTimer() {
        startTimer(totalTime);
    }


    private void handleButtonClick(int clickedButtonIndex) {
        Button clicked = buttons[clickedButtonIndex];

        //Color the clicked button
        if (clickedButtonIndex == correctButtonIndex) {
            score += (int) Math.ceil((double) progress / 10);
            clicked.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            constraintLayout.setBackgroundColor(Color.parseColor("#A1BCFEC2"));
        } else {
            clicked.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            correctButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            constraintLayout.setBackgroundColor(Color.parseColor("#A1FDB1AF"));
        }
        timer.cancel();
        timerRunning = false;
        timeBetweenQuestions();
    }

    private void startNextActivity() {
        questionIndex++; //Increment the questionIndex
        Intent intent;
        if (questionIndex < questions.length) { //Number of questions in a game
            intent = new Intent(GameActivity.this, GameActivity.class);
            intent.putExtra("questions", questions);
            intent.putExtra("questionIndex", questionIndex);
            intent.putExtra("score", score);
        } else {
            intent = new Intent(GameActivity.this, ScoreActivity.class);
            intent.putExtra("score", score);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cancel the timer
        if (timer != null) {
            timer.cancel();
        }
    }

    private void timeBetweenQuestions(long time){
        delayTimer = new CountDownTimer(time, 100) {
            public void onTick(long millisUntilFinished) {
                delay = millisUntilFinished;
            }
            public void onFinish() {
                startNextActivity();
            }
        }.start();
    }

    private void timeBetweenQuestions(){
        long totalDuration = 3000; //Time pause between questions
        timeBetweenQuestions(totalDuration);
    }

    //If the user press the back button during the game
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (delay > 0){
                //Stop the delay timer
                delayTimer.cancel();
            }

            //Stop the timer and store the remaining time
            if (timerRunning && timer != null) {
                timer.cancel();
                remainingTime = progress * (totalTime / 100);
                timerRunning = false;
            }

            //Alert that the progress will be lost
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to go back? Your progress will be lost.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //User clicked OK button, finishing the activity
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //User clicked Cancel button, resuming the timer
                            if (!timerRunning && delay == 0) {
                                startTimer(remainingTime);
                                timerRunning = true;
                            }
                            if(delay > 0) {
                                timeBetweenQuestions(delay);
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
