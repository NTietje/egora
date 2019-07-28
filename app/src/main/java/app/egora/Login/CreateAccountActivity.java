package app.egora.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import app.egora.Communities.CommunitiesActivity;
import app.egora.Communities.NewCommunityActivity;
import app.egora.Profile.ChangeInformationActivity;
import app.egora.R;

public class CreateAccountActivity extends AppCompatActivity {

    // Declaration Authentification Components
    private FirebaseDatabase database;
    private FirebaseFirestore db;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    // Declaration UI Components
    private ProgressDialog progressDialog;
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
        db = FirebaseFirestore.getInstance();
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
        progressDialog.setMessage("Nutzer wird registriert...");
        progressDialog.show();
        final String address;
        final String password = editPassword.getText().toString().trim();
        final String repeatedPassword = editRepeatedPassword.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String firstName = editFirstName.getText().toString().trim();
        final String lastName = editLastName.getText().toString().trim();
        final String displayName = "" + firstName + " " + lastName;
        final String houseNumber = editHouseNumber.getText().toString().trim();
        final String streetName = editStreetName.getText().toString().trim();
        final String cityName = editCityName.getText().toString().trim();
        final String imageURL = "default";

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Gib eine gültige Email ein", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        if(password.isEmpty()){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Gib ein Passwort ein", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        if(password.length() < 6){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Dein Passwort muss mindestens 6 Zeichen lang sein", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        if(repeatedPassword.isEmpty()){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Wiederhole dein Passwort", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }

        if(!password.equals(repeatedPassword)){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Passwörter müssen übereinstimmen", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        //Validating Firstname
        if(TextUtils.isEmpty(firstName)|| !Pattern.matches("^[-A-Za-z_äÄöÖüÜß ]*$",firstName)){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Gib einen gültigen Vornamen ein. Erlaubte Zeichen: A bis z, ß, - und Leerzeichen", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        //Validating Lastname
        if(TextUtils.isEmpty(lastName)|| !Pattern.matches("^[-A-Za-z_äÄöÖüÜß. ]*$",lastName)){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Gib einen gültigen Nachnamen ein. Erlaubte Zeichen: A bis z, ß, -, . und Leerzeichen", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        if(TextUtils.isEmpty(streetName)|| !Pattern.matches("^[-A-Za-z_äÄöÖüÜß. ]*$",streetName)){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Gib einen gültigen Straßennamen ein. Erlaubte Zeichen: A bis z, ß, -, . und Leerzeichen", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        //Validating Housenumber
        if(TextUtils.isEmpty(houseNumber)||!houseNumber.matches(".*\\d+.*")){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Gib eine gültige Hausnummer ein. Erlaubte Zeichen: 0-9 gefolgt von einem Zeichen", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        if(TextUtils.isEmpty(cityName)|| !Pattern.matches("^[-A-Za-z_äÄöÖüÜß ]*$" ,cityName)){
            progressDialog.dismiss();
            FancyToast.makeText(CreateAccountActivity.this,"Gib eine gültige Stadt ein. Erlaubte Zeichen: A bis z, ß, - und Leerzeichen", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
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

                        final String userID = user.getUid();
                        Log.d("UserID: " , userID);

                        //Creating User-Document for Cloud Firestore
                        Map<String, Object> currentUser = new HashMap<>();
                        currentUser.put("firstName" , firstName);
                        currentUser.put("lastName", lastName);
                        currentUser.put("email" , email);
                        currentUser.put("streetName", streetName);
                        currentUser.put("houseNumber" , houseNumber);
                        currentUser.put("cityName", cityName);

                        db.collection("users").document(userID)
                                .set(currentUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        FancyToast.makeText(CreateAccountActivity.this,"Fehler: " + e, FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                    }
                                });

                        //Writing into Authentication-Database
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();
                        user.updateProfile(profileUpdates);
                        progressDialog.dismiss();

                        FancyToast.makeText(CreateAccountActivity.this,"Dein Account wurde erfolgreich angelegt", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();

                        //Changing Activity
                        Intent intent = new Intent(getBaseContext(), CommunitiesActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    //Show Exception-Message
                    else if(!task.isSuccessful()){
                        progressDialog.dismiss();
                        FancyToast.makeText(CreateAccountActivity.this,task.getException().getMessage(), FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    }
                }
            });
    }


}
