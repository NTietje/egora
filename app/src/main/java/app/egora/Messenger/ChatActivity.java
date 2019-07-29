package app.egora.Messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import app.egora.Model.Chat;
import app.egora.R;
import app.egora.Utils.FirestoreUtil;
import app.egora.Utils.MessageAdapter;

public class ChatActivity extends AppCompatActivity {

    private  FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private ImageButton sendButton;
    private EditText textToSend;
    private String chatID;
    private String otherChatID;
    private String itemName;
    private String otherUserID;
    private boolean isAtBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Login-Prüfung
        mAuth = FirebaseAuth.getInstance();
        FirestoreUtil.addAuthListener(mAuth, this);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();

        chatID = intent.getStringExtra("chatid");
        otherChatID = intent.getStringExtra("otherchatid");
        itemName = intent.getStringExtra("itemname");
        otherUserID = intent.getStringExtra("otheruserid");
        String otherUserName = intent.getStringExtra("username");
        String initials = intent.getStringExtra("initials");

        getSupportActionBar().hide();

        //set toolbar content
        TextView chatPartnerToolbar = findViewById(R.id.title_toolbar);
        TextView itemNameToolbar = findViewById(R.id.subtitel_toolbar);
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        chatPartnerToolbar.setText(otherUserName);
        itemNameToolbar.setText(itemName);
        toolbar.inflateMenu(R.menu.options_menu_chat);

        sendButton = findViewById(R.id.chat_sendButton);
        textToSend = findViewById(R.id.chat_textToSend);
        Drawable send = sendButton.getDrawable();
        //Set send drawable to grey
        send.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.midGrey), PorterDuff.Mode.SRC_ATOP);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.chat_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MessageAdapter(ChatActivity.this, chatID, initials);

        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (isAtBottom) {
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            }
        };

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    isAtBottom = true;
                } else {
                    isAtBottom = false;
                }
            }
        });

        adapter.registerAdapterDataObserver(observer);
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
                //FirestoreUtil util = new FirestoreUtil();
                //String returnString = util.send2(otherUserID, chatID, otherChatID, itemName, message);
                //Log.d("deb2", "returnStringCHAT: " + returnString);
                sendMessage(message);
                textToSend.getText().clear();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_chat:
                        deleteDialog();
                        return true;
                }
                return false;
            }
        });

    }

    private void deleteDialog() {
        //Creating AlertDialog-Options
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked, delete chat in firestore
                        FirestoreUtil.deleteChat(chatID, otherChatID);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        //Initiating the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setMessage("Bist du sicher, dass du den gesamten Chat löschen möchtest?")
                .setNegativeButton("Nein", dialogClickListener).setPositiveButton("Ja", dialogClickListener);

        AlertDialog alertDialog = builder.create();

        if (alertDialog.isShowing() && alertDialog != null){
            alertDialog.dismiss();
        }
        alertDialog.show();
    }

    //send the message to firestore
    private void sendMessage(final String message) {
        db.collection("chats").document(chatID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String id = documentSnapshot.get("otherChatID").toString();
                boolean otherUserHasChat = (boolean) documentSnapshot.get("otherUserHasChat");
                if(!otherUserHasChat) {
                    createDeletedChat(message);
                }
                else {
                    FirestoreUtil.createAndSendMessage(chatID, otherChatID, message);
                }
            }
        });
    }

    //if chat of chatpartner was deleted, create new chat on chatpartner side
    private void createDeletedChat(final String message) {
        final Chat newChat = new Chat(otherUserID, FirestoreUtil.getCurrentUserID(), itemName);
        DocumentReference chatsRef = db.collection("chats").document();
        otherChatID = chatsRef.getId();
        newChat.setChatID(otherChatID);
        newChat.setOtherChatID(chatID);
        db.collection("chats").document(chatID).update("otherUserHasChat", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("chats").document(otherChatID).set(newChat)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirestoreUtil.createAndSendMessage(otherChatID, chatID, message);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        FancyToast.makeText(ChatActivity.this,e.toString(), FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                    }
                                });
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
