package app.egora;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import app.egora.Model.UserInformation;

public class CreateAccountActivity extends AppCompatActivity {

    // Declaration Authentification Components
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;

    // Declaration UI Components
    private Button buttonRegister;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editRepeatedPassword;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editPhoneNumber;
    private EditText editStreetName;
    private EditText editHouseNumber;
    private EditText editCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide();

        // Initialisation
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();

        /* Authentification Listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(CreateAccountActivity.this, "Succesfully logged in", Toast.LENGTH_LONG).show();
                 } else {
                    // User is signed out
                    Toast.makeText(CreateAccountActivity.this, "Succesfully logged out", Toast.LENGTH_LONG).show();
                    }
                        // ...
                }
            };
        */


        // Initialisation UI Components
        buttonRegister = findViewById(R.id.buttonRegister);
        editEmail = findViewById(R.id.regEmail);
        editPassword = findViewById(R.id.regPassword);
        editRepeatedPassword = findViewById(R.id.regPasswordRepeat);
        editFirstName = findViewById(R.id.regFirstName);
        editLastName = findViewById(R.id.regLastName);
        editStreetName = findViewById(R.id.regStreetName);
        editHouseNumber = findViewById(R.id.regHouseNumber);
        editCityName = findViewById(R.id.regCityName);


        //editPhoneNumber = findViewById(R.id.regPhoneNumber);
        progressDialog = new ProgressDialog(this);

        // Register new user in firebase
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    //Register-User-method
    private void registerUser(){
        progressDialog.setMessage("Register user...");
        progressDialog.show();
        final String address;
        final String password = editPassword.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String firstName = editFirstName.getText().toString().trim();
        final String lastName = editLastName.getText().toString().trim();
        final String displayName = "" + firstName + " " + lastName;
        final String houseNumber = editHouseNumber.getText().toString().trim();
        final String streetName = editStreetName.getText().toString().trim();
        final String cityName = editCityName.getText().toString().trim();
        final String imageURL = "default";

        //final String phoneNumber = editPhoneNumber.getText().toString().trim();

            //Validating matching passwords
            if(!password.equals(editRepeatedPassword.getText().toString().trim())){
                progressDialog.dismiss();
                Toast.makeText(this, "Passwords must match", Toast.LENGTH_LONG).show();
                return;
            }
            //Validating Firstname
            if(TextUtils.isEmpty(firstName)&& !Pattern.matches("[a-zA-Z]+",firstName)){
                progressDialog.dismiss();
                Toast.makeText(this, "Please insert a valid Firstname", Toast.LENGTH_LONG).show();
                return;
            }
            //Validating Lastname
            if(TextUtils.isEmpty(lastName)&& !Pattern.matches("[a-zA-Z]+",lastName)){
                progressDialog.dismiss();
                Toast.makeText(this, "Please insert a valid Lastname", Toast.LENGTH_LONG).show();
                return;
            }
            //Validating Housenumber
            if(!houseNumber.matches(".*\\d+.*")){
                progressDialog.dismiss();
                Toast.makeText(this, "Please insert a valid Housenumber", Toast.LENGTH_LONG).show();
                return;
            }

            // Firebase registration
            mAuth.createUserWithEmailAndPassword(email, password)

                    .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        mAuth.signInWithEmailAndPassword(email, password);
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userID = user.getUid();
                        Log.d("UserID: " , userID);

                        //Writing into Database
                        UserInformation userInformation = new UserInformation(cityName, email, firstName, houseNumber, lastName, streetName, userID, imageURL);
                        Log.d("cityName: ", cityName);

                        //Failurelistener
                        myRef.child("users").child(user.getUid()).setValue(userInformation).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Fehler: ", "" + e);
                            }
                        });

                        //Writing into Authentication-Database
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();
                        user.updateProfile(profileUpdates);
                        progressDialog.dismiss();
                        Toast.makeText(CreateAccountActivity.this, "Registration successful",
                                Toast.LENGTH_LONG).show();

                        //Changing Activity
                        Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    //Show Exception-Message
                    else if(!task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(CreateAccountActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        //}
    }

    //Adding AuthListener on Start
    /*
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    //Removing AuthListener
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    */
}
