package com.utilfreedom.brainmath;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.utilfreedom.brainmath.model.DifficultyOption;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MultiplayerRoomFragment extends Fragment {
    private String roomUID;
    private String player1UID;
    private String player2UID;
    private String difficulty;
    private Boolean isHost;
    private int playerCount;

    private View player2V;
    private ImageView profileImage2IV;
    private TextView player1TV;
    private TextView player2TV;
    private TextView difficultyTV;
    private Button startBtn;
    private Button cancelBtn;

    private DatabaseReference refCurrentRoom;

    public MultiplayerRoomFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            roomUID = bundle.getString(getString(R.string.ROOM_UID));
            player2UID = bundle.getString(getString(R.string.PLAYER_2_UID));
            isHost = bundle.getBoolean(getString(R.string.IS_HOST));
            if (isHost) {
                player1UID = bundle.getString(getString(R.string.PLAYER_1_UID));
            }
        }

        observeRoomDetail();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_multiplayer_room, container, false);

        player2V = (View) v.findViewById(R.id.player2V);
        profileImage2IV = (ImageView) v.findViewById(R.id.profileImage2IV);
        player2V.setVisibility(View.INVISIBLE);
        profileImage2IV.setVisibility(View.INVISIBLE);

        player1TV = (TextView) v.findViewById(R.id.player1TV);
        player1TV.setText("");
        player2TV = (TextView) v.findViewById(R.id.player2TV);
        player2TV.setText("");
        difficultyTV = (TextView) v.findViewById(R.id.difficultyTV);


        startBtn = (Button) v.findViewById(R.id.startBtn);
        cancelBtn = (Button) v.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new CancelBtnPressed());
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getFragmentManager().popBackStack();
//            }
//        });

        if (isHost) {
            startBtn.setOnClickListener(new StartBtnPressed());
        } else {
            startBtn.setVisibility(View.GONE);
        }

        return v;
    }

    private void observeRoomDetail() {
        ValueEventListener roomDetailVEL = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//                    System.out.println(dataSnapshot.getValue());
                    playerCount = 0;
                    playerNameWithUID(player1TV, (String) dataSnapshot.child("players").child("player1UID").getValue());
                    playerNameWithUID(player2TV, (String) dataSnapshot.child("players").child("player2UID").getValue());
                    difficultyTV.setText((String) dataSnapshot.child("difficulty").getValue());

                    if (!isHost) {
                        if ((boolean) dataSnapshot.child("isStart").getValue()) {
                            startGameOnline();
                        }
                    }

                } else { //this "else" is for player other then player1, if "host" exit from "room" then others will exit too
                    getActivity().getFragmentManager().popBackStack();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //rooms->roomUID->child()
        refCurrentRoom = Config.DB_BASE.child("rooms").child(roomUID);
        refCurrentRoom.addValueEventListener(roomDetailVEL);
    }

    private void playerNameWithUID(final TextView tv, final String UID) {
        final ValueEventListener playerNameVEL = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String playerName = (String) dataSnapshot.child("username").getValue();
                    tv.setText(playerName);
                    System.out.println("UID inside void: " + UID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        if (UID != null) {
            DatabaseReference ref = Config.DB_BASE.child("users")
                    .child(UID);
            ref.addListenerForSingleValueEvent(playerNameVEL);
            playerCount += 1;

            if (playerCount > 1) {
                player2V.setVisibility(View.VISIBLE);
                profileImage2IV.setVisibility(View.VISIBLE);
            } else {
                player2V.setVisibility(View.INVISIBLE);
                profileImage2IV.setVisibility(View.INVISIBLE);
            }
        } else {
            tv.setText("");
        }
    }


    private class StartBtnPressed implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (playerCount < 2) {
                Toast.makeText(getContext(), "Not enough player to start the game!", Toast.LENGTH_SHORT)
                        .show();
            } else {
                startGameOnline();

                Map<String, Object> map = new HashMap<>();
                map.put("isStart", true);

                refCurrentRoom.updateChildren(map);
            }

            System.out.println("Count: " + playerCount);
        }
    }

    private void startGameOnline() {
        DifficultyOption dOpt = new DifficultyOption(difficultyTV.getText().toString().toLowerCase());
        String difficulty = dOpt.getDifficulty();
        int range = dOpt.getRange();
        double duration = dOpt.getDuration();
        System.out.println(difficulty + " XXX " + range + " " + duration);

        Bundle bundle = new Bundle();
        //main bundle -> "online" or "offline" game
        bundle.putBoolean(getString(R.string.IS_ONLINE), true);
        bundle.putBoolean(getString(R.string.IS_HOST), isHost);
        bundle.putString(getString(R.string.ROOM_UID), roomUID);
        //additional bundle
        bundle.putString(getString(R.string.game_setup_difficulty), difficulty);
        bundle.putInt(getString(R.string.game_setup_range), range);
        bundle.putDouble(getString(R.string.game_setup_duration), duration);

        StartGameFragment SGF = new StartGameFragment();
        SGF.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .replace(R.id.mainLayout, SGF)
                .addToBackStack(null)
                .commit();
    }

    private void popBackToMenu() {
        getFragmentManager().popBackStack();
    }

    private class CancelBtnPressed implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (isHost) {
                refCurrentRoom.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println(databaseError);
                            return;
                        }

                        popBackToMenu();
                    }
                });
            } else {
                refCurrentRoom.child("players").child("player2UID").removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println(databaseError);
                            return;
                        }

                        popBackToMenu();
                    }
                });
            }
        }
    }
}
