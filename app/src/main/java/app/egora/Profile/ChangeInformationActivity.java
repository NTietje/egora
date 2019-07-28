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
import app.egora.Login.CreateAccountActivity;
import app.egora.Login.LoginActivity;
import app.egora.Model.UserInformation;
import app.egora.R;

public class ChangeInformationActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private UserInformation currentUser;

    private Boolean changingStatus;

    private ProgressDialog progressDialog;
    private Button buttonCommitChanges;
    private EditText editPassword;
    private EditText editRepeatedPassword;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editStreetName;
    private EditText editHouseNumber;
    private EditText editCityName;

    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);
        progressDialog = new ProgressDialog(this);
        changingStatus = false;

        counter = 0;

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
        editPassword = findViewById(R.id.changePassword);
        editRepeatedPassword = findViewById(R.id.changePasswordRepeat);
        editFirstName = findViewById(R.id.changeFirstName);
        editLastName = findViewById(R.id.changeLastName);
        editStreetName = findViewById(R.id.changeStreetName);
        editHouseNumber = findViewById(R.id.changeHouseNumber);
        editCityName = findViewById(R.id.changeCityName);

        buttonCommitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingStatus = true;
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
        final String repeatedPassword = editRepeatedPassword.getText().toString().trim();

        if(TextUtils.isEmpty(password) && TextUtils.isEmpty(repeatedPassword) && TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(houseNumber) && TextUtils.isEmpty(cityName) &&TextUtils.isEmpty(streetName) ){

            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Du musst zuerst mindestens ein Feld befüllen!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }

        if(!TextUtils.isEmpty(password) && TextUtils.isEmpty(repeatedPassword)){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Wiederhole dein Passwort!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }

        if(!TextUtils.isEmpty(password) && !password.equals(repeatedPassword)){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Passwörter müssen übereinstimmen!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }

        //Validating Firstname
        if(!TextUtils.isEmpty(firstName)&& !Pattern.matches("^[A-Za-z_äÄöÖüÜß]*$",firstName)){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Gebe einen gültigen Vornamen ein!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }

        //Validating Lastname
        if(!TextUtils.isEmpty(lastName)&& !Pattern.matches("[A-Za-z_äÄöÖüÜß]*$",lastName)){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Gebe einen gültigen Nachnamen ein!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }

        //Validating Housenumber
        if(!TextUtils.isEmpty(houseNumber)&& !houseNumber.matches(".*\\d+.*")){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Gebe eine gültige Hausnummer ein!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }

        if(!TextUtils.isEmpty(streetName)&& !Pattern.matches("^[A-Za-z_äÄöÖüÜß]+$",streetName)){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Gebe einen gültigen Straßennamen an!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }

        if(TextUtils.isEmpty(cityName)&& !Pattern.matches("^[A-Za-z_äÄöÖüÜß]*$",cityName)){
            progressDialog.dismiss();
            FancyToast.makeText(ChangeInformationActivity.this,"Gebe einen gültige Stadt an!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            return;
        }



        userRef = db.collection("users").document(mAuth.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(UserInformation.class);
                //Updating Data mit Passwort
                if(!TextUtils.isEmpty(password)){
                    user.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (!isEmpty(editFirstName) || !isEmpty(editLastName) || !isEmpty(editStreetName) || !isEmpty(editHouseNumber) || !isEmpty(editCityName)){
                                uploadChanges(firstName, lastName, streetName, houseNumber, cityName);
                            }

                            else {
                                FancyToast.makeText(ChangeInformationActivity.this,"Passwort wurde geändert!", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            FancyToast.makeText(ChangeInformationActivity.this,"Fehler: " + e, FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                            progressDialog.dismiss();
                        }
                    });
                }
                //Updating Data ohne Passwort
                else if (TextUtils.isEmpty(password) && (!TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName) || !TextUtils.isEmpty(streetName) || !TextUtils.isEmpty(houseNumber) || !TextUtils.isEmpty(cityName) )){
                    uploadChanges(firstName, lastName, streetName, houseNumber, cityName);

                }

                //Keine Angaben getätigt
                else {
                    FancyToast.makeText(ChangeInformationActivity.this,"Du hast keine Informationen eingefügt!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    progressDialog.dismiss();
                }
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
            if(!TextUtils.isEmpty(firstName)){
                data.put("firstName", firstName);
            }
            if(!TextUtils.isEmpty(lastName)){
                data.put("lastName", lastName);
            }

            if(!TextUtils.isEmpty(streetName)){
                data.put("streetName", streetName);
            }

            if(!TextUtils.isEmpty(houseNumber)){
                data.put("houseNumber", houseNumber);
            }

            if(!TextUtils.isEmpty(cityName)){
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
