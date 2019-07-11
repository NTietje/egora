package app.egora.Messenger;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.w3c.dom.Document;

import java.util.ArrayList;

import javax.annotation.Nullable;

import app.egora.Communities.CommunitiesActivity;
import app.egora.ItemManagement.HomeActivity;
import app.egora.Login.LoginActivity;
import app.egora.Model.Chat;
import app.egora.Model.CommunitiesListViewAdapter;
import app.egora.Model.Community;
import app.egora.Model.Item;
import app.egora.Model.Message;
import app.egora.Model.UserInformation;
import app.egora.ProfileActivity;
import app.egora.R;
import app.egora.Utils.ChatAdapter;
import app.egora.Utils.FirestoreUtil;
import app.egora.Utils.ItemAdapter;


public class MessengerActivity extends AppCompatActivity {

    ///Declaration Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    //private DocumentReference chatsRef;

    //Declaration
    private String currentUserID;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private String otherUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Messages");
        }

        FloatingActionButton testbutton = findViewById(R.id.addTestButton);
        recyclerView = findViewById(R.id.messenger_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        //Firestore
        db = FirebaseFirestore.getInstance();
        //Login
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("n4@gmail.com", "nana2014"); //später entfernen ***********
        currentUserID = FirestoreUtil.getCurrentUserID();

        testbutton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  otherUserID = "bM3BoBky6cX9jiT7e65bLBR8IXR2";
                  //otherUserID = "bM3BoBky6cX9jiT7e65bLBR8IXR2";
                  db.collection("users").document(otherUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                      @Override
                      public void onSuccess(DocumentSnapshot documentSnapshot) {
                          UserInformation otherUser = documentSnapshot.toObject(UserInformation.class);
                          String otherUserName = otherUser.getFullName();


                              /*Message m1 = new Message(mAuth.getCurrentUser().getUid(), "hallo, ich bin user1");
                              Message m2 = new Message(otherUserID, "hallo, ich bin user2");
                              Message m3 = new Message(mAuth.getCurrentUser().getUid(), "Ist die Kommode noch da?");
                              Message m4 = new Message(otherUserID, "Ja, ist noch da.");
                              ArrayList<Message> list = new ArrayList<>();
                              list.add(m1);
                              list.add(m2);
                              list.add(m3);
                              list.add(m4);*/

                              Chat chat = new Chat(mAuth.getCurrentUser().getUid(), otherUserID, otherUserName, "Akku Bohrer");
                              //Chat chat = new Chat(otherUserID, mAuth.getCurrentUser().getDisplayName(), "Akku Bohrschrauber");
                              //chat.setMessages(list);


                              //set new chat in firestore
                              DocumentReference chatsRef = db.collection("chats").document();
                              String chatID = chatsRef.getId();
                              chat.setChatID(chatID);

                              //db.collection("communities").document(name).update("userIDs", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));
                              db.collection("chats").document(chatID).set(chat)
                                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                              FancyToast.makeText(MessengerActivity.this,"Chat erstellt",
                                                      FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                          }
                                      })
                                      .addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              FancyToast.makeText(MessengerActivity.this,"Chat wurde nicht erstellt",
                                                      FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                          }
                                      });



                      }
                  });
              }
          });

        //Bottom Navigation Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.getMenu().getItem(2).setChecked(true);

        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(FirestoreUtil.getUserChatsQuery(currentUserID), Chat.class)
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

    //Toolbar Menu - Funktionen (Logout)
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
