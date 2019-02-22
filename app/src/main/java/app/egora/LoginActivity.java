package app.egora;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;
    private TextView linkRegisterNow;
    private FirebaseAuth mAuth;

    // wird noch nicht verwendet ******************************
    private EditText editEmail;
    private EditText editPassword;
    private TextView linkForgotPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        //Checking Loginstatus
        if(mAuth.getCurrentUser() != null){
            //Sprung zur Datenbankactivity
        }
        getSupportActionBar().hide(); // hide the app title bar

        // Declaration
        buttonLogin = findViewById(R.id.buttonLogin);
        linkRegisterNow = findViewById(R.id.textRegister);
        editEmail = findViewById(R.id.logEmail);
        editPassword = findViewById(R.id.logPassword);
        linkForgotPassword = findViewById(R.id.textForgotPassword);
        progressDialog = new ProgressDialog(this);


        // Login with firebase auth
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("login", "login button clicked"); // nur zum Testen
                // hier Login mit Firebase Auth ausführen
                userLogin();
            }
        });

        // Switch activity to register
        linkRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("register", "switch to registerview"); // nur zum Testen
                Intent intent = new Intent(getBaseContext(), CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void userLogin(){
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please insert a valid email address", Toast.LENGTH_LONG).show();
            return;
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please insert a Password", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Log.i("loginVerify", mAuth.getCurrentUser().getEmail());
                    //Enter Datenbank
                    //finish activity
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


}
