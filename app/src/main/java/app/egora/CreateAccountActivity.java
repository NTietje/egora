package app.egora;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.support.v7.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {

    private Button buttonRegister;

    // wird noch nicht verwendet ******************************
    private EditText editEmail;
    private EditText editPassword;
    private EditText editRepeatedPassword;
    private EditText editFirstName;
    private EditText editLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide(); // hide the app title bar

        // Declaration
        buttonRegister = findViewById(R.id.buttonRegister);
        editEmail = findViewById(R.id.regEmail);
        editPassword = findViewById(R.id.regPassword);
        editRepeatedPassword = findViewById(R.id.regPasswordRepeat);
        editFirstName = findViewById(R.id.regFirstName);
        editLastName = findViewById(R.id.regLastName);

        // Register new user in firebase
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("register", "register button clicked"); // nur zum Testen
                // hier User bei Firebase anlegen
            }
        });
    }
}
