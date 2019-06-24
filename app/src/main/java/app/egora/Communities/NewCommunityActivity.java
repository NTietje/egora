package app.egora.Communities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;


import app.egora.Login.CreateAccountActivity;
import app.egora.R;
import app.egora.Model.Community;

public class NewCommunityActivity extends AppCompatActivity {

    private static class UserPost{
        String userID;
        private UserPost(String userID) {
            this.userID = userID;
        }
    }

    // Declaration Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_community);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Community");
        }

        //Initialisation Firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        final CheckBox keyCheckBox = findViewById(R.id.keyCheckBox);
        final EditText keyEdit = findViewById(R.id.keyEdit);
        Button createButton = findViewById(R.id.buttonCreateCommunity);

        keyEdit.setVisibility(View.GONE);

        keyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (keyCheckBox.isChecked()){
                keyEdit.setVisibility(View.VISIBLE);
            }
            else {
                keyEdit.setVisibility(View.GONE);
            }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ((EditText)findViewById(R.id.communityNameEdit)).getText().toString().trim();
                final String desc = ((EditText)findViewById(R.id.descEdit)).getText().toString().trim();
                final String key = ((EditText)findViewById(R.id.keyEdit)).getText().toString().trim();

                if (name.isEmpty()) {
                    FancyToast.makeText(NewCommunityActivity.this,"Give your community a name!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }
                else if (keyCheckBox.isChecked() && key.length() < 4) {
                    FancyToast.makeText(NewCommunityActivity.this,"Your key must be at least 4 characters long!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }
                else {
                    //Create community if name doesn't already exist
                    DocumentReference communityRef = db.collection("communities").document(name);
                    communityRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                //check if the name exists
                                if (document.exists()) {
                                    FancyToast.makeText(NewCommunityActivity.this,"This community name already exists!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                }
                                else {
                                    //create new community
                                    final Community community = new Community(name, desc, key, keyCheckBox.isChecked());
                                    mAuth.signInWithEmailAndPassword("n@gmail.com", "nana2014").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            community.addUser(mAuth.getCurrentUser().getUid());

                                            //set new community data in firestore
                                            db.collection("communities").document(community.getName())
                                                .set(community)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FancyToast.makeText(NewCommunityActivity.this,"Created community", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        FancyToast.makeText(NewCommunityActivity.this,"Can't create community", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                                    }
                                                });

                                            //set communityname in usercollection
                                            db.collection("users").document(mAuth.getCurrentUser().getUid()).update("community", community.getName());

                                        }
                                    });
                                }
                            }
                            else {
                                Log.d("failed", "get failed");
                            }
                        }
                    });


                }
            }
        });

    }

}




