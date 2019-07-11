package app.egora.Messenger;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

import app.egora.Model.Chat;
import app.egora.Model.Message;
import app.egora.R;
import app.egora.Utils.ChatAdapter;
import app.egora.Utils.FirestoreUtil;
import app.egora.Utils.MessageAdapter;

public class ChatActivity extends AppCompatActivity {

    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private ImageButton sendButton;
    private EditText textToSend;
    private String chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_recyclerView);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        Intent intent = getIntent();
        String otherUserName = intent.getStringExtra("username");
        String initials = intent.getStringExtra("initials");
        chatID = intent.getStringExtra("chatid");
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(otherUserName);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sendButton = findViewById(R.id.chat_sendButton);
        textToSend = findViewById(R.id.chat_textToSend);
        Drawable iv = sendButton.getDrawable();
        iv.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.midGrey), PorterDuff.Mode.SRC_ATOP);

        /*FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Message>()
                //.setQuery(FirestoreUtil.getChatQuery(chatID), Chat.class)
                .setQuery(FirestoreUtil.getMessagesQuery(chatID), Message.class)
                .build();*/

        adapter = new MessageAdapter(ChatActivity.this, chatID, initials);

        recyclerView.setAdapter(adapter);
        adapter.startListening();



        textToSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Drawable iv = sendButton.getDrawable();
                if(textToSend.getText().toString().trim().isEmpty()) {
                    sendButton.setClickable(false);
                    iv.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.midGrey), PorterDuff.Mode.SRC_ATOP);
                }
                else{
                    sendButton.setClickable(true);
                    iv.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FancyToast.makeText(ChatActivity.this,"ON CLICK " + chatID,
                        FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                //FirestoreUtil.createAndSendMessage(chatID, textToSend.getText().toString(), ChatActivity.this);
                FirestoreUtil.createAndSendMessage2(chatID, textToSend.getText().toString(), ChatActivity.this, recyclerView, adapter);
                textToSend.getText().clear();
            }
        });
    }

}
