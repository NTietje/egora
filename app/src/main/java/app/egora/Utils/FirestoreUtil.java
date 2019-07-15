package app.egora.Utils;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.shashank.sony.fancytoastlib.FancyToast;

import app.egora.ItemManagement.ItemActivity;
import app.egora.Model.Chat;
import app.egora.Model.Message;

public class FirestoreUtil {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static boolean exists;

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

    public static String getCurrentUserName() {
        return mAuth.getCurrentUser().getDisplayName();
    }

    public static void deleteChat(String chatID, String otherChatID) {
        db.collection("chats").document(otherChatID).update("otherChatID", "none");
        db.collection("chats").document(chatID)
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error FUtil 3: ", e.toString());
                    }
                });
    }

    public static boolean existsOtherChat(String chatID) {
        exists = true;
        db.collection("chats").document(chatID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String id = documentSnapshot.get("otherChatID").toString();
                Log.d("createError", "otherchatID: " + id);
                if(id.equals("none")) {
                    exists = false;
                }
            }
        });
        return exists;
    }

    public static void createDeletetChat(String otherUserID, final String otherChatID, String itemName, final String message,final RecyclerView recyclerView) {
        Log.d("itemINFireUtil: ", itemName);
        Chat newChat = new Chat(otherUserID, FirestoreUtil.getCurrentUserID(), FirestoreUtil.getCurrentUserName(), itemName);
        DocumentReference chatsRef = db.collection("chats").document();
        final String newChatID = chatsRef.getId();
        newChat.setChatID(newChatID);
        Log.d("createError", "new chatID:" + newChatID);
        newChat.setOtherChatID(otherChatID);
        db.collection("chats").document(otherChatID).update("otherChatID", newChatID);
        db.collection("chats").document(newChatID).set(newChat)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirestoreUtil.createAndSendMessage(newChatID, otherChatID, message, recyclerView);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("createError", e.toString());
                }
            });
    }

    public static void send(final String otherUserID,final String chatID,final String otherChatID,final String itemName,final String message,final RecyclerView recyclerView) {
        db.collection("chats").document(chatID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String id = documentSnapshot.get("otherChatID").toString();
                Log.d("createError", "otherchatID: " + id);
                if(id.equals("none")) {
                    FirestoreUtil.createDeletetChat(otherUserID, chatID, itemName, message, recyclerView);
                    Log.d("createError", "im IF");
                }
                else {
                    FirestoreUtil.createAndSendMessage(chatID, otherChatID, message, recyclerView);
                    Log.d("createError", "im ELSE");
                }
            }
        });
        /*if (!FirestoreUtil.existsOtherChat(chatID)) {
            FirestoreUtil.createDeletetChat(otherUserID, chatID, itemName, message, recyclerView);
            Log.d("createError", "im IF");
        }
        else {
            FirestoreUtil.createAndSendMessage(chatID, otherChatID, message, recyclerView);
            Log.d("createError", "im ELSE");
        }*/
    }

    public static void createAndSendMessage(String chatID, String otherChatID, String textMessage, final RecyclerView recyclerView) {
        Message message = new Message(getCurrentUserID(), textMessage);
        //chat A
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
                        Log.e("error FUtil 1a: ", e.toString());
                    }
                });

        db.collection("chats").document(chatID).update(
                "lastActivity", message.getDate(),
                "lastMessageText", message.getText())
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

        db.collection("chats").document(otherChatID).update(
                "lastActivity", message.getDate(),
                "lastMessageText", message.getText())
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

}
