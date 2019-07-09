package app.egora.Communities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.shashank.sony.fancytoastlib.FancyToast;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.egora.ItemManagement.HomeActivity;
import app.egora.Model.Item;
import app.egora.R;
import app.egora.Model.Community;

public class NewCommunityActivity extends AppCompatActivity {

    // Declaration Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DocumentReference itemRef;
    private DocumentReference userRef;
    private DocumentReference communityRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<Item> itemList;
    private WriteBatch batch;

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

        itemList = new ArrayList<>();

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
                                    community.setLastActivity(new Date());
                                    community.addUser(mAuth.getCurrentUser().getUid());

                                    //set new community data in firestore
                                    final DocumentReference communityRef = db.collection("communities").document(community.getName());
                                    final DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

                                    batch = db.batch();
                                    batch.set(communityRef, community);

                                    db.collection("items").whereEqualTo("ownerId" , mAuth.getUid()).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    //Checking if the query is empty
                                                    if(!queryDocumentSnapshots.isEmpty()){
                                                        //Adding items to Arraylist
                                                        for (DocumentSnapshot snapshot:queryDocumentSnapshots){

                                                            itemList.add(snapshot.toObject(Item.class));
                                                        }
                                                    }

                                                    //Adding items from Arraylist to the batch object
                                                    for(Item item: itemList){
                                                        itemRef = db.collection("items").document(item.getItemId());
                                                        batch.update(itemRef, "communityName" , community.getName() );
                                                    }

                                                    ////set communityname in usercollection and delete userId from community
                                                    batch.update(userRef, "communityName" , community.getName());

                                                    //Commiting the whole batch
                                                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            //finish this activity and CommunitiesActivity
                                                            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                            CommunitiesActivity.getInstance().finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            FancyToast.makeText(NewCommunityActivity.this,"Can't create community", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                                        }
                                                    });
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




