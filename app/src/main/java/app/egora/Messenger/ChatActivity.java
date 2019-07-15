package app.egora.Messenger;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shashank.sony.fancytoastlib.FancyToast;

import app.egora.Communities.CommunityInfoActivity;
import app.egora.Model.Chat;
import app.egora.R;
import app.egora.Utils.FirestoreUtil;
import app.egora.Utils.MessageAdapter;

public class ChatActivity extends AppCompatActivity {

    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private ImageButton sendButton;
    private EditText textToSend;
    private String chatID;
    private String otherChatID;
    private String itemName;
    private String otherUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        chatID = intent.getStringExtra("chatid");
        otherChatID = intent.getStringExtra("otherchatid");
        itemName = intent.getStringExtra("itemname");
        otherUserID = intent.getStringExtra("otheruserid");
        String otherUserName = intent.getStringExtra("username");
        String initials = intent.getStringExtra("initials");

        //set username in top actionbar
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(otherUserName);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Back Pfeil in der Toolbar oben links hinzufügen
        }

        sendButton = findViewById(R.id.chat_sendButton);
        textToSend = findViewById(R.id.chat_textToSend);
        Drawable send = sendButton.getDrawable();
        //Set send drawable to grey
        send.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.midGrey), PorterDuff.Mode.SRC_ATOP);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView = findViewById(R.id.chat_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MessageAdapter(ChatActivity.this, chatID, initials);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        //TextEdit Listener
        textToSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Drawable send = sendButton.getDrawable();
                if(textToSend.getText().toString().trim().isEmpty()) {
                    sendButton.setClickable(false);
                    send.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.midGrey), PorterDuff.Mode.SRC_ATOP);
                }
                else{
                    sendButton.setClickable(true);
                    send.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });



        //Button Listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textToSend.getText().toString();
                FirestoreUtil.send(otherUserID, chatID, otherChatID, itemName, message, recyclerView);
                textToSend.getText().clear();
            }
        });
    }

    //Toolbar Menu Hinzufügen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_chat, menu);
        return true;
    }

    //Toolbar Menu - Funktionen (Logout)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_chat:
                FirestoreUtil.deleteChat(chatID, otherChatID);
                finish();
                return true;
        }
        return false;
    }

}
