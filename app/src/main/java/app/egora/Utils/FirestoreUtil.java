package app.egora.Utils;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import app.egora.Model.Message;

public class FirestoreUtil {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static Query getUserChatsQuery(String userID) {
        Query query = db.collection("chats").whereEqualTo("userID", userID).orderBy("lastActivity", Query.Direction.DESCENDING);
        return query;
    }

    public static Query getMessagesQuery(String chatID) {
        Query query = db.collection("chats").document(chatID).collection("messages").orderBy("date", Query.Direction.DESCENDING);
        return query;
    }

    public static String getCurrentUserID() {
        return mAuth.getCurrentUser().getUid();
    }

    public static void deleteChatDocument(String chatID) {
        db.collection("chats").document(chatID)
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error FUtil 3: ", e.toString());
                    }
                });
    }

    public static void createAndSendMessage(String chatID, String textMessage, final RecyclerView recyclerView, final MessageAdapter adapter) {
        Message message = new Message(getCurrentUserID(), textMessage);

        db.collection("chats").document(chatID).collection("messages").document().set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        recyclerView.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error FUtil 1: ", e.toString());
                    }
                });

        db.collection("chats").document(chatID).update(
                "lastActivity", message.getDate(),
                "lastMessageText", message.getText())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error FUtil 2: ", e.toString());
                    }
                });

    }

    public static void signOut() {
        mAuth.signOut();
    }

}
