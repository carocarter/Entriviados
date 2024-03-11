package com.example.entriviados10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreview;
    SharedPreferences sharedPreferences;
    Button playAgain;
    int score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreview = findViewById(R.id.textViewScore);
        playAgain = findViewById(R.id.playAgain);


        //Retrieve score from the intent
        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        scoreview.setText("Score: " + score);

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (ScoreActivity.this, SelectLevelActivity.class);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
            }
        });

        }

}
