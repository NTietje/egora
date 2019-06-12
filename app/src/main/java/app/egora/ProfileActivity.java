package app.egora;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import app.egora.ItemManagement.HomeActivity;
import app.egora.Messenger.MessengerActivity;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout){
            //Logout Methode
            logoutUser();
        }

        else if(item.getItemId() == R.id.main_show_user_profile){
            //Show user
        }
        return true;
    }

    public boolean logoutUser(){
        mAuth.signOut();
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
    */

}
