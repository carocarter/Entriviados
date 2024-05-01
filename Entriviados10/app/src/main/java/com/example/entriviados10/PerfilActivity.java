package com.example.entriviados10;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class PerfilActivity extends AppCompatActivity {

    private TextView totalScoreView;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        toolbar = findViewById(R.id.toolbar3);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Display total score
        totalScoreView = findViewById(R.id.textView6);
        sharedPreferences = getSharedPreferences("totalScore", MODE_PRIVATE);
        int totalScore = sharedPreferences.getInt("totalScore", 0);
        totalScoreView.setText("Total score: " + totalScore);

        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.profile);
    }

    public void confirmDeleteAccount(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar cuenta");
        builder.setMessage("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarCuenta();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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
                                Toast.makeText(PerfilActivity.this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                                // Redirige al usuario a la pantalla de inicio de sesión
                                Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Termina la actividad actual
                            } else {
                                // Ocurrió un error al intentar eliminar la cuenta
                                Toast.makeText(PerfilActivity.this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
