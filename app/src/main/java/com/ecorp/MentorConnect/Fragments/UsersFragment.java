package com.ecorp.MentorConnect.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecorp.MentorConnect.RegisterActivity_Mentee;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ecorp.MentorConnect.Adapter.OnItemClick;
import com.ecorp.MentorConnect.Adapter.UserAdapter;
import com.ecorp.MentorConnect.Model.User;
import com.ecorp.MentorConnect.R;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;

    Typeface MR, MRR;
    FrameLayout frameLayout;
    TextView es_descp, es_title;

    private UserAdapter userAdapter;
    private List<User> mUsers;
    static OnItemClick onItemClick;

    EditText search_users;

    public static UsersFragment newInstance(OnItemClick click) {
        onItemClick = click;
        Bundle args = new Bundle();

        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);


        MRR = Typeface.createFromAsset(getContext().getAssets(), "fonts/myriadregular.ttf");
        MR = Typeface.createFromAsset(getContext().getAssets(), "fonts/myriad.ttf");

        recyclerView = view.findViewById(R.id.recycler_view);
        frameLayout = view.findViewById(R.id.es_layout);
        es_descp = view.findViewById(R.id.es_descp);
        es_title = view.findViewById(R.id.es_title);

        es_descp.setTypeface(MR);
        es_title.setTypeface(MRR);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mUsers = new ArrayList<>();

        readUsers();

        search_users = view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //edited
                String stxt=charSequence.toString().toLowerCase();
                stxt="m."+stxt;
                searchUsers(stxt);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentusermail1=fuser.getEmail();
        final String mentorname1=fuser.getDisplayName();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Mentor
                if(currentusermail1.matches("[a-zA-Z0-9]+@[a-z]+.sastra.edu")) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        assert user != null;
                        assert fuser != null;
                        //edited 2 lines
                        String usr = user.getUsername();
                        if (usr.matches("[M/m][.][A-Za-z]+")) {
                            if (!user.getId().equals(fuser.getUid())) {
                                mUsers.add(user);
                            }
                        }
                    }
                    userAdapter = new UserAdapter(getContext(), onItemClick, mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                }

                //Mentee
                else{
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        assert user != null;
                        assert fuser != null;
                        //edited 2 lines
                        String usr = user.getUsername();
                        if (usr.equals(mentorname1)) {
                            if (!user.getId().equals(fuser.getUid())) {
                                mUsers.add(user);
                            }
                        }
                    }
                    userAdapter = new UserAdapter(getContext(), onItemClick, mUsers, false);
                    recyclerView.setAdapter(userAdapter);







                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentusermail=firebaseUser.getEmail();
        final String mentorname=firebaseUser.getDisplayName();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                //Mentor

                if(currentusermail.matches("[a-zA-Z0-9]+@[a-z]+.sastra.edu")) {
                    if (search_users.getText().toString().equals("")) {
                        mUsers.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            //Next 2 lines edited
                            String usr = user.getUsername();
                            if (usr.matches("[M/m][.][A-Za-z]+")) {
                                if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                                    mUsers.add(user);
                                   //Log.d("whoami", "Value" + mentorname);
                                }
                            }
                        }

                        if (mUsers.size() == 0) {
                            frameLayout.setVisibility(View.VISIBLE);
                        } else {
                            frameLayout.setVisibility(View.GONE);
                        }

                        userAdapter = new UserAdapter(getContext(), onItemClick, mUsers, false);
                        recyclerView.setAdapter(userAdapter);
                    }
                }

                //Mentee
                else {



                    if (search_users.getText().toString().equals("")) {
                        mUsers.clear();




                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);

                            //Next 2 lines edited
                            String usr1 = user.getUsername();
                            if (mentorname.equals(usr1)) {
                                if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                                    mUsers.add(user);
                                   // Log.d("whoami", "Value" + mentorname);
                                }
                            }
                        }

                        if (mUsers.size() == 0) {
                            frameLayout.setVisibility(View.VISIBLE);
                        } else {
                            frameLayout.setVisibility(View.GONE);
                        }

                        userAdapter = new UserAdapter(getContext(), onItemClick, mUsers, false);
                        recyclerView.setAdapter(userAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
