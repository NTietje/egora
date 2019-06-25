package app.egora.Communities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import app.egora.ItemManagement.HomeActivity;
import app.egora.R;

public class CommunityInfoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String name;
    private Boolean mode;
    private String key;
    private EditText keyEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Community Info");
        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
        db.collection("users").document(mAuth.getCurrentUser().getUid()).update("communityName", name);
        db.collection("communities").document(name).update("userIDs", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));
        FancyToast.makeText(CommunityInfoActivity.this,"You joined " + name, FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(intent);
        finish();
        CommunitiesActivity.getInstance().finish();
    }
}
