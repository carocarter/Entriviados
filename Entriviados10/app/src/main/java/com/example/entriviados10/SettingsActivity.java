package com.example.entriviados10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.bumptech.glide.Glide;

public class SettingsActivity extends AppCompatActivity {
    private EditText editUsername, editEmail, editPassword;
    Button saveButton, deleteButton, changePicButton;
    private ImageView editImage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    final private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ImageButton imageLogout;
    private String userEmail;
    private Uri imageURL;
    private String myUri = "";
    private StorageTask uploadTask;

    String[] settingsMessages;

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
        imageLogout = findViewById(R.id.imagelogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Image");
        userEmail = user.getEmail();
        settingsMessages = getResources().getStringArray(R.array.settings_messages);


        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        saveButton.setOnClickListener(v -> saveChanges());

        deleteButton.setOnClickListener(v -> confirmDeleteAccount());

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    imageURL = result;
                    uploadImageToFirebase(imageURL);
                }
            }
        });
        changePicButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        getUserInfo();

        imageLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            // Redirige al usuario a la pantalla de inicio de sesión
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Termina la actividad actual
        });
    }

    private void getUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            firebaseFirestore.collection("usuarios").whereEqualTo("email", userEmail).get()
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
                                Toast.makeText(SettingsActivity.this, settingsMessages[0], Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, settingsMessages[1], Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveChanges() {
        //Set new values for data info
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            String newUsername = editUsername.getText().toString();
            String newPassword = editPassword.getText().toString();

            firebaseFirestore.collection("usuarios").document(userEmail)
                    .update("nombre", newUsername)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SettingsActivity.this, settingsMessages[2], Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, settingsMessages[3], Toast.LENGTH_SHORT).show();
                        }
                    });

            if (!newPassword.isEmpty()) {
                user.updatePassword(newPassword)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SettingsActivity.this, settingsMessages[4], Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SettingsActivity.this, settingsMessages[5], Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void uploadProfileImage(Uri imageUri) {
        Glide.with(this).load(imageUri).into(editImage);
    }

    private void uploadImageToFirebase(Uri imageURL) {
        //Upload the image to Firebase Storage
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.show();

        StorageReference fileRef = storageReference.child(mAuth.getCurrentUser().getUid() + ".jpg");

        uploadTask = fileRef.putFile(imageURL);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return fileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                //Image uploaded successfully, update photoURL in Firebase Realtime Database
                Uri downloadUrl = (Uri) task.getResult();
                myUri = downloadUrl.toString();

                //Retrieve user document from firebase
                db.collection("usuarios")
                        .whereEqualTo("email", userEmail)
                        .get()
                        .addOnCompleteListener(newtask -> {
                            if (newtask.isSuccessful()) {
                                for (QueryDocumentSnapshot document : newtask.getResult()) {

                                    //Update the score
                                    document.getReference().update("photoURL", myUri)
                                            .addOnSuccessListener(aVoid -> {
                                                // Update profile image in the UI using Glide
                                                Glide.with(this).load(myUri).into(editImage);
                                                Toast.makeText(SettingsActivity.this, settingsMessages[6], Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(SettingsActivity.this, settingsMessages[7], Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                Toast.makeText(this, settingsMessages[8], Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Handle upload failure
                Toast.makeText(SettingsActivity.this, settingsMessages[8], Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void confirmDeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete account");
        builder.setMessage("Are you sure? This action can not be undone");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void deleteAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //Delete user document from Firebase
            db.collection("usuarios")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // User document deleted successfully
                                        })
                                        .addOnFailureListener(e -> {
                                            // Failed to delete user document
                                        });
                            }
                        } else {
                            // Failed to retrieve user document
                        }
                    });

            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // La cuenta se ha eliminado exitosamente.
                    Toast.makeText(SettingsActivity.this, settingsMessages[9], Toast.LENGTH_SHORT).show();
                    // Redirige al usuario a la pantalla de inicio de sesión
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Termina la actividad actual
                } else {
                    // Ocurrió un error al intentar eliminar la cuenta
                    Toast.makeText(SettingsActivity.this, settingsMessages[10], Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
