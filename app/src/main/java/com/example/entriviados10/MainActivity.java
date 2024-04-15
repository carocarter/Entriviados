package com.example.entriviados10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button registerButton, signInButton;
    EditText email, password;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerButton = findViewById(R.id.button2);
        signInButton = findViewById(R.id.button);

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        textView = findViewById(R.id.textViewForgot);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email2 = email.getText().toString();
                String password2 = password.getText().toString();
                signIn(email2, password2);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void signIn(String email, String password) {

        if ( email.isEmpty() ||  password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email or password is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    try {
                        if (task.isSuccessful()) {
                            // Inicio de sesión exitoso, redirigir a la actividad de nivel seleccionado
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this, SelectLevelActivity.class);
                            startActivity(intent);
                        } else {
                            // Si falla el inicio de sesión, mostrar un mensaje de error
                            Toast.makeText(MainActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        // Manejar cualquier excepción que pueda ocurrir durante el inicio de sesión
                            Toast.makeText(MainActivity.this, "An error occurred: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                    }
                });
    }
}