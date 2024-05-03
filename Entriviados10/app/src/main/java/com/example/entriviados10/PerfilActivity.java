package com.example.entriviados10;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PerfilActivity extends AppCompatActivity {

    private TextView totalScoreView;
    private SharedPreferences sharedPreferences;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        ImageButton logoutButton = findViewById(R.id.imagelogout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //Display total score
        totalScoreView = findViewById(R.id.textView6);
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
                            if (score != null) {
                                totalScoreView.setText("Total score: " + score.intValue());
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error: unable to retrieve score", Toast.LENGTH_SHORT).show();
                    }
                });

        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.profile);

        logoutButton.setOnClickListener(v -> {
            // Cierra la sesión del usuario
            FirebaseAuth.getInstance().signOut();

            // Redirige al usuario a la pantalla de inicio de sesión
            Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Termina la actividad actual
        });
    }





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
        }
    }

}
