package app.egora;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    // Declaration
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    // Declaration UI Components
    private Button buttonRegister;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editRepeatedPassword;
    private EditText editFirstName;
    private EditText editLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide();

        // Initialisation
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();

        // Initialisation UI Components
        buttonRegister = findViewById(R.id.buttonRegister);
        editEmail = findViewById(R.id.regEmail);
        editPassword = findViewById(R.id.regPassword);
        editRepeatedPassword = findViewById(R.id.regPasswordRepeat);
        editFirstName = findViewById(R.id.regFirstName);
        editLastName = findViewById(R.id.regLastName);
        progressDialog = new ProgressDialog(this);

        // Register new user in firebase
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        progressDialog.setMessage("Register user...");
        progressDialog.show();
        final String password = editPassword.getText().toString().trim();
        if (password.equals(editRepeatedPassword.getText().toString().trim())) {
            // Firebase registration
            mAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        progressDialog.dismiss();
                        Toast.makeText(CreateAccountActivity.this, "Registration successful",
                                Toast.LENGTH_LONG).show();

                    }
                    else if(!task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(CreateAccountActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(CreateAccountActivity.this, "Passwords must match!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
