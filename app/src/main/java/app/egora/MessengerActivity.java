package app.egora;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.egora.Model.UserInformation;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "MessengerActivity";


    private FirebaseAuth mAuth;
    private Toolbar myToolbar;
    private ViewPager myViewPager;
    private TabLayout myTablayout;
    private SectionsPageAdapter mySectionsPageAdapter;
    private CircleImageView profileImage;
    private TextView userName;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);


        //Setting up Toolbar and Database
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.username);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                userName.setText(userInformation.getFullName());
                if(userInformation.getImageURL().equals("default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessengerActivity.this).load(userInformation.getImageURL()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Bottom Navigation Menu
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_logout:
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent (MessengerActivity.this, LoginActivity.class));
            finish();
            return true;

        }
        return false;
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
