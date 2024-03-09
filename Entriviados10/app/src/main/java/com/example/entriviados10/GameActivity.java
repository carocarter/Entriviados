package com.example.entriviados10;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
    private CountDownTimer timer;



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

    private void startTimer() {
        // Timer for 10 seconds
        final long totalDuration = 10000;
        timer = new CountDownTimer(totalDuration, 100) {
            public void onTick(long millisUntilFinished) {
                int progress = (int) (((float) millisUntilFinished / totalDuration) * 100);
                progressBar.setProgress(progress);
            }

            public void onFinish() {
                correctButton.setBackgroundTintList(ContextCompat.getColorStateList(GameActivity.this, R.color.green));
                constraintLayout.setBackgroundColor(Color.parseColor("#A1FDB1AF"));
                TimeBetweenQuestions();
            }
        }.start();
    }

    private void handleButtonClick(int clickedButtonIndex) {
        timer.cancel();
        Button clicked = buttons[clickedButtonIndex];

        // Color the clicked button
        if (clickedButtonIndex == correctButtonIndex) {
            clicked.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            constraintLayout.setBackgroundColor(Color.parseColor("#A1BCFEC2"));
        } else {
            clicked.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            correctButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            constraintLayout.setBackgroundColor(Color.parseColor("#A1FDB1AF"));
        }
        TimeBetweenQuestions();
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
