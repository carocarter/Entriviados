package com.example.entriviados10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreview;
    Button playAgain;
    int score;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreview = findViewById(R.id.textViewScore);
        playAgain = findViewById(R.id.playAgain);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Retrieve score from the intent
        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        scoreview.setText("Final score:\n" + score);

        //Retrieve old score and update it with the new score
        updateScore();

        playAgain.setOnClickListener(view -> {
            Intent intent1 = new Intent(ScoreActivity.this, SelectLevelActivity.class);
            startActivity(intent1);
            finish();
        });
    }

    private void updateScore() {
        String userEmail = mAuth.getCurrentUser().getEmail();

        //Retrieve user document from firebase
        db.collection("usuarios")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Get the old score
                            int oldScore = document.getLong("score").intValue();
                            int totalScore = oldScore + score;

                            //Update the score
                            document.getReference().update("score", totalScore)
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error: unable to update score", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Error: unable to retrieve score", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}