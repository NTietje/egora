package app.egora.ItemManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import app.egora.Model.Item;
import app.egora.Model.UserInformation;
import app.egora.R;

public class AddingItem extends AppCompatActivity {


    //Deklaration
    private static final int pic_id = 123;


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProgressDialog progressDialog;
    private UserInformation currentUser;
    private String ownerId;
    private String itemId;
    private String ownerCommunity;
    private Item item;
    private String downloadUrl;
    private Boolean tookPicture;

    private ImageView editImageView;
    private EditText editItemName;
    private EditText editItemDescription;
    private Spinner spinner;
    private Button buttonReset;
    private Button buttonItemInsert;
    private Uri mImageUri;
    private Bitmap photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_item);
        db = FirebaseFirestore.getInstance();
        tookPicture = false;


        //Firebase Komponenten laden
        mAuth = FirebaseAuth.getInstance();

        //Verbindung mit Firebase
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //Abfragen der Community aus dem Homescreen
        Intent intent = getIntent();
        ownerCommunity = "testcommunity";
        if(intent.getStringExtra("USER_COMMUNITY")!=null){
            ownerCommunity = intent.getStringExtra("USER_COMMUNITY");
        }

        //Zuweisung der Viewelemente
        progressDialog = new ProgressDialog(this);
        editItemName = findViewById(R.id.item_name);
        editItemDescription = findViewById(R.id.item_description);
        spinner = findViewById(R.id.item_category_spinner);
        buttonReset = findViewById(R.id.button_reset_item);
        buttonItemInsert = findViewById(R.id.button_insert_item);

        //Zuweisung des ImageViews
        editImageView = (ImageView) findViewById(R.id.item_imageView);

        //Laden des KategorieArrays

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //Onclick-Listener für Bildaufnahme
        editImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Starten der Handykamera
                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);

                //Starten der Activität mit Rückgabe der BildID
                startActivityForResult(camera_intent, pic_id);

            }
        });

        //Onclick-Listener für Buttons (Reset und Insert)
        buttonItemInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    registerItem();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddingItem.this, AddingItem.class));
                finish();
            }
        });
    }

    // Übergabe des Bildes
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        if (requestCode == pic_id  && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras()
                    .get("data");
            editImageView.setImageBitmap(photo);
            tookPicture = true;
        }
    }

    //Methode um die Itemdaten an Firebase zu übergeben
    private void registerItem() {
        progressDialog.setMessage("Lade hoch...");

        final String itemName = editItemName.getText().toString().trim();
        final String itemDescription = editItemDescription.getText().toString().trim();
        final String category = spinner.getSelectedItem().toString();
        final String ownerId = mAuth.getCurrentUser().getUid();

        progressDialog.show();

        //&& !TextUtils.isEmpty(itemDescription) && !TextUtils.isEmpty(ownerCommunity)
        if (!TextUtils.isEmpty(itemName) && tookPicture && !category.equals("Kategorie")  ) {
            //Erstellung einer einzigartigen ID

            DocumentReference itemRef = db.collection("items").document();
            itemId = itemRef.getId();
            final StorageReference filePath = mStorage.child("Item_Images").child(itemId);

            //Upload des Bildes
            editImageView.setDrawingCacheEnabled(true);
            editImageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) editImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        downloadUrl = task.getResult().toString();

                        Map<String, Object> newItem = new HashMap<>();
                        newItem.put("name", itemName);
                        newItem.put("description" , itemDescription);
                        newItem.put("category" , category);
                        newItem.put("downloadUrl", downloadUrl);
                        newItem.put("ownerId", ownerId);
                        newItem.put("itemId", itemId);

                        //OWNERCOMMUNITY MUSS NOCH ALLGEMEIN GESCHRIEBEN WERDEN
                        newItem.put("communityName", ownerCommunity);

                        db.collection("items").document(itemId)
                                .set(newItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                FancyToast.makeText(AddingItem.this,itemName + " wurde erfolgreich hochgeladen!", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        FancyToast.makeText(AddingItem.this,"Da lief etwas schief!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    }
                }
            });

        } else {
            progressDialog.dismiss();
            if(TextUtils.isEmpty(itemName) && tookPicture && !category.equals("Kategorie")){
                FancyToast.makeText(AddingItem.this,"Gebe einen Namen ein!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            }
            else if(!tookPicture && !TextUtils.isEmpty(itemName) && !category.equals("Kategorie") ){
                FancyToast.makeText(AddingItem.this,"Nehme ein Foto auf!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            }

            else if(tookPicture && !TextUtils.isEmpty(itemName) && category.equals("Kategorie")){
                FancyToast.makeText(AddingItem.this,"Wähle eine Kategorie aus!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            }
            else{
                FancyToast.makeText(AddingItem.this,"Du musst zuerst Informationen eingeben!", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null){
        progressDialog.dismiss();
        }
    }

}

