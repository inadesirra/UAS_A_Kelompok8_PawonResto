package com.example.pawonresto.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawonresto.EditActivity;
import com.example.pawonresto.LoginActivity;
import com.example.pawonresto.Preferences.UserPreferences;
import com.example.pawonresto.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    public static final String TAG = "TAG";
    ImageView profilePicture;
    TextView profileName, profileEmail;
    Button logoutButton, gotoEditProfile;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    String userID;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();

        profilePicture = view.findViewById(R.id.profilePicture);
        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        logoutButton = view.findViewById(R.id.logoutButton);
        gotoEditProfile = view.findViewById(R.id.gotoEditProfile);

        DocumentReference documentReference = firebaseFirestore.collection("users")
                .document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (value.getString("fullname") != null &&
                        value.getString("email") != null) {
                    StorageReference profileRef = storageReference.child("users/" + userID +
                            "/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(profilePicture);
                        }
                    });

                    profileName.setText(value.getString("fullname"));
                    profileEmail.setText(value.getString("email"));
                } else {
                    Log.d(TAG, "onEvent: Document Does Not Exists");
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeFragment.this.getContext(), LoginActivity.class));
            }
        });

        gotoEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeFragment.this.getContext(), EditActivity.class);
                intent.putExtra("fullname", profileName.getText().toString());
                intent.putExtra("email", profileEmail.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}