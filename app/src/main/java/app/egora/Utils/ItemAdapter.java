package app.egora.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import app.egora.Model.Item;
import app.egora.R;

public class ItemAdapter extends FirestoreRecyclerAdapter <Item, ItemAdapter.ItemHolder> {


    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull Item model) {

        holder.textViewItemName.setText(model.getName());
        holder.textViewitemDescription.setText(model.getDescription());
        Picasso.get().load(model.getDownloadUrl()).fit().into(holder.imageViewitemImage);
        Log.d("ItemName: " , model.getName());
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
        ImageView imageViewitemImage;


        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.item_textView_name);
            textViewitemDescription = itemView.findViewById(R.id.item_textView_description);
            imageViewitemImage = itemView.findViewById(R.id.item_imageView);


                    /* TextView itemName = v.findViewById(R.id.item_listView_name);
                                    TextView itemDescription = v.findViewById(R.id.item_listView_description);
                                    itemImage = v.findViewById(R.id.item_listView_imageView);
                                    Picasso.get().load(item.getDownloadUrl()).fit().into(itemImage);
                                    */
        }
    }
}
