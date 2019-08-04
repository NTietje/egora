package app.egora.Profile;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

import app.egora.Communities.CommunitiesActivity;
import app.egora.ItemManagement.HomeActivity;
import app.egora.Login.LoginActivity;
import app.egora.Messenger.MessengerActivity;
import app.egora.Model.UserInformation;
import app.egora.Profile.Fragments.MyInfoFragment;
import app.egora.Profile.Fragments.MyItemsFragment;
import app.egora.R;
import app.egora.Utils.FirestoreUtil;
import app.egora.Utils.SectionsPageAdapter;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private DocumentReference userRef;

    private ViewPager myViewPager;
    private SectionsPageAdapter mySectionsPageAdapter;
    private TabLayout myTablayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Login-Prüfung
        mAuth = FirebaseAuth.getInstance();
        FirestoreUtil.addAuthListener(mAuth, this);
        db = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myViewPager = findViewById(R.id.profile_viewpager);

        //Tabs
        myTablayout = findViewById(R.id.profile_tabs);
        myTablayout.setupWithViewPager(myViewPager);

        setupViewPager();
        checkCommunity();

        //BottomNavigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.getMenu().getItem(0).setChecked(true);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item){
            Intent intent = new Intent();

            switch (item.getItemId()){
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

    private void setupViewPager (){
        //SectionsPageAdapter
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyItemsFragment(), getString(R.string.myitems));
        adapter.addFragment(new MyInfoFragment(), getString(R.string.myinfo));
        myViewPager.setAdapter(adapter);
    }

    private void checkCommunity(){

        userRef = db.collection("users").document(mAuth.getUid());
        userRef.addSnapshotListener(ProfileActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null){
                    UserInformation currentUser = documentSnapshot.toObject(UserInformation.class);

                    if(currentUser.getCommunityName().equals("changing")){
                        Intent intent = new Intent(getBaseContext(), CommunitiesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                }

            }
        });
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
