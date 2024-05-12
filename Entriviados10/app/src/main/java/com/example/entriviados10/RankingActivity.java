package com.example.entriviados10;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private androidx.appcompat.widget.Toolbar toolbar;
    private Button goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        toolbar = findViewById(R.id.rankingToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //Retrieve user data
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .orderBy("score", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<com.example.entriviados10.User> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imageURL = document.getString("photoURL");
                            String name = document.getString("nombre");
                            String password = "";
                            Integer score = document.getLong("score") != null ? document.getLong("score").intValue() : null;

                            //Check if any of the required values are null before adding to the list
                            if (imageURL != null && name != null && score != null) {
                                userList.add(new User(imageURL, name, password, score));
                            }
                        }
                        UserAdapter adapter = new UserAdapter(userList, this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Error loading ranking", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
