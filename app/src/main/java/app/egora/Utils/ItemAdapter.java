package app.egora.Utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import app.egora.ItemManagement.AddingItem;
import app.egora.ItemManagement.ItemActivity;
import app.egora.Model.Item;
import app.egora.R;

public class ItemAdapter extends FirestoreRecyclerAdapter <Item, ItemAdapter.ItemHolder> {


    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull Item model) {

        final String itemName = model.getName();
        final String itemDescription = model.getDescription();
        final String ownerId = model.getOwnerId();
        final String itemId = model.getItemId();
        final String downloadUrl = model.getDownloadUrl();

        holder.textViewItemName.setText(itemName);
        holder.textViewitemDescription.setText(itemDescription);
        Picasso.get()
                .load(downloadUrl)
                .centerCrop()
                .resize(100,100)
                .into(holder.imageViewitemImage);
        Log.d("ItemName: " , model.getName());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        Intent intent = new Intent(v.getContext(), ItemActivity.class);
                        intent.putExtra("ITEM_ID", itemId);
                        intent.putExtra("OWNER_ID", ownerId);
                        intent.putExtra("DOWNLOAD_URL", downloadUrl);
                        v.getContext().startActivity(intent);

            }
        });



    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_information,
                viewGroup, false);
        return new ItemHolder(v);
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView textViewItemName;
        TextView textViewitemDescription;

        TextView textViewownerName;
        String ownerId;
        String ownerFirstName;
        String ownerLastName;
        RelativeLayout relativeLayout;



        ImageView imageViewitemImage;



        public ItemHolder(@NonNull final View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.item_information_layout);
            textViewItemName = itemView.findViewById(R.id.item_textView_name);
            textViewitemDescription = itemView.findViewById(R.id.item_textView_description);
            imageViewitemImage = itemView.findViewById(R.id.item_imageView);




        }
    }
}
