package app.egora.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.egora.Communities.CommunityInfoActivity;
import app.egora.Model.Community;
import app.egora.R;

public class CommunitiesListViewAdapter extends BaseAdapter {

    // Declaration
    private Context mContext;
    private LayoutInflater inflater;
    private List<Community> communityList;
    private ArrayList<Community> communityArrayList;

    //Constructor
    public CommunitiesListViewAdapter(Context context, List<Community> communities) {
        this.mContext = context;
        this.communityList = communities;
        inflater = LayoutInflater.from(mContext); //oder nur context?
        this.communityArrayList = new ArrayList<>();
        this.communityArrayList.addAll(communities);
    }

    public class ViewHolder{
        TextView communityName;
        ImageView privacyIcon;
    }

    @Override
    public int getCount() {
        return communityList.size();
    }

    @Override
    public Object getItem(int position) {
        return communityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Set the community row with content (name and privacy icon)
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view==null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.community_row, null);

            //locate the views in community_row.xml
            holder.communityName = view.findViewById(R.id.communityName);
            holder.privacyIcon = view.findViewById(R.id.privateIconField);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        //fill cummunity row with content
        holder.communityName.setText(communityList.get(position).getName());
        if(communityList.get(position).getPrivacyMode()) { //set lock item if privacy mode is true
            holder.privacyIcon.setImageResource(R.drawable.ic_lock_24dp);
        }
        //listview community clicks
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommunityInfoActivity.class);
                intent.putExtra("name", communityList.get(position).getName());
                intent.putExtra("desc", communityList.get(position).getDescription());
                intent.putExtra("mode", communityList.get(position).getPrivacyMode());
                intent.putExtra("key", communityList.get(position).getKey());
                mContext.startActivity(intent);
            }
        });
        
        return view;
    }

    //filter
    public void filter(String text) {
        text = text.toLowerCase(Locale.getDefault());
        communityList.clear();
        if (text.length()==0) {
            communityList.addAll(communityArrayList);
        }
        else {
            for (Community community : communityArrayList) {
                if (community.getName().toLowerCase(Locale.getDefault()).contains(text)) {
                    communityList.add(community);
                }
            }
        }
        notifyDataSetChanged();
    }


}
