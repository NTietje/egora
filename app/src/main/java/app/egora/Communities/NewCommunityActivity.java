package app.egora.Communities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;


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
                String name = ((EditText)findViewById(R.id.communityNameEdit)).getText().toString().trim();
                String desc = ((EditText)findViewById(R.id.descEdit)).getText().toString().trim();
                String key = ((EditText)findViewById(R.id.keyEdit)).getText().toString().trim();

                if (name.isEmpty()) {
                    FancyToast.makeText(NewCommunityActivity.this,"Give your community a name!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }
                else if (keyCheckBox.isChecked() && key.length() < 4) {
                    FancyToast.makeText(NewCommunityActivity.this,"Your key must be at least 4 characters long!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }
                else {
                    final Community community = new Community(name, desc, key, keyCheckBox.isChecked());
                    myRef.child("communities").child(community.getName()).setValue(community).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FancyToast.makeText(NewCommunityActivity.this,"Created community", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                            String user = mAuth.getCurrentUser().getUid();
                            myRef.child("communities").child(community.getName()).child("users").child(user).setValue(new UserPost(user)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FancyToast.makeText(NewCommunityActivity.this,"Added user", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                }
                            });
                        }
                    });
                }
            }
        });

    }

}




