package app.egora.ItemManagement;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Ref;

import app.egora.Communities.CommunityInfoActivity;
import app.egora.Login.LoginActivity;
import app.egora.Messenger.ChatActivity;
import app.egora.Model.Chat;
import app.egora.Model.Item;
import app.egora.Model.UserInformation;
import app.egora.R;
import app.egora.Utils.FirestoreUtil;

public class ItemActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference ownerRef;
    private DocumentReference itemRef;
    private StorageReference itemStorageRef;

    private TextView textViewOwnerName;
    private TextView textViewOwnerAddress;
    private TextView textViewItemName;
    private TextView textViewItemDescription;
    private ImageView imageViewPicture;
    private Button buttonContact;

    private Item item;
    private String downloadUrl;
    private String itemName;
    private String itemDescription;
    private String ownerId;
    private String itemId;
    private String ownerName;
    private String ownerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        //Setting up Firebase
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        //Binding view-elements
        textViewOwnerName = findViewById(R.id.item_activity_owner_name);
        textViewOwnerAddress = findViewById(R.id.item_activity_owner_address);
        textViewItemName = findViewById(R.id.item_activity_name);
        textViewItemDescription = findViewById(R.id.item_activity_description);
        imageViewPicture = findViewById(R.id.item_activity_imageView);
        buttonContact = findViewById(R.id.item_activity_button_contact);

        //Loading extra data
        Intent intent = getIntent();
        ownerId = intent.getStringExtra("OWNER_ID");
        itemId = intent.getStringExtra("ITEM_ID");
        downloadUrl = intent.getStringExtra("DOWNLOAD_URL");

        ownerRef = db.collection("users").document(ownerId);
        itemRef = db.collection("items").document(itemId);


        //Getting owner data
        ownerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ownerName = documentSnapshot.get("firstName").toString() + " " + documentSnapshot.get("lastName").toString();
                        ownerAddress = documentSnapshot.get("streetName").toString() + " " + documentSnapshot.get("houseNumber").toString() + " , " + documentSnapshot.get("cityName").toString();
                        textViewOwnerName.setText(ownerName);
                        textViewOwnerAddress.setText(ownerAddress);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String error = "Connection problem";
                textViewOwnerName.setText(error);
                textViewOwnerAddress.setText(error);
            }
        });


        //Getting item data
        itemRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        itemName = documentSnapshot.get("name").toString();
                        textViewItemName.setText(itemName);
                        textViewItemDescription.setText(documentSnapshot.get("description").toString());

                        Picasso.get().load(downloadUrl).fit().into(imageViewPicture);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String error = "Connection problem";
                textViewItemName.setText(error);
                textViewItemDescription.setText(error);
            }
        });

        buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // chat A
                Chat chat = new Chat(FirestoreUtil.getCurrentUserID(), ownerId, ownerName, itemName);
                // chat B
                final Chat itemOwnerChat = new Chat(ownerId, FirestoreUtil.getCurrentUserID(), FirestoreUtil.getCurrentUserName(), itemName);

                //set chat IDs
                DocumentReference chatsRef = db.collection("chats").document();

                final String chatID = chatsRef.getId(); // chat A Id
                Log.d("chat", chatID);
                chat.setChatID(chatID);
                itemOwnerChat.setOtherChatID(chatID); // chat A saved in chat B

                db.collection("chats").document(chatID).set(chat)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DocumentReference chatsRef = db.collection("chats").document();
                            String ownerChatID = chatsRef.getId(); // chat B Id
                            Log.d("chatowner", ownerChatID);
                            itemOwnerChat.setChatID(ownerChatID);
                            db.collection("chats").document(chatID).update("otherChatID", ownerChatID);

                            db.collection("chats").document(ownerChatID).set(itemOwnerChat);

                            final String initials = ownerName.replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2");
                            Intent intent = new Intent(ItemActivity.this, ChatActivity.class);
                            intent.putExtra("chatid", chatID);
                            intent.putExtra("itemname", itemName);
                            intent.putExtra("otheruserid", ownerId);
                            intent.putExtra("otherchatid", ownerChatID);
                            intent.putExtra("username", ownerName);
                            intent.putExtra("initials", initials);
                            ItemActivity.this.startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FancyToast.makeText(ItemActivity.this,"Error Item 1: " + e.toString(), FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        }
                    });
            }
        });

    }

}
