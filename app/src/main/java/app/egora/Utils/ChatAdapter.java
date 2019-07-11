package app.egora.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.egora.Messenger.ChatActivity;
import app.egora.Model.Chat;
import app.egora.Model.Message;
import app.egora.R;

public class ChatAdapter extends FirestoreRecyclerAdapter <Chat, ChatAdapter.ChatHolder> {

    private Context mContext;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatHolder chatHolder, int position, @NonNull final Chat chatModel) {
        //Message lastMessage = chatModel.getMessages().get((chatModel.getMessages().size()-1));
        final String initials = chatModel.getOtherUserName().replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2");
        chatHolder.textViewUserName.setText(chatModel.getOtherUserName());
        chatHolder.textViewItemName.setText(chatModel.getItemTitle());
        chatHolder.textViewLastMessage.setText(chatModel.getLastMessageText());
        chatHolder.textViewDate.setText(Message.getDateStringOfDate(chatModel.getLastActivity()));
        chatHolder.textViewInitials.setText(initials);

        chatHolder.getChatView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("chatid", chatModel.getChatID());
                intent.putExtra("username", chatModel.getOtherUserName());
                intent.putExtra("initials", initials);
                /*Bundle bundle = new Bundle();
                bundle.putSerializable("messages", chatModel.getMessages());
                intent.putExtras(bundle);*/
                mContext.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_row,
                viewGroup, false);
        return new ChatHolder(v);
    }

    //Chatholder connects to the layout elements
    class ChatHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        TextView textViewItemName;
        TextView textViewLastMessage;
        TextView textViewDate;
        TextView textViewInitials;
        View chatView;

        //Constructor
        public ChatHolder(@NonNull View chatView) {
            super(chatView);
            this.chatView = chatView;
            textViewUserName = chatView.findViewById(R.id.textViewUserName);
            textViewItemName = chatView.findViewById(R.id.textViewItemTitle);
            textViewLastMessage = chatView.findViewById(R.id.textViewLastMessage);
            textViewDate = chatView.findViewById(R.id.textViewDate);
            textViewInitials = chatView.findViewById(R.id.initials);
        }

        public View getChatView()
        {
            return chatView;
        }
    }


}
