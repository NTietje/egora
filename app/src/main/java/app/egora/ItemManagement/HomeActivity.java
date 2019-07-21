package app.egora.ItemManagement;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import app.egora.Login.LoginActivity;
import app.egora.Messenger.MessengerActivity;
import app.egora.Model.Item;
import app.egora.Model.UserInformation;
import app.egora.Profile.ProfileActivity;
import app.egora.R;
import app.egora.Utils.FilterableItemAdapter;
import app.egora.Utils.ItemAdapter;

public class HomeActivity extends AppCompatActivity {




    ImageView itemImage;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    //private ItemAdapter adapter;
    private FilterableItemAdapter filterAdapter;


    private UserInformation currentUser;
    private String currentCommunity;
    private TextView noCommunityTextView;
    private RecyclerView recyclerView;
    private String category;
    private String searchText;
    private Spinner spinnerCategory;
    private SearchView searchView;
    private String[] categories;


    //Konstruktor (wird benötigt)
    public HomeActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Prüfung des Loginstatus
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        category = "Alle";
        searchText = "";

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        searchView = findViewById(R.id.item_search2);
        spinnerCategory = findViewById(R.id.item_category2);
        categories =  new String[]{"Alle", "Basteln", "Elektronik & Zubehör", "Garten & Grill", "Handwerken", "Küche & Haushalt", "Sport & Freizeit", "Sonstiges"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(spinnerAdapter);

        //RecyclerView
        recyclerView = findViewById(R.id.items_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        db = FirebaseFirestore.getInstance();
        setupFirebaseModules();

        noCommunityTextView = findViewById(R.id.textview_no_group);
        noCommunityTextView.setVisibility(View.INVISIBLE);

        //Button zum Hinzufügen von Items
        FloatingActionButton addButton = findViewById(R.id.add_object_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddingItem.class);
                intent.putExtra("USER_COMMUNITY", currentCommunity);
                startActivity(intent);
            }
        });

        //Unteres Navigationsmenü
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.getMenu().getItem(1).setChecked(true);

        //Textview bei keiner Gruppe
    }


    //Navigationsmenü mit Listener und Funktionen befüllen
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = new Intent();

            switch (item.getItemId()) {
                case R.id.action_profile:
                    intent = new Intent(getBaseContext(), ProfileActivity.class);
                    break;
                case R.id.action_home:
                    intent = new Intent(getBaseContext(), HomeActivity.class);
                    break;
                case R.id.action_messenger:
                    intent = new Intent(getBaseContext(), MessengerActivity.class);
                    break;
            }

            startActivity(intent);
            return true;
        }

    };

    //Methode um Firebase und die Listener vorzubereiten
    private void setupFirebaseModules() {
        Log.d("Firebase: ", "setupFirebaseAuth: setting up firebase auth.");


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        currentUser = new UserInformation();

        userRef = db.collection("users").document(mAuth.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        currentUser = documentSnapshot.toObject(UserInformation.class);
                        String userId = mAuth.getUid().toString();

                        //Checking if Community exists
                        if (currentUser.getCommunityName() != null) {

                            //Updating View and adding RecyclerViewAdapter
                            currentCommunity = currentUser.getCommunityName();
                            noCommunityTextView.setVisibility(View.INVISIBLE);

                            //Get item data from firestore
                            Query query = db.collection("items").whereEqualTo("communityName", currentCommunity);
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    filterAdapter = new FilterableItemAdapter(task.getResult().toObjects(Item.class));
                                    recyclerView.setAdapter(filterAdapter);
                                    createSearchListener();
                                }
                            });

                        } else {
                            //Show No Community TextView
                            noCommunityTextView = findViewById(R.id.textview_no_group);
                            noCommunityTextView.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        });
    }

    private void createSearchListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                category = spinnerCategory.getSelectedItem().toString();
                filterAdapter.getFilter(category).filter(newText);
                return false;
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = spinnerCategory.getSelectedItem().toString();
                filterAdapter.getFilter(category).filter("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    /*@Override
    protected void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }*/


    /*@Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }*/
}

