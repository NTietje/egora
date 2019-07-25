package app.egora.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.egora.Login.LoginActivity;
import app.egora.Model.UserInformation;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;

    private Context mContext;



    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    /**
     * Retrieves the account settings for teh user currently logged in
     * Database: user_acount_Settings node
     * @param dataSnapshot
     * @return
     */
    public UserInformation getCurrentUser(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase.");


        UserInformation currentUser = new UserInformation();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            // user_account_settings node
            if(ds.getKey().equals("users")){
                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds);

                try{

                    currentUser.setFirstName(
                            ds.child(userID)
                                .getValue(UserInformation.class)
                                .getFirstName()

                    );
                    Log.d("FirstName: " , currentUser.getFirstName());
                    currentUser.setLastName(
                            ds.child(userID)
                                    .getValue(UserInformation.class)
                                    .getLastName()
                    );

                    currentUser.setStreetName(
                            ds.child(userID)
                                    .getValue(UserInformation.class)
                                    .getStreetName()
                    );

                    currentUser.setHouseNumber(
                            ds.child(userID)
                                    .getValue(UserInformation.class)
                                    .getHouseNumber()
                    );

                    currentUser.setCityName(
                            ds.child(userID)
                                    .getValue(UserInformation.class)
                                    .getCityName()
                    );

                    currentUser.setEmail(
                            ds.child(userID)
                                    .getValue(UserInformation.class)
                                    .getEmail()
                    );

                    currentUser.setUserID(
                            ds.child(userID)
                                    .getValue(UserInformation.class)
                                    .getUserID()
                    );

                    currentUser.setCommunityName(
                            ds.child(userID)
                                    .getValue(UserInformation.class)
                                    .getCommunityName()
                    );


                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information: " + currentUser.toString());
                }catch (NullPointerException e){
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage() );
                }

            }
        }
        return currentUser;

    }

}
