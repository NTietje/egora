package app.egora.Messenger;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

        db = FirebaseFirestore.getInstance();
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
        //linearLayoutManager.setReverseLayout(true);
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
                deleteDialog();
                return true;
        }
        return false;
    }

    private void deleteDialog() {
        //Creating AlertDialog-Options
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
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
        builder.setMessage("Are you sure you want to delete the whole Chat?")
                .setNegativeButton("No", dialogClickListener).setPositiveButton("Yes", dialogClickListener);

        AlertDialog alertDialog = builder.create();

        if (alertDialog.isShowing() && alertDialog != null){
            alertDialog.dismiss();
        }
        alertDialog.show();
    }

    private void sendMessage(final String message) {
        db.collection("chats").document(chatID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String id = documentSnapshot.get("otherChatID").toString();
                if(id.equals("none")) {
                    createDeletetChat(message);
                }
                else {
                    FirestoreUtil.createAndSendMessage(chatID, otherChatID, message);
                }
            }
        });
    }

    private void createDeletetChat(final String message) {
        final Chat newChat = new Chat(otherUserID, FirestoreUtil.getCurrentUserID(), FirestoreUtil.getCurrentUserName(), itemName);
        DocumentReference chatsRef = db.collection("chats").document();
        otherChatID = chatsRef.getId();
        newChat.setChatID(otherChatID);
        newChat.setOtherChatID(chatID);
        db.collection("chats").document(chatID).update("otherChatID", otherChatID)
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

}
