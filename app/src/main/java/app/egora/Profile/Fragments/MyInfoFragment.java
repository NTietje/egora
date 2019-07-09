package app.egora.Profile.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import app.egora.Model.Item;
import app.egora.Model.UserInformation;
import app.egora.Profile.ChangeInformationActivity;
import app.egora.Profile.ProfileActivity;
import app.egora.R;
import app.egora.Utils.MyItemAdapter;

public class MyInfoFragment extends Fragment {


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userRef;
    private DocumentReference itemRef;
    private DocumentReference communityRef;
    private CollectionReference itemCol;
    private UserInformation currentUser;

private AlertDialog alertDialog;
    private TextView textViewName;
    private TextView textViewAddress;
    private TextView textViewEmail;
    private Button buttonChangeInfo;
    private Button buttonChangeCommunity;

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
        textViewName = view.findViewById(R.id.my_info_name);
        textViewAddress = view.findViewById(R.id.my_info_address);
        textViewEmail = view.findViewById(R.id.my_info_email);

        buttonChangeInfo = view.findViewById(R.id.my_info_button_change_info);
        buttonChangeCommunity = view.findViewById(R.id.my_info_button_change_community);

        settingView();




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
                                                        FancyToast.makeText(view.getContext(),"You successfully left your community!", FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();

                                                        Intent intent = new Intent(getActivity().getBaseContext(), CommunitiesActivity.class);
                                                        startActivity(intent);


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
                builder.setMessage("Are you sure you want to change your community?")
                        .setNegativeButton("No", dialogClickListener).setPositiveButton("Yes", dialogClickListener);


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
                        currentUser = documentSnapshot.toObject(UserInformation.class);

                        communityRef = db.collection("communities").document(currentUser.getCommunityName());
                        String userName = "" + currentUser.getFirstName() +" " +currentUser.getLastName();
                        String userAddress = "" + currentUser.getStreetName() +" " + currentUser.getHouseNumber() + " , " + currentUser.getCityName();
                        String userEmail = "" + currentUser.getEmail();

                        textViewName.setText(userName);
                        textViewAddress.setText(userAddress);
                        textViewEmail.setText(userEmail);





                        Log.d("User starting: " , currentUser.getCommunityName());
                        String userId = mAuth.getUid().toString();


                    }
                });
            }
        });
    }



}