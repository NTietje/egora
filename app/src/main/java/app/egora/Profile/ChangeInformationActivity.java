package app.egora.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import app.egora.Communities.CommunitiesActivity;
import app.egora.Communities.NewCommunityActivity;
import app.egora.Login.LoginActivity;
import app.egora.Model.UserInformation;
import app.egora.R;

public class ChangeInformationActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private UserInformation currentUser;

    private ProgressDialog progressDialog;
    private Button buttonCommitChanges;
    private EditText editPassword;
    private EditText editRepeatedPassword;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editStreetName;
    private EditText editHouseNumber;
    private EditText editCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);
        progressDialog = new ProgressDialog(this);

        //Firebasekomponenten laden
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Login-Prüfung
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        user = mAuth.getCurrentUser();

        //Einbinden der ViewElemente
        buttonCommitChanges = findViewById(R.id.buttonChangeCommit);
        editPassword = findViewById(R.id.changePasswordRepeat);
        editRepeatedPassword = findViewById(R.id.changePassword);
        editFirstName = findViewById(R.id.changeFirstName);
        editLastName = findViewById(R.id.changeLastName);
        editStreetName = findViewById(R.id.changeStreetName);
        editHouseNumber = findViewById(R.id.changeHouseNumber);
        editCityName = findViewById(R.id.changeCityName);

        buttonCommitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitChanges();
            }
        });

    }

    private void commitChanges(){
        progressDialog.setMessage("Updating information...");
        progressDialog.show();
        final String password = editPassword.getText().toString().trim();
        final String firstName = editFirstName.getText().toString().trim();
        final String lastName = editLastName.getText().toString().trim();
        final String houseNumber = editHouseNumber.getText().toString().trim();
        final String streetName = editStreetName.getText().toString().trim();
        final String cityName = editCityName.getText().toString().trim();

        if(!password.equals(editRepeatedPassword.getText().toString().trim())){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Passwords must match!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        //Validating Firstname
        if(!TextUtils.isEmpty(firstName)&& !Pattern.matches("[a-zA-Z]+",firstName)){
            progressDialog.dismiss();

            FancyToast.makeText(ChangeInformationActivity.this,"Please insert a valid firstname!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        //Validating Lastname
        if(!TextUtils.isEmpty(lastName)&& !Pattern.matches("[a-zA-Z]+",lastName)){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Please insert a valid lastname!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }
        //Validating Housenumber
        if(!TextUtils.isEmpty(houseNumber)&& !houseNumber.matches(".*\\d+.*")){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Please insert a valid housenumber!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }

        userRef = db.collection("users").document(mAuth.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        currentUser = documentSnapshot.toObject(UserInformation.class);
                        //Updating Data mit Passwort
                        if(!isEmpty(editPassword)){
                            user.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (!isEmpty(editFirstName) || !isEmpty(editLastName) || !isEmpty(editStreetName) || !isEmpty(editHouseNumber) || !isEmpty(editCityName)){
                                        uploadChanges(firstName, lastName, streetName, houseNumber, cityName);
                                    }
                                    else {
                                        FancyToast.makeText(ChangeInformationActivity.this,"Updated your password!", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                        progressDialog.dismiss();

                                        Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                        //Updating Data ohne Passwort
                        else if (isEmpty(editPassword) && (!isEmpty(editFirstName) || !isEmpty(editLastName) || !isEmpty(editStreetName) || !isEmpty(editHouseNumber) || !isEmpty(editCityName) )){
                            uploadChanges(firstName, lastName, streetName, houseNumber, cityName);
                        }

                        //Keine Angaben getätigt
                        else {
                            FancyToast.makeText(ChangeInformationActivity.this,"You did not insert any information!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                            progressDialog.dismiss();
                        }

                    }
                });
            }
        });
    }


    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    //Uploading Firestore Data
    private void uploadChanges(String firstName, String lastName, String streetName, String houseNumber, String cityName){
        Map<String, Object> data = new HashMap<>();

        //Prüfen der einzelnen Felder
        if(!firstName.isEmpty()){
            data.put("firstName", firstName);
        }
        if(!lastName.equals("")){
            data.put("lastName", lastName);
        }

        if(!streetName.equals("")){
            data.put("streetName", streetName);
        }

        if(!houseNumber.equals("")){
            data.put("houseNumber", houseNumber);
        }

        if(!cityName.equals("")){
            data.put("cityName", cityName);
        }

        //Starten des Uploads (Update der Werte)
        userRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                FancyToast.makeText(ChangeInformationActivity.this,"Informationen wurden geändert!", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                FancyToast.makeText(ChangeInformationActivity.this,"Fehler: " + e, FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            }
        });

    }


}
