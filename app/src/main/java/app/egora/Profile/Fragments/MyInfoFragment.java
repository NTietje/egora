package app.egora.Profile.Fragments;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

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
    private UserInformation currentUser;

    private TextView textViewName;
    private TextView textViewAddress;
    private TextView textViewEmail;
    private Button buttonChangeInfo;
    private Button buttonChangeCommunity;



    public MyInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        View view = inflater.inflate(R.layout.fragment_my_info, container, false);
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
