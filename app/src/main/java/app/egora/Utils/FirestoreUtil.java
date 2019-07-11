package app.egora.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.w3c.dom.Document;

import java.util.ArrayList;

import app.egora.Communities.CommunitiesActivity;
import app.egora.Model.Chat;
import app.egora.Model.CommunitiesListViewAdapter;
import app.egora.Model.Community;
import app.egora.Model.Message;

public class FirestoreUtil {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static Query getUserChatsQuery(String userID) {
        Query query = db.collection("chats").whereEqualTo("userID", userID).orderBy("lastActivity", Query.Direction.DESCENDING);
        return query;
    }

    public static Query getMessagesQuery(String chatID) {
        Log.d("con2", "in getMessagesQuery: chatid: "+ chatID);
        Query query = db.collection("chats").document(chatID).collection("messages").orderBy("date", Query.Direction.DESCENDING);
        return query;
    }

    public static String getCurrentUserID() {
        return mAuth.getCurrentUser().getUid();
    }

    public static void createAndSendMessage(String chatID, String textMessage, final Context context) {
        Message message = new Message(getCurrentUserID(), textMessage);

        db.collection("chats").document(chatID).update("messages", FieldValue.arrayUnion(message))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FancyToast.makeText(context,"Nachricht hinzugefügt",
                                FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FancyToast.makeText(context,"Error: " + e.toString(),
                                FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    }
                });
    }

    public static void createAndSendMessage2(String chatID, String textMessage, final Context context, final RecyclerView recyclerView, final MessageAdapter adapter) {
        Message message = new Message(getCurrentUserID(), textMessage);

        db.collection("chats").document(chatID).collection("messages").document().set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        recyclerView.smoothScrollToPosition(0);
                        FancyToast.makeText(context,"Nachricht hinzugefügt",
                                FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FancyToast.makeText(context,"Error:1 " + e.toString(),
                                FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    }
                });

        db.collection("chats").document(chatID).update(
                "lastActivity", message.getDate(),
                "lastMessageText", message.getText())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FancyToast.makeText(context,"Activity und Text aktualisiert",
                                FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FancyToast.makeText(context,"Error:2 " + e.toString(),
                                FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    }
                });

    }

}
