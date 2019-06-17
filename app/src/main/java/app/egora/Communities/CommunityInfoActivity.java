package app.egora.Communities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app.egora.R;

public class CommunityInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Community Info");
        }

        TextView communityNameText = findViewById(R.id.communityName_Info);
        TextView descText = findViewById(R.id.communityDesc_Info);
        EditText keyEdit = findViewById(R.id.editKey_Info);
        Button joinButton = findViewById(R.id.buttonJoin);

        Intent intent = getIntent();
        communityNameText.setText(intent.getStringExtra("name"));
        Boolean mode = intent.getExtras().getBoolean("mode");
        String desc = intent.getStringExtra("desc");
        String key;

        if (desc != null) {
            if (!desc.isEmpty()) {
                descText.setText(desc);
            }
        }

        else {
            descText.setVisibility(View.GONE);
        }

        if (mode) {
            key = intent.getStringExtra("key");
        }
        else {
            keyEdit.setVisibility(View.GONE);
        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("join", "joined community"); // nur zum Testen
                //Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                //startActivity(intent);
            }
        });

    }

}
