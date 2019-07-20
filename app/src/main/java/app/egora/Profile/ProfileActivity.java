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

import app.egora.ItemManagement.HomeActivity;
import app.egora.Login.LoginActivity;
import app.egora.Messenger.MessengerActivity;
import app.egora.Profile.Fragments.MyInfoFragment;
import app.egora.Profile.Fragments.MyItemsFragment;
import app.egora.R;
import app.egora.Utils.SectionsPageAdapter;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private ViewPager myViewPager;
    private SectionsPageAdapter mySectionsPageAdapter;
    private TabLayout myTablayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Login-Prüfung
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myViewPager = findViewById(R.id.profile_viewpager);

        //Tabs
        myTablayout = findViewById(R.id.profile_tabs);
        myTablayout.setupWithViewPager(myViewPager);

        setupViewPager();

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
            return true;
        }

    };

    private void setupViewPager (){
        //SectionsPageAdapter
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyItemsFragment(), "Meine Items");
        adapter.addFragment(new MyInfoFragment(), "Meine Infos");
        myViewPager.setAdapter(adapter);
    }
}
