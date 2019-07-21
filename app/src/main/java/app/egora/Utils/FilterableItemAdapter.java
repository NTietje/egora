package app.egora.Utils;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import app.egora.ItemManagement.ItemActivity;
import app.egora.Model.Item;
import app.egora.R;


public class FilterableItemAdapter extends RecyclerView.Adapter<FilterableItemAdapter.ItemHolder> {

    private List<Item> itemList;
    private List<Item> filteredItemList;

    public FilterableItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
        filteredItemList = itemList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_information,
                viewGroup, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int i) {
        final Item item = filteredItemList.get(i);
        final String itemName = item.getName();
        final String itemDescription = item.getDescription();
        final String ownerId = item.getOwnerId();
        final String itemId = item.getItemId();
        final String downloadUrl = item.getDownloadUrl();

        holder.textViewItemName.setText(itemName);
        holder.textViewitemDescription.setText(itemDescription);
        Picasso.get()
                .load(downloadUrl)
                .centerCrop()
                .resize(100,100)
                .into(holder.imageViewItemImage);

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

    @Override
    public int getItemCount() {
        return filteredItemList.size();
    }

    //filter
    public void filter(String text) {
        text = text.toLowerCase().trim();
        filteredItemList.clear();
        if (text.isEmpty()) {
            filteredItemList = itemList;
        }
        else {
            for (Item item : itemList) {
                if (item.getName().toLowerCase(Locale.getDefault()).contains(text)) {
                    filteredItemList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public Filter getFilter(final String category) {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String pattern = constraint.toString().toLowerCase().trim();
                if(pattern.isEmpty() && category.equals("Alle")) { //category empty, text empty
                    filteredItemList = itemList;
                }
                else {
                    List<Item> filteredList = new ArrayList<>();
                    for(Item item : itemList){
                        Log.d("deb7", "category: " +  category);
                        Log.d("deb7", "itemcategory: " +  item.getCategory());
                        if(pattern.isEmpty() && category.equals(item.getCategory())) { //category filled, text empty
                            filteredList.add(item);
                            Log.d("deb8:", "item gefunden: " + item.getName());
                        }
                        else if(category.equals("Alle") && item.getName().toLowerCase().contains(pattern)) { //category empty, text filled
                            filteredList.add(item);
                        }
                        else if(category.equals(item.getCategory()) && item.getName().toLowerCase().contains(pattern)) { //category filled, text filled
                            filteredList.add(item);
                        }
                    }
                    filteredItemList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItemList = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView textViewItemName;
        TextView textViewitemDescription;
        TextView textViewownerName;
        String ownerId;
        String ownerFirstName;
        String ownerLastName;
        RelativeLayout relativeLayout;
        ImageView imageViewItemImage;

        public ItemHolder(@NonNull final View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.item_information_layout);
            textViewItemName = itemView.findViewById(R.id.item_textView_name);
            textViewitemDescription = itemView.findViewById(R.id.item_textView_description);
            imageViewItemImage = itemView.findViewById(R.id.item_imageView);
        }
    }
}
