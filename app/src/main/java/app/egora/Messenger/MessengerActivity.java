package app.egora.Messenger;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import app.egora.ItemManagement.HomeActivity;
import app.egora.Login.LoginActivity;
import app.egora.Model.Chat;
import app.egora.Model.UserInformation;
import app.egora.Profile.ProfileActivity;
import app.egora.R;
import app.egora.Utils.ChatAdapter;
import app.egora.Utils.FirestoreUtil;


public class MessengerActivity extends AppCompatActivity {

    //Declaration
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        getSupportActionBar().hide();

        recyclerView = findViewById(R.id.messenger_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        //Bottom Navigation Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.getMenu().getItem(2).setChecked(true);

        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(FirestoreUtil.getUserChatsQuery(FirestoreUtil.getCurrentUserID()), Chat.class)
                .build();

        adapter = new ChatAdapter(options, MessengerActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    //Toolbar Menu Hinzufügen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    //Bottom Navigationsbar
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


}
