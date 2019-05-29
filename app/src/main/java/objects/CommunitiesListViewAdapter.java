package objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.egora.R;

public class CommunitiesListViewAdapter extends BaseAdapter {

    // Declaration
    private Context mContext;
    private LayoutInflater inflater;
    private List<Community> communities;
    private ArrayList<Community> communitiesArrayList;

    //Constructor


    public CommunitiesListViewAdapter(Context context, List<Community> communities) {
        this.mContext = context;
        this.communities = communities;
        inflater = LayoutInflater.from(mContext);
        this.communitiesArrayList = new ArrayList<>();
        this.communitiesArrayList.addAll(communities);
    }

    public class ViewHolder{
        TextView communityName;
        ImageView privateIcon;
    }

    @Override
    public int getCount() {
        return communities.size();
    }

    @Override
    public Object getItem(int position) {
        return communities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.community_row, null);

            //get views in community-row.xml
            holder.communityName = convertView.findViewById(R.id.communityName);
            holder.privateIcon = convertView.findViewById(R.id.privateIconField);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //fill cummunity row with content
        holder.communityName.setText(communities.get(position).getName());
        if(communities.get(position).getPrivateMode()) {
            holder.privateIcon.setImageResource(R.drawable.ic_lock_24dp);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        
        return convertView;
    }

    //filter
    public void filter(String text) {
        text = text.toLowerCase(Locale.getDefault());
        communities.clear();
        if (text.length()==0) {
            communities.addAll(communitiesArrayList);
        }
        else {
            for (Community community : communitiesArrayList) {
                if (community.getName().toLowerCase(Locale.getDefault()).contains(text)) {
                    communities.add(community);
                }
            }
        }
        notifyDataSetChanged();
    }


}
