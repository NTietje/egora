package app.egora.Profile.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.IntentCompat;
import androidx.fragment.app.Fragment;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import app.egora.Communities.CommunitiesActivity;
import app.egora.Login.LoginActivity;
import app.egora.Model.Community;
import app.egora.Model.Item;
import app.egora.Model.UserInformation;
import app.egora.Profile.ChangeInformationActivity;
import app.egora.Profile.ProfileActivity;
import app.egora.R;
import app.egora.Utils.FirestoreUtil;
import app.egora.Utils.MyItemAdapter;

public class MyInfoFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userRef;
    private DocumentReference itemRef;
    private DocumentReference communityRef;
    private CollectionReference itemCol;
    private UserInformation currentUser;
    private Community community;

    private AlertDialog alertDialog;

    private TextView textViewFirstname;
    private TextView textViewLastname;
    private TextView textViewAddress;
    private TextView textViewEmail;
    private TextView textViewCommunityName;
    private TextView textViewCommunityKey;
    private TextView textViewTableCommunityKey;
    private ImageButton buttonChangeInfo;
    private ImageButton buttonChangeCommunity;
    private Button buttonLogout;

    private List<Item> itemList;

    public MyInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        itemList= new ArrayList<>();

        final View view = inflater.inflate(R.layout.fragment_my_info, container, false);

        textViewFirstname = view.findViewById(R.id.my_info_firstname);
        textViewLastname = view.findViewById(R.id.my_info_lastname);
        textViewAddress = view.findViewById(R.id.my_info_address);
        textViewEmail = view.findViewById(R.id.my_info_email);
        textViewCommunityName = view.findViewById(R.id.my_info_community);
        textViewCommunityKey = view.findViewById(R.id.my_info_commmunity_key);
        textViewTableCommunityKey = view.findViewById(R.id.my_info_table_community_key);

        buttonChangeInfo = view.findViewById(R.id.my_info_button_change_info);
        buttonChangeCommunity = view.findViewById(R.id.my_info_button_change_community);
        buttonLogout = view.findViewById(R.id.button_logout);

        settingView();

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                FirestoreUtil.signOut();
                getActivity().finish();

            }
        });

        buttonChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangeInformationActivity.class);
                startActivity(intent);
            }
        });

        buttonChangeCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating AlertDialog-Options
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                //Searching for all owned Items
                                db.collection("items").whereEqualTo("ownerId" , mAuth.getUid()).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                WriteBatch batch = db.batch();

                                                //Checking if the query is empty
                                                if(!queryDocumentSnapshots.isEmpty()){
                                                    //Adding items to Arraylist
                                                    for (DocumentSnapshot snapshot:queryDocumentSnapshots){

                                                        itemList.add(snapshot.toObject(Item.class));
                                                    }
                                                }

                                                //Adding items from Arraylist to the batch object
                                                for(Item item: itemList){
                                                    itemRef = db.collection("items").document(item.getItemId());
                                                    batch.update(itemRef, "communityName" , "changing" );
                                                }
                                                //Updating UserInfo
                                                batch.update(userRef, "communityName" , "changing");
                                                batch.update(communityRef, "userIDs" , FieldValue.arrayRemove(mAuth.getUid()));

                                                //Commiting the whole batch
                                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FancyToast.makeText(view.getContext(),"Du hast erfolgreiche deine Community verlassen", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                                        if(getActivity()!= null){
                                                            Context context = getActivity().getBaseContext();
                                                            if(context != null){
                                                                Intent intent = new Intent(context, CommunitiesActivity.class);
                                                                startActivity(intent);
                                                                getActivity().finish();
                                                            }
                                                        }

                                                    }
                                                });
                                            }
                                        });

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }

                    }

                };

                //Initiating the Dialog

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Bist du sicher, dass du die Community '"+ currentUser.getCommunityName() + "' verlassen willst?")
                        .setNegativeButton("Nein", dialogClickListener).setPositiveButton("Ja", dialogClickListener);


                alertDialog = builder.create();

                if (alertDialog.isShowing() && alertDialog != null){
                    alertDialog.dismiss();
                }
                alertDialog.show();


            }
        });

        //return inflater.inflate(R.layout.fragment_chats, container, false);
        return view;
    }

    private void settingView(){
        userRef = db.collection("users").document(mAuth.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot != null){
                            currentUser = documentSnapshot.toObject(UserInformation.class);


                            communityRef = db.collection("communities").document(currentUser.getCommunityName());
                            String userAddress = "" + currentUser.getStreetName() +" " + currentUser.getHouseNumber() + ", " + currentUser.getCityName();
                            String userEmail = "" + currentUser.getEmail();

                            textViewFirstname.setText(currentUser.getFirstName());
                            textViewLastname.setText(currentUser.getLastName());
                            textViewAddress.setText(userAddress);
                            textViewEmail.setText(userEmail);
                            communityRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.toObject(Community.class) != null) {

                                        community = documentSnapshot.toObject(Community.class);
                                        textViewCommunityName.setText(community.getName());

                                        textViewCommunityKey.setText(R.string.none);
                                        if(!community.getKey().isEmpty()){
                                            textViewCommunityKey.setText(community.getKey());
                                        }
                                    }
                                }
                            });
                        }

                    }
                });
            }
        });
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        FirestoreUtil.addAuthListener(mAuth, getActivity());
    }

    @Override
    public void onStop() {
        FirestoreUtil.removeAuthListener();
        super.onStop();
    }
    */
}
