package com.example.entriviados10;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImg;
    private TextView profileScore, profileEmail, profileUsername,profilePassword, titleUsername;
    private Button editProfile;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private ImageButton logoutButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImg = findViewById(R.id.profileImg);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        titleUsername = findViewById(R.id.titleUsername);
        editProfile = findViewById(R.id.editButton);
        toolbar = findViewById(R.id.toolbar3);
        logoutButton = findViewById(R.id.imagelogout);

        setSupportActionBar(toolbar);
        showAllUserData();

        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Display total score
        profileScore = findViewById(R.id.profileScore);
        sharedPreferences = getSharedPreferences("totalScore", MODE_PRIVATE);
        int totalScore = sharedPreferences.getInt("totalScore", 0);
        profileScore.setText(totalScore);

        // Shows user settings
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passUserData();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra la sesión del usuario
                FirebaseAuth.getInstance().signOut();

                // Redirige al usuario a la pantalla de inicio de sesión
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Termina la actividad actual
            }
        });
    }

    public void showAllUserData() {
        Intent intent = getIntent();
        //Img missing
        int scoreUser = intent.getIntExtra("totalScore", 0);
        String emailUser = intent.getStringExtra("email");
        String usernameUser = intent.getStringExtra("nombre");
        String passwordUser = intent.getStringExtra("password");

        titleUsername.setText(usernameUser);
        profileScore.setText(scoreUser);
        profileUsername.setText(usernameUser);
        profileEmail.setText(emailUser);
        profilePassword.setText(passwordUser);
    }

    public void passUserData() {
        String userUsername = profileUsername.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
        Query checkUserDatabase = reference.orderByChild("nombre").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //int scoreFromDB = (int) snapshot.child(userUsername).child("totalScore").getValue();
                    //Img missing
                    String usernameFromDB = snapshot.child(userUsername).child("nombre").getValue(String.class);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                    //Img missing
                    intent.putExtra("nombre", usernameFromDB);
                    intent.putExtra("email", emailFromDB);
                    intent.putExtra("password", passwordFromDB);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}