package com.example.entriviados10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button registerButton, signInButton;
    MediaPlayer mediaPlayer;
    EditText email, password;
    TextView textView;
    private boolean keepSplashAlive = true;
    private boolean isMusicPlaying = false;
    private static final String MUSIC_ON_OFF_KEY = "music_on_off";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> keepSplashAlive);
        new Handler(Looper.getMainLooper()).postDelayed(() -> keepSplashAlive = false, 1000);

        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.radar142575);
        mediaPlayer.setLooping(true);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isMusicPlaying = sharedPreferences.getBoolean(MUSIC_ON_OFF_KEY, false);

        if (isMusicPlaying) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }

        registerButton = findViewById(R.id.button2);
        signInButton = findViewById(R.id.button);

        email = findViewById(R.id.editTextEmailAddress);
        password = findViewById(R.id.editTextPassword);
        textView = findViewById(R.id.textView3);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(view -> {
            String email2 = email.getText().toString();
            String password2 = password.getText().toString();
            signIn(email2, password2);
        });

        textView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
            startActivity(intent);
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
                            Intent intent = new Intent(MainActivity.this, SelectLevelActivity.class);
                            startActivity(intent);
                            finish();
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
    @Override
    protected void onPause() {
        super.onPause();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMusicPlaying && mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
