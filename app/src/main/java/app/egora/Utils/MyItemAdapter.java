package app.egora.Utils;

import android.content.DialogInterface;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import app.egora.Communities.NewCommunityActivity;
import app.egora.Model.Item;
import app.egora.R;

public class MyItemAdapter extends FirestoreRecyclerAdapter <Item, MyItemAdapter.ItemHolder> {

    private FirebaseFirestore db;
    private DocumentReference itemRef;

    private String itemId;
    private String itemName;


    public MyItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull Item model) {

        holder.itemName.setText(model.getName());
        holder.itemDescription.setText(model.getDescription());
        Picasso.get().load(model.getDownloadUrl()).fit().into(holder.itemPicture);
        Picasso.get().load(R.drawable.ic_delete).resize(75,75).into(holder.deleteIcon);

        itemId = model.getItemId();
        itemName = model.getName();

    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_item_information,
                viewGroup, false);

        db = FirebaseFirestore.getInstance();

        return new ItemHolder(v);
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView itemDescription;
        ImageView deleteIcon;
        ImageView itemPicture;


        public ItemHolder(@NonNull final View itemView) {
            super(itemView);


            itemName = itemView.findViewById(R.id.my_item_textView_name);
            itemDescription = itemView.findViewById(R.id.my_item_textView_description);
            itemPicture = itemView.findViewById(R.id.my_item_imageView);
            deleteIcon = itemView.findViewById(R.id.my_item_delete_icon);
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

                                            FancyToast.makeText(itemView.getContext(),itemName.getText() + " was successfully removed!", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();

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
                    builder.setMessage("Are you sure you want to delete " + itemName.getText() + "?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });




                    /* TextView itemName = v.findViewById(R.id.item_listView_name);
                                    TextView itemDescription = v.findViewById(R.id.item_listView_description);
                                    itemImage = v.findViewById(R.id.item_listView_imageView);
                                    Picasso.get().load(item.getDownloadUrl()).fit().into(itemImage);
                                    */
        }
    }


}
