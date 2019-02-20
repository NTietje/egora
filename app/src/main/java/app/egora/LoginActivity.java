package app.egora;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;
    private TextView linkRegisterNow;

    // wird noch nicht verwendet ******************************
    private EditText editEmail;
    private EditText editPassword;
    private TextView linkForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide(); // hide the app title bar

        // Declaration
        buttonLogin = findViewById(R.id.buttonLogin);
        linkRegisterNow = findViewById(R.id.textRegister);
        editEmail = findViewById(R.id.logEmail);
        editPassword = findViewById(R.id.logPassword);
        linkForgotPassword = findViewById(R.id.textForgotPassword);

        // Login with firebase auth
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("login", "login button clicked"); // nur zum Testen
                // hier Login mit Firebase Auth ausführen
            }
        });

        // Switch activity to register
        linkRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("register", "switch to registerview"); // nur zum Testen
                Intent intent = new Intent(getBaseContext(), CreateAccountActivity.class);
                startActivity(intent);
                //finish();
            }
        });

    }
}
