package app.egora.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import app.egora.Model.Message;
import app.egora.R;


public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder> {

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT= 1;

    private Message mMessage;
    private String initials;
    private ViewGroup viewG;
    private boolean loaded = false;

    public MessageAdapter(@NonNull Context context, String chatID, String initials) {
        super(new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(FirestoreUtil.getMessagesQuery(chatID), Message.class)
                .build());
        this.initials = initials;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).getSendingUser().equals(FirestoreUtil.getCurrentUserID())) {
            Log.d("user", "user of Mes: " + getItem(position).getSendingUser());
            return MSG_RIGHT;
        }
        return  MSG_LEFT;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        if(viewType == MSG_LEFT) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_left,
                    viewGroup, false);
        }
        else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_right,
                    viewGroup, false);
        }
        return new MessageHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message message) {
        holder.initialsView.setText(initials);
        holder.textMessageView.setText(message.getText());
        holder.dateView.setText(Message.getDateAndTimeOfMessage(message));
    }

    //Chatholder connects to the layout elements
    public class MessageHolder extends RecyclerView.ViewHolder {
        TextView initialsView;
        TextView textMessageView;
        TextView dateView;

        //Constructor
        public MessageHolder(@NonNull View messageView) {
            super(messageView);
            initialsView = messageView.findViewById(R.id.message_initials);
            textMessageView = messageView.findViewById(R.id.message_text);
            dateView = messageView.findViewById(R.id.message_date);
        }
    }


   /* public int getItemViewType(){
        if(mMessage.getSendingUser().equals(FirestoreUtil.getCurrentUserID())) {
            Log.d("user", "user of Mes: " + mMessage.getSendingUser());
            return MSG_RIGHT;
        }
        else {
            return  MSG_LEFT;
        }
    }*/


}