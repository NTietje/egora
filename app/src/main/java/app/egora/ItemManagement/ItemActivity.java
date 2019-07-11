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
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Ref;

import app.egora.Login.LoginActivity;
import app.egora.Model.Item;
import app.egora.R;

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
                        textViewItemName.setText(documentSnapshot.get("name").toString());
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







    }

}
