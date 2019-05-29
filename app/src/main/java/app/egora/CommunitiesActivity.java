package app.egora;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import objects.CommunitiesListViewAdapter;
import objects.Community;


public class CommunitiesActivity extends AppCompatActivity {

    private ListView listView;
    private CommunitiesListViewAdapter adapter;
    private ArrayList<Community> communities;
    String leer;
    String postcodeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_overview);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Communities");
        }

        communities = new ArrayList<>();
        listView = findViewById(R.id.communitiesListView);

        postcodeString = "22089";
        Community a = new Community("Eilbek-Viertel", leer, "0110", leer);
        Community b = new Community("Otto-Srtraße 4", "Wir sind eine nette Gemeinde!", "0110", leer);
        Community c = new Community("Richardstr. 86 22089 Hamburg", leer, leer, leer);
        Community d = new Community("Grünes Haus", "Hallo ihr Lieben :)", leer, leer);
        communities.add(a);
        communities.add(b);
        communities.add(c);
        communities.add(d);
        Toast.makeText(CommunitiesActivity.this, communities.get(2).getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
