package app.egora;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

import objects.CommunitiesListViewAdapter;
import objects.Community;


public class CommunitiesActivity extends AppCompatActivity {

    //Declaration Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;

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

        FloatingActionButton addButton = findViewById(R.id.addCommunityButton);

        //Initialisation Firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        communities = new ArrayList<>();
        listView = findViewById(R.id.communitiesListView);

        Community a = new Community("Eilbek-Viertel", "Beschreibung bla", "0110", true);
        Community b = new Community("Otto-Srtraße 4", "Wir sind eine nette Gemeinde!", "0110", true);
        Community c = new Community("Richardstr.86 Hamburg", leer, leer, false);
        Community d = new Community("Grünes Haus", "Hallo ihr Lieben :)", leer, false);
        Community[] communityArray = {a,b,c,d,b,c,d,a,c,a};
        communities.addAll(Arrays.asList(communityArray));

        //pass communities to CommunitiesListViewAdapter
        adapter = new CommunitiesListViewAdapter(this, communities);

        //bind the adapter to the listview
        listView.setAdapter(adapter);

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

}
