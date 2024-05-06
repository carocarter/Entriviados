package com.example.entriviados10;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private EditText editUsername, editEmail, editPassword;
    Button saveButton, deleteButton;
    String usernameUser, emailUser, passwordUser;
    DatabaseReference reference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar3);
        //Img missing
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        reference = FirebaseDatabase.getInstance().getReference("usuarios");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //img is missing
                if (isUsernameChanged() || isPasswordChanged() || isEmailChanged()) {
                    Toast.makeText(SettingsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "No changes found", Toast.LENGTH_SHORT).show();
                }

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDeleteAccount();
                    }
                });
            }

            private boolean isEmailChanged() {
                if (!emailUser.equals(editEmail.getText().toString())){
                    reference.child(usernameUser).child("email").setValue(editEmail.getText().toString());
                    emailUser = editEmail.getText().toString();
                    return true;
                } else {
                    return false;
                }
            }

            private boolean isPasswordChanged() {
                if (!passwordUser.equals(editPassword.getText().toString())){
                    reference.child(usernameUser).child("password").setValue(editPassword.getText().toString());
                    passwordUser = editPassword.getText().toString();
                    return true;
                } else {
                    return false;
                }
            }

            private boolean isUsernameChanged() {
                if (!usernameUser.equals(editUsername.getText().toString())) {
                    reference.child(usernameUser).child("nombre").setValue(editUsername.getText().toString());
                    usernameUser = editUsername.getText().toString();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void showData() {
        Intent intent = getIntent();
        usernameUser = intent.getStringExtra("nombre");
        emailUser = intent.getStringExtra("email");
        passwordUser = intent.getStringExtra("password");
        editUsername.setText(usernameUser);
        editEmail.setText(emailUser);
        editPassword.setText(passwordUser);
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
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
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
