package com.example.entriviados10;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImg;
    private TextView profileScore, profileEmail, profileUsername, titleUsername;
    private String name, username, email;
    private int score;
    private Button editProfile;
    private ImageButton imageLogout;
    private Toolbar toolbar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
    String userEmail = mAuth.getCurrentUser().getEmail();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        titleUsername = findViewById(R.id.titleUsername);
        profileScore = findViewById(R.id.profileScore);
        profileImg = findViewById(R.id.profileImg);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        editProfile = findViewById(R.id.editButton);
        toolbar = findViewById(R.id.toolbar3);
        imageLogout = findViewById(R.id.imagelogout);

        setSupportActionBar(toolbar);
        showAllUserData();

        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        name = intent.getStringExtra("nombre");
        username = intent.getStringExtra("nombre");
        email = intent.getStringExtra("email");
        score = intent.getIntExtra("score", 0);
        titleUsername.setText("nombre");
        //profileImg.setImageURI(Uri.parse("photoURL"));
        profileScore.setText("score");
        profileEmail.setText("email");
        profileUsername.setText("nombre");

        //Shows user settings
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
                //passUserData();
            }
        });

        imageLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                // Redirige al usuario a la pantalla de inicio de sesión
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Termina la actividad actual
            }
        });
    }

    public void showAllUserData() {
            //Retrieve data from Firebase
            firebaseFirestore.collection("usuarios")
                    .whereEqualTo("email", userEmail).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                //Img
                                name = documentSnapshot.getString("nombre");
                                email = documentSnapshot.getString("email");
                                Long scoreLong = documentSnapshot.getLong("score");
                                score = (scoreLong != null) ? scoreLong.intValue() : 0;

                                titleUsername.setText(name);
                                profileUsername.setText(name);
                                profileScore.setText(String.valueOf(score));
                                profileEmail.setText(email);
                            }
                        } else {
                            Toast.makeText(this, "Error: unable to retrieve data", Toast.LENGTH_SHORT).show();
                        }
                    });

        /*databaseReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String name = dataSnapshot.child("nombre").getValue(String.class);
                        String score = dataSnapshot.child("score").getValue(String.class);
                        String password = dataSnapshot.child("password").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String imageURL = dataSnapshot.child("photoURL").getValue(String.class);

                        if (name != null) {
                            titleUsername.setText(name);
                            profileUsername.setText(name);
                        }

                        if (score != null) {
                            profileScore.setText(score);
                        }

                        if (email != null) {
                            profileEmail.setText(email);
                        }

                        if (imageURL != null) {
                            Glide.with(ProfileActivity.this).load(imageURL).into(profileImg);
                        }
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Error: no data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }
    
    /*public void passUserData() {
        String userUsername = profileUsername.getText().toString().trim();

        Query checkUserDatabase = databaseReference.orderByChild("nombre").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //int scoreFromDB = (int) snapshot.child(userUsername).child("totalScore").getValue();
                    //Img missing
                    String usernameFromDB = snapshot.child(userUsername).child("nombre").getValue(String.class);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);

                    Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                    //Img missing
                    intent.putExtra("nombre", usernameFromDB);
                    intent.putExtra("email", emailFromDB);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }*/
}