package app.egora;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "MessengerActivity";


    private FirebaseAuth mAuth;
    private Toolbar myToolbar;
    private ViewPager myViewPager;
    private TabLayout myTablayout;
    private SectionsPageAdapter mySectionsPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);



        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.getMenu().getItem(2).setChecked(true);


        //myToolbar = (Toolbar) findViewById(R.id.);
        getSupportActionBar().setTitle("egora");
        myViewPager = (ViewPager) findViewById(R.id.main_messenger_viewpager);
        setupViewPager(myViewPager);
        mySectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        myTablayout = (TabLayout) findViewById(R.id.main_messenger_tabs);
        myTablayout.setupWithViewPager(myViewPager);
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

    private void setupViewPager (ViewPager myViewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(), "Chats");
        adapter.addFragment(new ContactsFragment(), "Contacts");
        myViewPager.setAdapter(adapter);
    }

}
