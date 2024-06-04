package com.example.entriviados10;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    TextView textViewTerms;

    String[] errorMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        repeatPassword = findViewById(R.id.repassword);
        checkBox = findViewById(R.id.checkBox);
        singUp = findViewById(R.id.register);
        textViewTerms = findViewById(R.id.textViewTerms);
        errorMessage = getResources().getStringArray(R.array.register_errors);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String termsText = getString(R.string.terms_and_conditions);
        SpannableString spannableString = new SpannableString(termsText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showTermsAndConditions();
            }
        };

        int start = 0;
        int end = start + getString(R.string.terms_and_conditions).length();
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textViewTerms.setText(spannableString);
        textViewTerms.setMovementMethod(LinkMovementMethod.getInstance());
        singUp.setEnabled(false);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> singUp.setEnabled(isChecked));

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

        if (!isValidEmail(email)) {
            Toast.makeText(this, errorMessage[0], Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPassword(password)) {
            Toast.makeText(this, errorMessage[1], Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Email already exists, show error message
                            Toast.makeText(this, R.string.email_already_exists, Toast.LENGTH_SHORT).show();
                        } else {
                            //Email does not exist, check if username exists
                            checkUsernameExists(email, password);
                        }
                    } else {
                        //Error occurred while checking email existence
                        Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUsernameExists(String email, String password) {
        String username = userName.getText().toString();
        //Check if username exists in Firestore
        db.collection("usuarios")
                .whereEqualTo("nombre", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Username already exists, show error message
                            Toast.makeText(this, R.string.username_already_exists, Toast.LENGTH_SHORT).show();
                        } else {
                            // Username does not exist, proceed with registration
                            registerUser(email, password);
                        }
                    } else {
                        // Error occurred while checking username existence
                        Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful
                            Log.d(TAG, "createUserWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                            saveUser();
                        } else {
                            // Registration failed
                            Log.w(TAG, "createUserWithEmail: failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }
                    }
                });
    }

    private void saveUser() {
        String name = userName.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        String repetirContraseña = repeatPassword.getText().toString();
        if (!password.equals(repetirContraseña)) {
            Toast.makeText(this, errorMessage[2], Toast.LENGTH_SHORT).show();
            return;
        }
        CollectionReference usuariosRef = db.collection("usuarios");

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", name);
        usuario.put("email", email);
        usuario.put("score", 0);
        usuario.put("photoURL", "https://firebasestorage.googleapis.com/v0/b/entriviados-aa084.appspot.com/o/Image%2Fprofile.png?alt=media&token=38c0c024-ebb4-4019-832f-2329f56ac4fc");

        usuariosRef.add(usuario)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "User registered", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private void showTermsAndConditions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.terms_and_conditions);
        builder.setMessage(getString(R.string.terms_and_conditions_text));
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }
}

