package app.egora.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import app.egora.Messenger.ChatActivity;
import app.egora.Model.Chat;
import app.egora.Model.Message;
import app.egora.Model.UserInformation;
import app.egora.R;

public class ChatAdapter extends FirestoreRecyclerAdapter <Chat, ChatAdapter.ChatHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Context mContext;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ChatHolder chatHolder, int position, @NonNull final Chat chatModel) {
        db.collection("users").document(chatModel.getOtherUserID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final String otherUserName = documentSnapshot.toObject(UserInformation.class).getFullName();

                //set chatholder ui content
                chatHolder.textViewUserName.setText(otherUserName);
                chatHolder.textViewItemName.setText(chatModel.getItemTitle());
                chatHolder.textViewLastMessage.setText(chatModel.getLastMessageText());
                chatHolder.textViewDate.setText(Message.getDateStringOfDate(chatModel.getLastActivity()));
                final String initials = otherUserName.replaceAll("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$", "$1$2");
                chatHolder.textViewInitials.setText(initials);

                //create ChatActivity on click
                chatHolder.getChatView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("chatid", chatModel.getChatID());
                        intent.putExtra("otherchatid", chatModel.getOtherChatID());
                        intent.putExtra("otheruserid", chatModel.getOtherUserID());
                        intent.putExtra("itemname", chatModel.getItemTitle());
                        intent.putExtra("username", otherUserName);
                        intent.putExtra("initials", initials);
                        mContext.startActivity(intent);
                    }
                });
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
        private ChatHolder(@NonNull View chatView) {
            super(chatView);
            this.chatView = chatView;
            textViewUserName = chatView.findViewById(R.id.textViewUserName);
            textViewItemName = chatView.findViewById(R.id.textViewItemTitle);
            textViewLastMessage = chatView.findViewById(R.id.textViewLastMessage);
            textViewDate = chatView.findViewById(R.id.textViewDate);
            textViewInitials = chatView.findViewById(R.id.initials);
        }

        private View getChatView()
        {
            return chatView;
        }
    }


}
