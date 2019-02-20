package app.egora;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    // Declaration
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

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

        // Register new user in firebase
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editPassword.getText().toString().trim();
                if (password.equals(editRepeatedPassword.getText().toString().trim())) {
                    // Firebase registration
                    mAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), password);
                    //hier fehlt noch eine Abfrage an Firebase ob die Registr. erfolreich war
                    Toast.makeText(CreateAccountActivity.this, "You have successfully registered!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(CreateAccountActivity.this, "Passwords must match!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.i("user", "current user is: " + currentUser.getUid());
        }
        else {
            Log.i("user", "user is null");
        }
    }
}
