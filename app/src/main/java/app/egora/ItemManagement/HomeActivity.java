package app.egora.ItemManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import app.egora.Login.LoginActivity;
import app.egora.Messenger.MessengerActivity;
import app.egora.Model.Item;
import app.egora.Model.UserInformation;
import app.egora.ProfileActivity;
import app.egora.R;
import app.egora.Utils.FirebaseMethods;

public class HomeActivity extends AppCompatActivity {

    ListView listView;
    FirebaseListAdapter adapter;
    ImageView itemImage;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mItemRef;
    private DatabaseReference mUserRef;

    private FirebaseMethods mFirebaseMethods;
    private UserInformation currentUser;
    private String currentCommunity;
    private TextView noGroupText;


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
        setupFirebaseAuth();


        //Zuordnung der Firebaseverbindungen

        mRef = mDatabase.getReference();

        //Listview Daten vorbereiten
        listView = findViewById(R.id.items_listView);

        Query query = mRef.child("items").child("Default");



        FirebaseListOptions<Item> options = new FirebaseListOptions.Builder<Item>()
                .setLayout(R.layout.item_information)
                .setQuery(query, Item.class)
                .build();

        //ListViewAdapter
        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
                Item item = (Item) model;

                TextView itemName = v.findViewById(R.id.item_listView_name);
                TextView itemDescription = v.findViewById(R.id.item_listView_description);
                itemImage = v.findViewById(R.id.item_listView_imageView);
                Picasso.get().load(item.getDownloadUrl()).fit().into(itemImage);


                itemName.setText(item.getName());
                itemDescription.setText(item.getDescription());

            }
        };
        listView.setAdapter(adapter);
        noGroupText = findViewById(R.id.textview_no_group);
        noGroupText.setVisibility(View.INVISIBLE);






        //Button zum Hinzufügen von Items
        FloatingActionButton addButton = findViewById(R.id.add_object_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent = new Intent(getBaseContext(), AddingItem.class);
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
    private void setupFirebaseAuth() {
        Log.d("Firebase: ", "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mUserRef = mDatabase.getReference().child("users");
        mItemRef = mDatabase.getReference().child("items");
        currentUser = new UserInformation();

        mFirebaseMethods = new FirebaseMethods(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out
                }
                // ...
            }
        };


        //Listener um den ListView upzudaten
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Löschen des Adapters
                listView.setAdapter(null);

                //Momentane Community wird abgefragt
                String userID = mAuth.getUid().toString();
                currentUser = dataSnapshot.child(userID).getValue(UserInformation.class);
                currentCommunity = currentUser.getCommunityName();

                //
                if(currentCommunity != null){
                    noGroupText.setVisibility(View.INVISIBLE);
                    Query query = mItemRef.child(currentCommunity);
                    Log.d("Update durch Listener: " , "" + currentCommunity);



                    FirebaseListOptions<Item> options = new FirebaseListOptions.Builder<Item>()
                            .setLayout(R.layout.item_information)
                            .setQuery(query, Item.class)
                            .build();

                    //ListViewAdapter
                    adapter = new FirebaseListAdapter(options) {
                        @Override
                        protected void populateView(@NonNull View v, @NonNull Object model, int position) {
                            Item item = (Item) model;

                            TextView itemName = v.findViewById(R.id.item_listView_name);
                            TextView itemDescription = v.findViewById(R.id.item_listView_description);
                            itemImage = v.findViewById(R.id.item_listView_imageView);
                            Picasso.get().load(item.getDownloadUrl()).fit().into(itemImage);


                            itemName.setText(item.getName());
                            itemDescription.setText(item.getDescription());

                        }
                    };
                    listView.setAdapter(adapter);
                    adapter.startListening();
                }
                else{
                    noGroupText = findViewById(R.id.textview_no_group);
                    noGroupText.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

