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

        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        name = intent.getStringExtra("nombre");
        username = intent.getStringExtra("nombre");
        email = intent.getStringExtra("email");
        score = intent.getIntExtra("score", 0);
        titleUsername.setText(R.string.name);
        //profileImg.setImageURI(Uri.parse("photoURL"));
        profileScore.setText(R.string.score);
        profileEmail.setText(R.string.email);
        profileUsername.setText(R.string.name);

        //Shows user settings
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        showAllUserData();
    }

    public void showAllUserData() {
            //Retrieve data from Firebase
            firebaseFirestore.collection("usuarios")
                    .whereEqualTo("email", userEmail).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                name = documentSnapshot.getString("nombre");
                                email = documentSnapshot.getString("email");
                                Long scoreLong = documentSnapshot.getLong("score");
                                score = (scoreLong != null) ? scoreLong.intValue() : 0;
                                String imageURL = documentSnapshot.getString("photoURL");

                                titleUsername.setText(name);
                                profileUsername.setText(name);
                                profileScore.setText(String.valueOf(score));
                                profileEmail.setText(email);

                                if (imageURL != null) {
                                    Glide.with(this).load(imageURL).into(profileImg);
                                }
                            }
                        } else {
                            Toast.makeText(this, "Error: unable to retrieve data", Toast.LENGTH_SHORT).show();
                        }
                    });
    }
}