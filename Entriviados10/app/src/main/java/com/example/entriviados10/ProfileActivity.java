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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        profileScore = findViewById(R.id.profileScore);
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
        toolbar.setNavigationOnClickListener(v -> finish());

        // Shows user settings
        editProfile.setOnClickListener(v -> passUserData());

        logoutButton.setOnClickListener(v -> {
            // Cierra la sesión del usuario
            FirebaseAuth.getInstance().signOut();

            // Redirige al usuario a la pantalla de inicio de sesión
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Termina la actividad actual
        });
    }

    public void showAllUserData() {
        //Retrieve score from firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = mAuth.getCurrentUser().getEmail();
        db.collection("usuarios")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Long score = document.getLong("score");
                            String name = document.getString("nombre");
                            String email = document.getString("email");
                            String password = document.getString("password");
                            if (score != null) {
                                profileScore.setText(String.valueOf(score.intValue()));
                            }
                            if (email != null) {
                                profileEmail.setText(email);
                            }
                            if (name != null) {
                                titleUsername.setText(name);
                                profileUsername.setText(name);
                            }
                            if (password != null) {
                                profilePassword.setText(password);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error: unable to retrieve score", Toast.LENGTH_SHORT).show();
                    }
                });
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

        /*
        public void confirmDeleteAccount(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Eliminar cuenta");
            builder.setMessage("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.");
            builder.setPositiveButton("Sí", (dialog, which) -> eliminarCuenta());
            builder.setNegativeButton("Cancelar", (dialog, which) -> {

            });
            builder.show();
        }

        private void eliminarCuenta() {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // La cuenta se ha eliminado exitosamente.
                                Toast.makeText(PerfilActivity.this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                                // Redirige al usuario a la pantalla de inicio de sesión
                                Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Termina la actividad actual
                            } else {
                                // Ocurrió un error al intentar eliminar la cuenta
                                Toast.makeText(PerfilActivity.this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                            }
                        });
            }*/

    }
}