package app.egora.Communities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import app.egora.Utils.CommunitiesListViewAdapter;
import app.egora.R;
import app.egora.Model.Community;


public class CommunitiesActivity extends AppCompatActivity {

    static CommunitiesActivity communitiesActivity;

    //Declaration Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseFirestore db;

    //Declaration
    private ListView listView;
    private CommunitiesListViewAdapter adapter;
    private ArrayList<Community> communities;
    String leer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_overview);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Communities");
        }
        communitiesActivity = this;
        FloatingActionButton addButton = findViewById(R.id.addCommunityButton);

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
                                Log.d("Communitie: ", name + ", " + desc + ", " + key + ", " + privacyMode.toString());
                                communities.add(new Community(name, desc, key, privacyMode));
                            }
                            //pass communities to CommunitiesListViewAdapter
                            adapter = new CommunitiesListViewAdapter(CommunitiesActivity.this, communities);

                            //bind the adapter to the listview
                            listView.setAdapter(adapter);

                        } else {
                            Log.d("msg", "Error getting documents: ", task.getException());
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

    }

    //Search function filters communities by name
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
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
    }

    public static CommunitiesActivity getInstance() {
        return communitiesActivity;
    }

}
