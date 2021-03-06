package app.egora.Utils;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import app.egora.Model.Item;
import app.egora.R;

public class MyItemAdapter extends FirestoreRecyclerAdapter <Item, MyItemAdapter.ItemHolder> {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference itemRef;
    private StorageReference storageReference;

    private AlertDialog alertDialog;

    public MyItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull Item model) {
        //Filling View
        holder.itemName.setText(model.getName());
        holder.itemDescription.setText(model.getDescription());
        Picasso.get().load(model.getDownloadUrl()).centerCrop().resize(100, 100).into(holder.itemPicture);

        //Setting ItemHolderValues
        holder.setItemId(model.getItemId());
        holder.setItemUrl(model.getDownloadUrl());
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_item_information,
                viewGroup, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        return new ItemHolder(v);
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemDescription;
        ImageView deleteIcon;
        ImageView itemPicture;
        DatabaseReference databaseReference;
        StorageReference storageReference;
        String itemId;
        String itemUrl;

        public ItemHolder(@NonNull final View itemView) {
            super(itemView);

            //Binding View
            itemName = itemView.findViewById(R.id.my_item_textView_name);
            itemDescription = itemView.findViewById(R.id.my_item_textView_description);
            itemPicture = itemView.findViewById(R.id.my_item_imageView);
            deleteIcon = itemView.findViewById(R.id.my_item_delete_icon);

            //Deletion Listener
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Creating AlertDialog-Options
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked

                                    db.collection("items").document(itemId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            storageReference = storage.getReferenceFromUrl(itemUrl);
                                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FancyToast.makeText(itemView.getContext(),itemName.getText() + " wurde erfolgreich gelöscht!", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                                }
                                            });
                                        }
                                    });
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    //Initiating the Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Willst du '" + itemName.getText() + "' wirklich löschen?")
                            .setNegativeButton("Nein", dialogClickListener).setPositiveButton("Ja", dialogClickListener);

                    alertDialog = builder.create();
                    if (alertDialog.isShowing() && alertDialog != null){
                        alertDialog.dismiss();
                    }
                    alertDialog.show();
                }
            });

        }

        //Setters für Itemholder (so the values dont get mixed up)
        public void setItemId(String itemId){
            this.itemId = itemId;
        }

        public void setItemUrl(String itemUrl){
            this.itemUrl = itemUrl;
        }

    }

}

