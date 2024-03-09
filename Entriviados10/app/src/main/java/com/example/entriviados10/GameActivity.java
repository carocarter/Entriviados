package com.example.entriviados10;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GameActivity extends AppCompatActivity {
    private Question[] questions;
    private int questionIndex;
    private ProgressBar progressBar;
    private CountDownTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        progressBar = findViewById(R.id.progressBar);

        //Retrieve values from the intent
        Intent intent = getIntent();
        if (intent != null) {
            questions = (Question[]) intent.getSerializableExtra("questions");
            questionIndex = intent.getIntExtra("questionIndex", 0);

            TextView textView = findViewById(R.id.textView);
            Button button1 = findViewById(R.id.button1);
            Button button2 = findViewById(R.id.button2);
            Button button3 = findViewById(R.id.button3);
            Button button4 = findViewById(R.id.button4);

            //Set the texts
            textView.setText(questions[questionIndex].getQuestion().getText());

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

            //Store the correct question index
            final int correctButtonIndex = answerOptions.indexOf(questions[questionIndex].getCorrectAnswer());

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick(0, correctButtonIndex);
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick(1, correctButtonIndex);
                }
            });

            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick(2, correctButtonIndex);
                }
            });

            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick(3, correctButtonIndex);
                }
            });

            startTimer();
        } else {
            //----! Error message + go back?
            Toast.makeText(GameActivity.this, "Error: Couldn't load the question", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTimer() {
        // Timer for 10 seconds
        final long totalDuration = 10000;
        timer = new CountDownTimer(totalDuration, 100) {
            public void onTick(long millisUntilFinished) {
                int progress = (int) (((float) millisUntilFinished / totalDuration) * 100);
                progressBar.setProgress(progress);
            }

            public void onFinish() {
                startNextActivity();
            }
        }.start();
    }



    private void handleButtonClick(int clickedButtonIndex, int correctButtonIndex) {
        timer.cancel();
        Button clicked = getClickedButton(clickedButtonIndex);
        Button correctButton = getCorrectButton(correctButtonIndex);
        Button[] incorrectButton = getIncorrectButton(correctButtonIndex);

        if (clicked == correctButton) {
            clicked.setBackgroundTintList(getResources().getColorStateList(R.color.green));
            TimeBetweenQuestions();
        } else {
            correctButton.setBackgroundTintList(getResources().getColorStateList(R.color.green));
            incorrectButton[0].setBackgroundTintList(getResources().getColorStateList(R.color.red));
            incorrectButton[1].setBackgroundTintList(getResources().getColorStateList(R.color.red));
            incorrectButton[2].setBackgroundTintList(getResources().getColorStateList(R.color.red));
            TimeBetweenQuestions();
        }
    }

    private void startNextActivity() {
        questionIndex++; //Increment the questionIndex
        Intent intent;
        if (questionIndex < questions.length) { //Number of questions in a game
            //----! Exchange for the end of game Activity!!
            intent = new Intent(GameActivity.this, GameActivity.class);
            intent.putExtra("questions", questions);
            intent.putExtra("questionIndex", questionIndex);
        } else {
            intent = new Intent(GameActivity.this, SelectLevelActivity.class);
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

    private Button getClickedButton(int clickedButtonIndex){
        Button clicked = null;
        switch (clickedButtonIndex){
            case 0:
                clicked = findViewById(R.id.button1);
                break;
            case 1:
                clicked = findViewById(R.id.button2);
                break;
            case 2:
                clicked = findViewById(R.id.button3);
                break;
            case 3:
                clicked = findViewById(R.id.button4);
                break;
        }
        return clicked;
    }

    private Button getCorrectButton(int correctButtonIndex){
        Button correctButton = null;
        switch (correctButtonIndex){
            case 0:
                correctButton = findViewById(R.id.button1);
                break;
            case 1:
                correctButton = findViewById(R.id.button2);
                break;
            case 2:
                correctButton = findViewById(R.id.button3);
                break;
            case 3:
                correctButton = findViewById(R.id.button4);
                break;
        }
        return correctButton;
    }

    private Button[] getIncorrectButton(int correctButtonIndex){
        Button[] incorrectButton = new Button[3];
        switch (correctButtonIndex){
            case 0:
                incorrectButton[0] = findViewById(R.id.button2);
                incorrectButton[1] = findViewById(R.id.button3);
                incorrectButton[2] = findViewById(R.id.button4);
                break;
            case 1:
                incorrectButton[0] = findViewById(R.id.button1);
                incorrectButton[1] = findViewById(R.id.button3);
                incorrectButton[2] = findViewById(R.id.button4);
                break;
            case 2:
                incorrectButton[0] = findViewById(R.id.button1);
                incorrectButton[1] = findViewById(R.id.button2);
                incorrectButton[2] = findViewById(R.id.button4);
                break;
            case 3:
                incorrectButton[0] = findViewById(R.id.button1);
                incorrectButton[1] = findViewById(R.id.button2);
                incorrectButton[2] = findViewById(R.id.button3);
                break;
        }
        return incorrectButton;
    }

    private void TimeBetweenQuestions(){
        long totalDuration = 3000;
        timer = new CountDownTimer(totalDuration, 100) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                startNextActivity();
            }
        }.start();
    }
}
