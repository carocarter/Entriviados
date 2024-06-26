package com.example.entriviados10;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
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
    private ImageView imageView, splashGameBg, splashGameAnim;
    private ProgressBar progressBar;
    private int progress;
    private CountDownTimer timer;
    private long totalTime = 10000; //10 seconds per question
    private long remainingTime = 0;
    private boolean timerRunning = false;
    private CountDownTimer delayTimer;
    private long delay = 0;
    private int score;
    private String level;
    private MediaPlayer correctSound, wrongSound;
    private static final String MUSIC_ON_OFF_KEY = "music_on_off";
    SharedPreferences sharedPreferences;
    private boolean isMusicPlaying = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        progressBar = findViewById(R.id.progressBar);
        constraintLayout = findViewById(R.id.constraintlayout);
        imageView = findViewById(R.id.imageView3);
        splashGameAnim = findViewById(R.id.splashGameAnim);
        splashGameBg = findViewById(R.id.splashGameBg);
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) splashGameAnim.getDrawable();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isMusicPlaying = sharedPreferences.getBoolean(MUSIC_ON_OFF_KEY, false);

        correctSound = MediaPlayer.create(this,R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.error);

        //Retrieve values from the intent
        Intent intent = getIntent();
        if (intent != null) {
            questions = (Question[]) intent.getSerializableExtra("questions");
            questionIndex = intent.getIntExtra("questionIndex", 0);
            score = intent.getIntExtra("score", 0);
            level = intent.getStringExtra("level");

            TextView textView = findViewById(R.id.textView);
            TextView scoreTextView = findViewById(R.id.scoreTextView);

            Button button1 = findViewById(R.id.button1);
            Button button2 = findViewById(R.id.button2);
            Button button3 = findViewById(R.id.button3);
            Button button4 = findViewById(R.id.button4);

            //Set the texts
            textView.setText(questions[questionIndex].getQuestion().getText());
            scoreTextView.setText("Score:\n" + score);

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

            if (questionIndex == 0){
                splashGameBg.setVisibility(View.VISIBLE);
                splashGameAnim.setVisibility(View.VISIBLE);
                drawable.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        splashGameBg.setVisibility(View.GONE);
                        splashGameAnim.setVisibility(View.GONE);
                        startTimer();
                    }
                }, 3700);
            } else {
                startTimer();
            }

            //If the user press the back button during the game
            OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
            dispatcher.addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    myBackPressedHandler();
                }
            });
        }
    }

    private double getScoreMultiplier(String level) {
        switch (level) {
            case "easy":
                return 1.0;
            case "medium":
                return 1.25;
            case "hard":
                return 1.5;
            default:
                return 1.0;
        }
    }

    private void startTimer(long remainingTime) {
        timerRunning = true;
        timer = new CountDownTimer(remainingTime, 100) {
            public void onTick(long millisUntilFinished) {
                progress = (int) (((float) (millisUntilFinished) / totalTime) * 100);
                progressBar.setProgress(progress);
            }

            @SuppressLint("ResourceAsColor")
            public void onFinish() {
                correctButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(GameActivity.this, R.color.green)));
                constraintLayout.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.background_error));
                imageView.setBackground(getDrawable(R.drawable.error_bg));
                timeBetweenQuestions();
                timerRunning = false;
            }
        }.start();
    }

    private void startTimer() {
        startTimer(totalTime);
    }


    @SuppressLint("ResourceAsColor")
    private void handleButtonClick(int clickedButtonIndex) {
        Button clicked = buttons[clickedButtonIndex];

        double multiplier = getScoreMultiplier(level);

        //Color the clicked button
        if (clickedButtonIndex == correctButtonIndex) {
            score += (int) Math.ceil((double) progress / 10 * multiplier);
            clicked.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(GameActivity.this, R.color.green)));
            constraintLayout.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.background_correct));
            imageView.setBackground(getDrawable(R.drawable.correct_bg));
            if(isMusicPlaying) {
                correctSound.start();
            }
        } else {
            clicked.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(GameActivity.this, R.color.button_error)));
            correctButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(GameActivity.this, R.color.green)));
            constraintLayout.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.background_error));
            imageView.setBackground(getDrawable(R.drawable.error_bg));
            if(isMusicPlaying){
                wrongSound.start();
            }
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
            intent.putExtra("level", level);
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
    public void myBackPressedHandler(){
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
    }
}
