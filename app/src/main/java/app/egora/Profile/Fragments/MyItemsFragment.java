package app.egora.Profile.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import app.egora.R;
import app.egora.Utils.MyItemAdapter;

public class MyItemsFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private DocumentReference userRef;
    private MyItemAdapter adapter;
    private UserInformation currentUser;
    private RecyclerView recyclerView;


    private TextView testView;

    public MyItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_my_items, container, false);
        recyclerView = view.findViewById(R.id.my_items_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        setupFirebaseModuls();
        return view;

    }

    private void setupFirebaseModuls() {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        currentUser = new UserInformation();

        if(mAuth.getCurrentUser() != null) {
            userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(documentSnapshot != null){

                                currentUser = documentSnapshot.toObject(UserInformation.class);

                                //Updating View and adding RecyclerViewAdapter
                                Query query = db.collection("items").whereEqualTo("ownerId", mAuth.getUid());

                                FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Item>()
                                        .setQuery(query, Item.class)
                                        .build();

                                adapter = new MyItemAdapter(options);
                                recyclerView.setAdapter(adapter);
                                adapter.startListening();
                            }

                        }
                    });
                }
            });
        }

    }

}
