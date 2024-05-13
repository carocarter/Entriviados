package com.example.entriviados10;

import android.os.Bundle;
import android.widget.Toast;

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
    private String registeredUserName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        toolbar = findViewById(R.id.rankingToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //Retrieve user data
        mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser().getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            registeredUserName = document.getString("nombre"); // Get the registered user's name
                        }
                    }
                });
        db.collection("usuarios")
                .orderBy("score", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<com.example.entriviados10.User> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imageURL = document.getString("photoURL");
                            String name = document.getString("nombre");
                            Long score = document.getLong("score");

                            //Check if any of the required values are null before adding to the list
                            if ((imageURL != null && name != null && score != null) && (score != 0)) {
                                userList.add(new User(name, imageURL,score));
                            }
                        }
                        UserAdapter adapter = new UserAdapter(userList, this, registeredUserName);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Error loading ranking", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
