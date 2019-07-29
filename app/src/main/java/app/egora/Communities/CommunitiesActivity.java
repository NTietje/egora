package app.egora.Communities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import app.egora.Utils.CommunitiesListViewAdapter;
import app.egora.R;
import app.egora.Model.Community;
import app.egora.Utils.FirestoreUtil;


public class CommunitiesActivity extends AppCompatActivity {

    static CommunitiesActivity communitiesActivity;

    //Declaration Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    //Declaration
    private ListView listView;
    private CommunitiesListViewAdapter adapter;
    private ArrayList<Community> communities;
    String leer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_overview);
        getSupportActionBar().hide();

        //Login-Prüfung
        mAuth = FirebaseAuth.getInstance();
        FirestoreUtil.addAuthListener(mAuth, this);

        communitiesActivity = this;
        FloatingActionButton addButton = findViewById(R.id.addCommunityButton);
        final EditText searchCommunity = findViewById(R.id.community_search);

        //Initialisation Firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        db = FirebaseFirestore.getInstance();

        communities = new ArrayList<>();
        listView = findViewById(R.id.communitiesListView);

        db.collection("communities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.get("name").toString();
                                String desc = document.get("description").toString();
                                String key = document.get("key").toString();
                                Boolean privacyMode = (Boolean) document.get("privacyMode");
                                communities.add(new Community(name, desc, key, privacyMode));
                            }
                            //pass communities to CommunitiesListViewAdapter
                            adapter = new CommunitiesListViewAdapter(CommunitiesActivity.this, communities);

                            //bind the adapter to the listview
                            listView.setAdapter(adapter);
                        }
                    }
                });

        //Switch to NewCommunityActivity
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NewCommunityActivity.class);
                startActivity(intent);
            }
        });

        searchCommunity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(searchCommunity.getText().toString())) {
                    adapter.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adapter.filter(searchCommunity.getText().toString());
                }
            }
        });

    }

    /*//Search function filters communities by name
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_communities, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.community_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adapter.filter(newText);
                }
                return false;
            }
        });

        return true;
    }*/

    public static CommunitiesActivity getInstance() {
        return communitiesActivity;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirestoreUtil.addAuthListener(mAuth, this);
    }

    @Override
    public void onStop() {
        FirestoreUtil.removeAuthListener();
        super.onStop();
    }

}
