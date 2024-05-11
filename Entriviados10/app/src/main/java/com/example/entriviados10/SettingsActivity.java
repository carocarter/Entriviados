package com.example.entriviados10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editUsername, editEmail, editPassword;
    Button saveButton, deleteButton, changePicButton;
    private ImageView editImage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ActivityResultLauncher<String> galleryLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        changePicButton = findViewById(R.id.changePicButton);
        editImage = findViewById(R.id.editProfileImg);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        if (o != null) {
                            Glide.with(SettingsActivity.this).load(o).into(editImage);
                        }
                    }
                });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteAccount();
            }
        });

        changePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            changeProfilePicture();
            }
        });

        getUserInfo();
    }

    private void changeProfilePicture() {
        galleryLauncher.launch("image/*");
    }

    private void getUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userUid = user.getUid();
            firebaseFirestore.collection("usuarios").whereEqualTo("email", userUid).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String email = documentSnapshot.getString("email");
                                String username = documentSnapshot.getString("username");
                                String password = documentSnapshot.getString("password");
                                String imageUrl = documentSnapshot.getString("photoURL");

                                editEmail.setText(email);
                                editUsername.setText(username);
                                editPassword.setText(password);

                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(SettingsActivity.this)
                                            .load(imageUrl)
                                            .into(editImage);
                                }
                            } else {
                                Toast.makeText(SettingsActivity.this, "No user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveChanges() {
        //Set new values for data info
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userUid = user.getUid();
            String newEmail = editEmail.getText().toString();
            String newUsername = editUsername.getText().toString();
            String newPassword = editPassword.getText().toString();

            firebaseFirestore.collection("usuarios").document(userUid)
                    .update("email", newEmail, "nombre", newUsername)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SettingsActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                        }
                    });

            if (!newPassword.isEmpty()) {
                user.updatePassword(newPassword)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SettingsActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SettingsActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    public void confirmDeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete account");
        builder.setMessage("Are you sure? This action can not be undone");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarCuenta();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void eliminarCuenta() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // La cuenta se ha eliminado exitosamente.
                        Toast.makeText(SettingsActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                        // Redirige al usuario a la pantalla de inicio de sesión
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Termina la actividad actual
                    } else {
                        // Ocurrió un error al intentar eliminar la cuenta
                        Toast.makeText(SettingsActivity.this, "Error deleting account", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
