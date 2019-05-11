package app.egora;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import app.egora.Model.UserInformation;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    ListView listView;
    FirebaseListAdapter adapter;


    public ContactsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        listView = view.findViewById(R.id.contacts_listView);
        Query query = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseListOptions<UserInformation> options = new FirebaseListOptions.Builder<UserInformation>()
                .setLayout(R.layout.user_information)
                .setQuery(query, UserInformation.class)
                .build();

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
                TextView userName = v.findViewById(R.id.user_information_name);
                TextView address = v.findViewById(R.id.user_information_address);

                UserInformation userInformation = (UserInformation) model;
                userName.setText(userInformation.getFullName());
                address.setText(userInformation.getAddress());

            }
        };
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
