package com.example.entriviados10;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText userName, email, password, repeatPassword;
    CheckBox checkBox;
    Button singUp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail2);
        password = findViewById(R.id.editTextPassword2);
        repeatPassword = findViewById(R.id.editTextPasswordRepeat);
        checkBox = findViewById(R.id.checkBox);
        singUp = findViewById(R.id.button3);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Log.d("RegisterActivity", email.getText().toString() + password.getText().toString() );

                String email2 = email.getText().toString();
                String password2 = password.getText().toString();
                createAccount(email2, password2);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if (item.getItemId() == android.R.id.home){
           onBackPressed();
           return true;
       }

        return super.onOptionsItemSelected(item);
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, actualiza la interfaz de usuario con la información del usuario registrado
                            Log.d(TAG, "createUserWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                     //       updateUI(user);

                        } else {
                            // Si falla el registro, muestra un mensaje al usuario
                            Log.w(TAG, "createUserWithEmail: failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }
                    }
                });

        guardarUsuarios();
    }

//    private void updateUI(FirebaseUser user) {
//        if (user != null) {
//          //  Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void guardarUsuarios() {
        String name = userName.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        String repetirContraseña = repeatPassword.getText().toString();
        if (!password.equals(repetirContraseña)) {
            Toast.makeText(this, "La contraseña no coincide", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference usuariosRef = db.collection("usuarios");

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", name);
        usuario.put("email", email);

        usuariosRef.add(usuario)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

