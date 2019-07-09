package app.egora.Communities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import app.egora.ItemManagement.HomeActivity;
import app.egora.Model.Item;
import app.egora.R;

public class CommunityInfoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference itemRef;
    private DocumentReference userRef;
    private DocumentReference communityRef;
    private String name;
    private Boolean mode;
    private String key;
    private EditText keyEdit;
    private List<Item> itemList;
    private WriteBatch batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Community Info");
        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        itemList = new ArrayList<>();

        TextView communityNameText = findViewById(R.id.communityName_Info);
        TextView descText = findViewById(R.id.communityDesc_Info);
        keyEdit = findViewById(R.id.editKey_Info);
        Button joinButton = findViewById(R.id.buttonJoin);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        communityNameText.setText(name);
        mode = intent.getExtras().getBoolean("mode");
        String desc = intent.getStringExtra("desc");
        key = intent.getStringExtra("key");

        if (desc != null) {
            if (!desc.isEmpty()) {
                descText.setText(desc);
            }
        }
        else {
            descText.setVisibility(View.GONE);
        }

        if (!mode) {
            keyEdit.setVisibility(View.GONE);
        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode) {
                    if(keyEdit.getText().toString().toLowerCase().equals(key.toLowerCase())) {
                        joinCommunity();
                    }
                    else {
                        FancyToast.makeText(CommunityInfoActivity.this,"The key isn't correct", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    }
                }
                else {
                    joinCommunity();
                }

            }
        });

    }

    private void joinCommunity() {

        //Setting up Paths
        userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        communityRef = db.collection("communities").document(name);
        batch = db.batch();

        //Updating fields in Firebase
        batch.update(userRef, "communityName" , name);
        batch.update(communityRef, "userIDs" , FieldValue.arrayUnion(mAuth.getUid()));

        //Changing Itemcommunites in Batch
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
                            //Adding items from Arraylist to the batch object
                            for(Item item: itemList){
                                itemRef = db.collection("items").document(item.getItemId());
                                batch.update(itemRef, "communityName" , name );
                            }

                        }
                        //Commiting the whole batch
                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Changing to Home View
                                FancyToast.makeText(CommunityInfoActivity.this,"You joined " + name, FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                                CommunitiesActivity.getInstance().finish();
                            }
                        });
                    }
                });
    }
}
