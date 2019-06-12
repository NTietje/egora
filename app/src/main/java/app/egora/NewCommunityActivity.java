package app.egora;

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

import java.util.Date;

import objects.Community;

public class NewCommunityActivity extends AppCompatActivity {

    // Declaration Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    //Declaration
    private CheckBox keyCheckBox;
    private EditText keyEdit;
    private String name;
    private String desc;
    private String key;
    private Date lastActivity;

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

        Button createButton = findViewById(R.id.buttonCreateCommunity);
        keyCheckBox = findViewById(R.id.keyCheckBox);
        keyEdit = findViewById(R.id.keyEdit);

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
                EditText nameEdit = findViewById(R.id.communityNameEdit);
                EditText descEdit = findViewById(R.id.descEdit);
                EditText keyEdit = findViewById(R.id.keyEdit);
                name = nameEdit.getText().toString().trim();
                desc = descEdit.getText().toString().trim();
                key = keyEdit.getText().toString().trim();

                if (name == null || name.isEmpty()) {
                    FancyToast.makeText(NewCommunityActivity.this,"Give your community a name!",
                            FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }
                else if (keyCheckBox.isChecked() && key.length() < 4) {
                    FancyToast.makeText(NewCommunityActivity.this,"Your key must be at least 4 characters long!",
                            FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }
                else {
                    Community community = new Community(name, desc, key, keyCheckBox.isChecked());
                    myRef.child("communities").child(community.getName()).setValue(community).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FancyToast.makeText(NewCommunityActivity.this,"Created community",
                                    FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                        }
                    });

                }

            }
        });

    }

}
