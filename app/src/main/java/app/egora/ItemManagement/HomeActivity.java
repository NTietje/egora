package app.egora.ItemManagement;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;
import java.util.ArrayList;
import javax.annotation.Nullable;
import app.egora.Communities.CommunitiesActivity;
import app.egora.Messenger.MessengerActivity;
import app.egora.Model.Item;
import app.egora.Model.UserInformation;
import app.egora.Profile.ProfileActivity;
import app.egora.R;
import app.egora.Utils.FilterableItemAdapter;
import app.egora.Utils.FirestoreUtil;


public class HomeActivity extends AppCompatActivity {


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FilterableItemAdapter filterAdapter;

    private UserInformation currentUser;
    private String currentCommunity;
    private RecyclerView recyclerView;
    private String category;
    private String searchText;
    private Spinner spinnerCategory;
    private EditText editSearch;
    private boolean filterOpen;
    private RelativeLayout spinnerLayout;
    private ArrayList<String> categories;


    //Konstruktor (wird benötigt)
    public HomeActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Prüfung des Loginstatus
        mAuth = FirebaseAuth.getInstance();
        FirestoreUtil.addAuthListener(mAuth, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        loadCategories();
        editSearch = findViewById(R.id.item_search);
        spinnerCategory = findViewById(R.id.categorie_spinner);
        ImageButton filterButton = findViewById(R.id.item_filter_button);
        FloatingActionButton addButton = findViewById(R.id.add_object_button);
        spinnerLayout = findViewById(R.id.spinner_layout);

        spinnerLayout.setVisibility(View.INVISIBLE);

        //RecyclerView
        recyclerView = findViewById(R.id.items_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        setupFirebaseModules();

        //Buttonlistener zum Hinzufügen von Items
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

        //filterbutton listener to show category spinner
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filterOpen) {
                    spinnerLayout.setVisibility(View.INVISIBLE);
                    filterOpen = false;
                }
                else {
                    spinnerLayout.setVisibility(View.VISIBLE);
                    filterOpen = true;
                }

            }
        });
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
            finish();
            return true;
        }

    };

    //Methode um Firebase und die Listener vorzubereiten
    private void setupFirebaseModules() {

        currentUser = new UserInformation();
        userRef = db.collection("users").document(mAuth.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                userRef.addSnapshotListener(HomeActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        currentUser = documentSnapshot.toObject(UserInformation.class);

                        //Checking if Community exists
                        if(currentUser.getCommunityName().equals("changing")){
                            Intent intent = new Intent(getBaseContext(), CommunitiesActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            //Updating View and adding RecyclerViewAdapter
                            currentCommunity = currentUser.getCommunityName();

                            //Get item data from firestore
                            Query query = db.collection("items").whereEqualTo("communityName", currentCommunity);
                            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    filterAdapter = new FilterableItemAdapter(queryDocumentSnapshots.toObjects(Item.class), categories);
                                    recyclerView.setAdapter(filterAdapter);
                                    createSearchListener();
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    FancyToast.makeText(HomeActivity.this,"Fehler: " + e.toString(), FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                }
                            });

                        }
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FancyToast.makeText(HomeActivity.this,"Fehler: " + e.toString(), FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();

            }
        });
    }

    //search method for editText and spinner with categories
    private void createSearchListener() {

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchText = editSearch.getText().toString();
                category = spinnerCategory.getSelectedItem().toString();
                filterAdapter.getFilter(category).filter(searchText);
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = spinnerCategory.getSelectedItem().toString();
                filterAdapter.getFilter(category).filter("");
                spinnerLayout.setVisibility(View.INVISIBLE);
                filterOpen = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    //get filter categories from database
    private void loadCategories() {
        db.collection("basedata").document("categories").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        categories = (ArrayList<String>) documentSnapshot.get("categories");
                        setSpinnerCategories(categories);
                    }
                });
    }

    //set categories in spinner
    private void setSpinnerCategories(ArrayList<String> categories) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, categories);
        spinnerCategory.setAdapter(spinnerAdapter);
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

