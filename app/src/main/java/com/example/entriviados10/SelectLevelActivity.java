package com.example.entriviados10;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SelectLevelActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Button buttonEasy, buttonMedium, buttonHard;
    ImageButton profileButton;
    SharedPreferences sharedPreferences;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_select);
        profileButton = findViewById(R.id.profileButton);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectLevelActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });

        buttonEasy = findViewById(R.id.buttoneasy);
        buttonMedium = findViewById(R.id.buttonmedium);
        buttonHard = findViewById(R.id.buttonhard);

        buttonEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendGetRequest("easy");
            }
        });

        buttonMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendGetRequest("medium");
            }
        });

        buttonHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendGetRequest("hard");
            }
        });
        TextView totalScoreView = findViewById(R.id.scoreTextView);

        //Retrieve score from the preferences

        sharedPreferences = getSharedPreferences("totalScore", MODE_PRIVATE);
        int totalScore = sharedPreferences.getInt("totalScore", 0);

        //If the score is zero the Total score isn't shown
        if (totalScore > 0){
            totalScoreView.setVisibility(View.VISIBLE);
            totalScoreView.setText("Total score:\n" + totalScore);
        } else {
            totalScoreView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (i){
            case 0:
                break;
    }
    transaction.commit();
    }

    private void sendGetRequest(String difficulty) {
        OkHttpClient client = new OkHttpClient();

        //Changes in the number of questions per round should be added to the parameters
        Request request = new Request.Builder()
                .url("https://the-trivia-api.com/v2/questions?difficulties=" + difficulty)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonResponse = response.body().string();

                    Gson gson = new Gson();
                    Question[] questions = gson.fromJson(jsonResponse, Question[].class);

                    //Start GameActivity and add questions and the index of the question to be incremented
                    Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                    intent.putExtra("questions", questions);
                    intent.putExtra("questionIndex", 0);
                    intent.putExtra("score", 0);
                    startActivity(intent);
                } else {
                    //Error message
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(SelectLevelActivity.this)
                                    .setMessage("Error: Server failure")
                                    .setPositiveButton("OK", null) // You can add an OnClickListener here if needed
                                    .show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //Error message
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(SelectLevelActivity.this)
                                .setMessage("Error: Network failure")
                                .setPositiveButton("OK", null) // You can add an OnClickListener here if needed
                                .show();
                    }
                });
            }
        });
    }
}