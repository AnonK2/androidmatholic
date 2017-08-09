package com.utilfreedom.brainmath;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.utilfreedom.brainmath.Services.DiffCallback;
import com.utilfreedom.brainmath.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MultiplayerMenuFragment extends Fragment {

    private List<Room> mRooms = new ArrayList<>();
    private int difficultyOption;

    Button logoutBtn;
    EditText roomNameET;
    Button createBtn;
    Button difficultyBtn;
    RecyclerView roomRV;
    LinearLayoutManager mLinearLayoutManager;

    FirebaseAuth mAuth;

    public MultiplayerMenuFragment() {
        // Required empty public constructor
    }

    private void observeRoom() {
        ValueEventListener roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mRooms.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                        System.out.println("DATASNAPSHOT1: " + ds.getValue());
//                        System.out.println("DATASNAPSHOT2: " + ds.child("name").getValue());

                    if ((boolean) ds.child("isStart").getValue()) { continue; }

                    Room room = ds.getValue(Room.class);
                    room.setUID(ds.getKey());
                    mRooms.add(room);
                }

                ((CustomAdapter) roomRV.getAdapter()).updateList(mRooms);// <- update the cell
//                    roomRV.getAdapter().notifyDataSetChanged(); // <- update the cell, THIS METHOD VERY EXPENSIVE
//                System.out.println("mRoom: " + mRooms);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        DatabaseReference ref = Config.DB_BASE.child("rooms");
        ref.addValueEventListener(roomListener);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        observeRoom();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_multiplayer_menu, container, false);



        logoutBtn = (Button) v.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new LogoutBtnPressed());

        roomNameET = (EditText) v.findViewById(R.id.roomNameET);

        difficultyBtn = (Button) v.findViewById(R.id.difficultyBtn);
        difficultyBtn.setOnClickListener(new DifficultyBtnPressed());

        createBtn = (Button) v.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new CreateBtnPressed());

        roomRV = (RecyclerView) v.findViewById(R.id.roomRV);
        roomRV.setHasFixedSize(true);

        // set mRecylerView's LayoutManager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        roomRV.setLayoutManager(mLinearLayoutManager);

        // set mRecyclerView's Adapter
        roomRV.setAdapter(new CustomAdapter(this, mRooms));

        return v;
    }

    private class LogoutBtnPressed implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mAuth.signOut();
            getFragmentManager().popBackStack();
            System.out.println("GL: signOut");
        }
    }

    private class CreateBtnPressed implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if (checkIfRoomNameIsEmpty()) {
                Toast.makeText(getContext(), "Please enter the room name", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            Map<String, Object> playersMap = new HashMap<>();
            playersMap.put("player1UID", mAuth.getCurrentUser().getUid());

            HashMap<String, Object> map = new HashMap<>();
            map.put("name", roomNameET.getText().toString());
            map.put("difficulty", difficultyBtn.getText().toString());
            map.put("isStart", false);
            map.put("players", playersMap);

            final DatabaseReference refWithUID = Config.DB_BASE.child("rooms").push(); // <- same as "childByAutoId"
            refWithUID.updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        System.out.println("GL: " + databaseError);
                        return;
                    }

                    String roomUID = refWithUID.getKey();
//                    Intent gameIntent = new Intent(, GameActivity.class);
//                    gameIntent.putExtra("ROOM_CODE", roomCode);
//                    startActivity(gameIntent);

                    System.out.println("GL: SUCCESSFULLY SAVED " + roomUID);

                    startMRF(roomUID);
                }
            });
        }

        private void startMRF(String roomUID) {
            String player1UID = "";
            if (mAuth.getCurrentUser() != null) {
                player1UID = mAuth.getCurrentUser().getUid();
            }

            Bundle bundle = new Bundle();
            bundle.putBoolean(getString(R.string.IS_HOST), true);
            bundle.putString(getString(R.string.ROOM_UID), roomUID);
            bundle.putString(getString(R.string.PLAYER_1_UID), player1UID);

            MultiplayerRoomFragment MRF = new MultiplayerRoomFragment();
            MRF.setArguments(bundle);

            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .addToBackStack("MultiplayerMenuFragment")
                    .replace(R.id.mainLayout, MRF)
                    .commit();
        }
    }

    private Boolean checkIfRoomNameIsEmpty() {
        if (roomNameET.getText().toString().trim().length() == 0) {
            //isEmpty
            return true;
        }
        return false;
    }

    private class DifficultyBtnPressed implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (difficultyOption == 3) {
                difficultyOption = 0;
            } else {
                difficultyOption += 1;
            }

            switch (difficultyOption) {
                case 0:
                    difficultyBtn.setText("Easy");
                    break;
                case 1:
                    difficultyBtn.setText("Medium");
                    break;
                case 2:
                    difficultyBtn.setText("Hard");
                    break;
                case 3:
                    difficultyBtn.setText("Insane");
                    break;
            }
        }
    }
}

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private List<Room> mRooms = new ArrayList<>();
//    private List<Room> mRoom;
    private MultiplayerMenuFragment mMMF;

    public CustomAdapter(MultiplayerMenuFragment fragment, List<Room> rooms) {
        this.mRooms.addAll(rooms);
        this.mMMF = fragment;
    }

    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // set View
        LinearLayout linearLayout  = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room, parent, false); //<- custom cell(R.layout.room) that we created

        CustomAdapter.CustomViewHolder cvh = new CustomAdapter.CustomViewHolder(linearLayout);
        return cvh;
    }

    @Override
    public void onBindViewHolder(final CustomAdapter.CustomViewHolder holder, int position) {
        Room room = mRooms.get(position);
        String roomUID = room.getUID();
        String roomName = room.getName();
        final String player1UID = (String) room.getPlayers().get("player1UID");
        String difficulty = room.getDifficulty();
        FirebaseUser player2UID = mMMF.mAuth.getCurrentUser();

        holder.roomNameTV.setText(roomName);
        holder.difficultyTV.setText("Difficulty: " + difficulty);

//        System.out.println("HOST: " + player1UID);
        if (player2UID != null) {
            holder.joinBtn.setOnClickListener(new joinBtnPressed(player2UID.getUid(), roomUID));
        }

        //users->child()->name
        DatabaseReference ref = Config.DB_BASE.child("users").child(player1UID).child("username");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String host = dataSnapshot.getValue().toString();
                    holder.hostTV.setText(host);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    public void updateList(List<Room> newRooms) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(this.mRooms, newRooms));

        mRooms.clear();
        mRooms.addAll(newRooms);
        diffResult.dispatchUpdatesTo(this);
    }


    // LIKE onCreate where you set the view or actionListener
    static class CustomViewHolder extends RecyclerView.ViewHolder {
        public Button joinBtn;
        public TextView roomNameTV, hostTV, difficultyTV;


        public CustomViewHolder(View v) {
            super(v);

            this.roomNameTV = (TextView) v.findViewById(R.id.roomNameTV);
            this.hostTV = (TextView) v.findViewById(R.id.hostTV);
            this.difficultyTV = (TextView) v.findViewById(R.id.difficultyTV);
            this.joinBtn = (Button) v.findViewById(R.id.joinBtn);
        }
    }

    private class joinBtnPressed implements View.OnClickListener {
        private String player2UID, roomUID;

        public joinBtnPressed(String player2UID, String roomUID) {
            this.player2UID = player2UID;
            this.roomUID = roomUID;
        }

        @Override
        public void onClick(View view) {
            Map<String, Object> map = new HashMap<>();
            map.put("player2UID", player2UID);

            DatabaseReference ref = Config.DB_BASE.child("rooms").child(roomUID).child("players");
            ref.updateChildren(map);

            MultiplayerRoomFragment MRF = new MultiplayerRoomFragment();
            Bundle bundle = new Bundle();
            bundle.putString(mMMF.getString(R.string.PLAYER_2_UID), player2UID);
            bundle.putBoolean(mMMF.getString(R.string.IS_HOST), false);
            bundle.putString(mMMF.getString(R.string.ROOM_UID), roomUID);

            MRF.setArguments(bundle);

            FragmentManager fm = mMMF.getFragmentManager();
            fm.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_NONE)
            .addToBackStack("MultiplayerMenuFragment")
            .replace(R.id.mainLayout, MRF)
            .commit();
        }
    }
}
