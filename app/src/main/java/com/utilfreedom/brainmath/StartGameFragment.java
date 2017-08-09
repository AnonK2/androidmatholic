package com.utilfreedom.brainmath;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.utilfreedom.brainmath.model.Operation;

import java.util.HashMap;

import static com.utilfreedom.brainmath.R.id.score;

//import android.widget.EditText;

public class StartGameFragment extends Fragment {
    private TextView indicator;
//    private TextView gameOver;
//    private EditText second;
    private Button answer1Btn;
    private Button answer2Btn;
    private TextView number1;
    private TextView number2;
    private TextView equals;
    private TextView number3;
    private TextView operationSymbol;
    private TextView scoreTV;

    private double _duration;
    private int _range;
    private String _difficulty;

    private boolean _answer;
    private int _score;
    private boolean _isOnline;
    private boolean _isHost;
    private String _roomUID;
    private String playerUID;
    private int currentScore;
    private boolean waiting;
    private boolean visibleGenerated;
    private  boolean isFinalGame;

    Operation mOperation;

    FirebaseAuth mAuth;
    DatabaseReference refGamePlayers;

    public StartGameFragment() {
        mOperation = new Operation(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _isOnline = bundle.getBoolean(getString(R.string.IS_ONLINE), false);
            _roomUID = bundle.getString(getString(R.string.ROOM_UID), "");
            _duration = bundle.getDouble(getString(R.string.game_setup_duration), 10.0);
            _range = bundle.getInt(getString(R.string.game_setup_range), 10);
            _difficulty = bundle.getString(getString(R.string.game_setup_difficulty), "easy");

            if (_isOnline) {
                _isHost = bundle.getBoolean(getString(R.string.IS_HOST));
                System.out.println(_isHost);
            }
        }

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start_game, container, false);

        // start game
        _score = 0;


        indicator = (TextView) v.findViewById(R.id.indicator);
//        gameOver = (TextView) v.findViewById(R.id.gameOver);
        answer1Btn = (Button) v.findViewById(R.id.answer1Btn);
        answer2Btn = (Button) v.findViewById(R.id.answer2Btn);
        number1 = (TextView) v.findViewById(R.id.number1);
        number2 = (TextView) v.findViewById(R.id.number2);
        equals = (TextView) v.findViewById(R.id.equals);
        number3 = (TextView) v.findViewById(R.id.number3);
        operationSymbol = (TextView) v.findViewById(R.id.operationSymbol);
        scoreTV = (TextView) v.findViewById(score);
        scoreTV.setText(String.valueOf(_score));

        answer1Btn.setOnClickListener(new answer1BtnClickListener());
        answer2Btn.setOnClickListener(new answer2BtnClickListener());


        if (_isOnline) {
            if (mAuth.getCurrentUser() != null) {
                playerUID = mAuth.getCurrentUser().getUid();
            }

            refGamePlayers = Config.DB_BASE.child("games")
                                        .child(_roomUID)
                                        .child("players");
            checkOnlineState(playerUID, 0, false, false); //the first time(default)
            checkOnlineWithFirstRoundPassed(false);
        } else {
            checkOnlineWithFirstRoundPassed(false);
        }

        return v;
    }

    private void allPlayersIsDone() {
        checkOnlineState(playerUID, currentScore, false, false); //<- ALL PLAYERS IS DONE SO MAKE THE STATE BACK TO "isDone = false"

        //check host or not
        if (_isHost) {
            mOperation.setNumberAndOperation(_isOnline, _roomUID, _score, _range);
        } else {
            DatabaseReference refGameVars = Config.DB_BASE.child("games")
                    .child(_roomUID);
            refGameVars.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String operation = (String) dataSnapshot.child("operation").getValue();
                        String num1Text = (String) dataSnapshot.child("num1Text").getValue();
                        String num2Text = (String) dataSnapshot.child("num2Text").getValue();
                        String num3Text = (String) dataSnapshot.child("num3Text").getValue();
                        String answer1Text = (String) dataSnapshot.child("answer1Text").getValue();
                        String answer2Text = (String) dataSnapshot.child("answer2Text").getValue();
                        boolean answer = (boolean) dataSnapshot.child("answer").getValue();

                        setupUIValueWithAnswer(operation, num1Text, num2Text, num3Text, answer1Text, answer2Text, answer);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void setupUIWithIsWaiting(int visibility) {

        operationSymbol.setVisibility(visibility);
        number1.setVisibility(visibility);
        number2.setVisibility(visibility);
        equals.setVisibility(visibility);
        number3.setVisibility(visibility);
        answer1Btn.setVisibility(visibility);
        answer2Btn.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            indicatorAnimation();
        }
    }

    private void checkAllPlayersIsDone() {
        ValueEventListener checkAllPlayersIsDoneVEL = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot playersSnapShot) {
                if (playersSnapShot.child(playerUID).child("isDone") != null) {
                    boolean myIsDone = (boolean) playersSnapShot.child(playerUID).child("isDone").getValue();
                    boolean othersIsDone = true;
                    boolean othersIsEnd = false;

                    //check for other's isDone
                    for (DataSnapshot playerSnapShot : playersSnapShot.getChildren()) {
                        if (mAuth.getCurrentUser() != null) {
                            boolean me = mAuth.getCurrentUser().getUid().matches(playerSnapShot.getKey());
                            System.out.println(me);
                            if (!me) { //check for other users
                                boolean isDone = (boolean) playerSnapShot.child("isDone").getValue();
                                if (!isDone) {
                                    othersIsDone = false;
                                    break; //break from for-loop
                                }
                            }
                        }
                    }

                    //check for other's isEnd
                    for (DataSnapshot playerSnapShot : playersSnapShot.getChildren()) {
                        if (mAuth.getCurrentUser() != null) {
                            boolean me = mAuth.getCurrentUser().getUid().matches(playerSnapShot.getKey());
                            System.out.println(me);
                            if (!me) { //check for other users
                                boolean isEnd = (boolean) playerSnapShot.child("isEnd").getValue();
                                if (isEnd) {
                                    othersIsEnd = true;
                                }
                            }
                        }
                    }

                    //INVISIBLE
                    if (myIsDone && !othersIsDone) {
                        System.out.println("myIsDone: " + "INVISIBLE");
                        setupUIWithIsWaiting(View.INVISIBLE);
                        waiting = true; // <- so that at the end of the animation's indicator the player will not lose
                        visibleGenerated = false;
                    } else { //VISIBLE
                        System.out.println("visibleGenerated: " + visibleGenerated);
                        if (!visibleGenerated) {
                            System.out.println("myIsDone: " + "VISIBLE");
                            setupUIWithIsWaiting(View.VISIBLE);
                            allPlayersIsDone();
                            waiting = false;
                            visibleGenerated = true;
                            if (othersIsEnd) {
                                isFinalGame = true;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        refGamePlayers.addValueEventListener(checkAllPlayersIsDoneVEL);
    }

    private void checkOnlineState(String player, final int score, final boolean isDone, final boolean isEnd) {
        HashMap<String, Object> map = new HashMap<String, Object>() {{ //anonymous implementation of HashMap's method
            put("score", score);
            put("isDone", isDone);
            put("isEnd", isEnd);
        }};

        refGamePlayers.child(playerUID).updateChildren(map);
    }

    private void checkOnlineWithFirstRoundPassed(boolean firstRoundPassed) {
        if (_isOnline) {

            //check firstRoundPassed?
            if (firstRoundPassed) {
                currentScore += 1; //currentScore increase from here
                checkOnlineState(playerUID, currentScore, true, false); //need to update "isDone" to "true" so it can be "invisible" in "checkAllPlayersIsDone()"
                checkAllPlayersIsDone();
            } else {
                System.out.print("FIRST TIME");
                allPlayersIsDone();
            }

        } else {
            mOperation.setNumberAndOperation(false, _roomUID, _score, _range);
        }
    }

    public void setupUIValue(String os, String num1, String num2, String num3, String ans1, String ans2) {
        operationSymbol.setText(os);
        number1.setText(num1);
        number2.setText(num2);
        number3.setText(num3);
        answer1Btn.setText(ans1);
        answer2Btn.setText(ans2);
    }

    public void setupUIValueWithAnswer(String os, String num1, String num2, String num3, String ans1, String ans2, boolean answer) {
        setupUIValue(os, num1, num2, num3, ans1, ans2);
        _answer = answer;
    }

    private class answer1BtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (_answer) {
                if (isFinalGame) {
                    currentScore += 1;
                    _score = currentScore;
                    checkOnlineState(playerUID, currentScore, true, true);
                    gameOver();
                } else {
                    stillPlaying();
                }
            } else {
                if (_isOnline) {
                    checkOnlineState(playerUID, currentScore, true, true);
                }
                gameOver();
            }
        }
    }

    private class answer2BtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!_answer) {
                if (isFinalGame) {
                    currentScore += 1;
                    _score = currentScore;
                    checkOnlineState(playerUID, currentScore, true, true);
                    gameOver();
                } else {
                    stillPlaying();
                }
            } else {
                if (_isOnline) {
                    checkOnlineState(playerUID, currentScore, true, true);
                }
                gameOver();
            }
        }
    }
    // FRAGMENT VERSION
    private void gameOver() {
        Bundle bundle = new Bundle();
        GameOverFragment GOF = new GameOverFragment();
        // use "BUNDLE" to pass data from fragment NOT "INTENT".
        bundle.putInt(getString(R.string.new_score), _score);
        bundle.putString(getString(R.string.game_setup_difficulty), _difficulty);
        bundle.putBoolean(getString(R.string.IS_ONLINE), _isOnline);

        GOF.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
          .setTransition(FragmentTransaction.TRANSIT_NONE)
//          .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
          .addToBackStack(null)
          .replace(R.id.mainLayout, GOF)
//                .show(GOF)
          .commit();

//        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    // INTENT VERSION
//    private void gameOver() {
//        Intent mIntent = new Intent(getActivity(), GameOverActivity.class);
//        mIntent.putExtra(getString(R.string.new_score), _score);
//        mIntent.putExtra(getString(R.string.game_setup_difficulty), _difficulty);
//        getActivity().startActivity(mIntent);
//    }

    private void stillPlaying() {
        System.out.println(currentScore);
        setupUIValue("", "", "", "", "", "");
        //answer is true
        checkOnlineWithFirstRoundPassed(true);
        indicatorAnimation();

        // increase the score.
        new IncreaseScore().execute();
    }

    private void indicatorAnimation() {
        // set the animation.
        // this.getActivity() -> The activity is a context (since Activity extends Context).
        Animation mAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.indicator_animation);
        mAnimation.setDuration((long) (_duration * 1000));
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                if (_isOnline) {
                    if (!waiting) {
                        checkOnlineState(playerUID, currentScore, true, true); //so that the other player can answer the next question, the game will be draw or win.
                        gameOver();
                    } else {
                        waiting = false;
                    }
                } else {
                    gameOver();
                }
            }
        });
        // attach the animation and start the animation.
        indicator.startAnimation(mAnimation);
    }

    private class IncreaseScore extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            _score++;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            scoreTV.setText(String.valueOf(_score));
        }
    }
}
