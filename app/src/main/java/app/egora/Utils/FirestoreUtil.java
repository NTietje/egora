package app.egora.Utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

import javax.annotation.Nullable;

import app.egora.ItemManagement.HomeActivity;
import app.egora.ItemManagement.ItemActivity;
import app.egora.Login.LoginActivity;
import app.egora.Messenger.ChatActivity;
import app.egora.Model.Chat;
import app.egora.Model.Message;
import app.egora.Model.UserInformation;

public class FirestoreUtil {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseAuth.AuthStateListener mAuthListener;


    public FirestoreUtil() {
    }

    public static Query getUserChatsQuery(String userID) {
        return db.collection("chats").whereEqualTo("userID", userID).orderBy("lastActivity", Query.Direction.DESCENDING);
    }

    public static Query getMessagesQuery(String chatID) {
        return db.collection("chats").document(chatID).collection("messages").orderBy("date", Query.Direction.ASCENDING);
    }

    public static String getCurrentUserID() {
        return mAuth.getCurrentUser().getUid();
    }

    public static String getCurrentUserName() {
        return mAuth.getCurrentUser().getDisplayName();
    }


    public static void deleteChat(String chatID, String otherChatID) {

        //delete messages in subcollection of chatID
        db.collection("chats").document(chatID).collection("messages").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = db.batch();
                        if(!queryDocumentSnapshots.isEmpty()){
                            //Delete every message
                            for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                                String mes = snapshot.getReference().getId();
                                batch.delete(snapshot.getReference());
                            }
                        }
                        batch.commit();
                    }
                });

        //change ID in otherChat document to "none"
        db.collection("chats").document(otherChatID).update("otherChatID", "none");

        //delete chat document of chatID
        db.collection("chats").document(chatID)
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error FUtil 3: ", e.toString());
                    }
                });
    }


    public static void createAndSendMessage(String chatID, String otherChatID, String textMessage) {
        Message message = new Message(getCurrentUserID(), textMessage);
        //chat A
        db.collection("chats").document(chatID).collection("messages").document().set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error FUtil 1a: ", e.toString());
                    }
                });

        db.collection("chats").document(chatID).update("lastActivity", message.getDate(), "lastMessageText", message.getText())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error FUtil 2a: ", e.toString());
                    }
                });

        //chat B
        db.collection("chats").document(otherChatID).collection("messages").document().set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error FUtil 1b: ", e.toString());
                    }
                });

        db.collection("chats").document(otherChatID).update("lastActivity", message.getDate(), "lastMessageText", message.getText())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error FUtil 2b: ", e.toString());
                    }
                });
    }


    public static void signOut() {
        mAuth.signOut();
    }


    public static void addAuthListener(final FirebaseAuth mAuth, final Activity activity){
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null){
                    Intent intent = new Intent(activity.getBaseContext(), LoginActivity.class);
                    activity.startActivity(intent);
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

    }

    public static void removeAuthListener(){
        if(mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}
