package com.utilfreedom.brainmath;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.utilfreedom.brainmath.controller.GameOverDialog;

public class GameOverFragment extends Fragment {
    private static final int DELAY_DURATION = 0;
    private int _newScore;
    private String _difficulty;
    private boolean _isOnline;


    public GameOverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game_over_blank, container, false);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            _newScore = bundle.getInt(getString(R.string.new_score), 0); // "0" is defaultValue.
//            System.out.println("NEW SCORE: " + _newScore);
            _difficulty = bundle.getString(getString(R.string.game_setup_difficulty), "easy");
            _isOnline = bundle.getBoolean(getString(R.string.IS_ONLINE), false);
        }

        GameOverDialog GOD = new GameOverDialog(getContext(), R.style.GameOverDialog, GameOverFragment.this,  _newScore, _difficulty, _isOnline);
        GOD.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // so that there is no need to "clipToBounds" for rounded corner and
        // it will clear the shadow of box dialog.
        GOD.setCanceledOnTouchOutside(false);
        GOD.setCancelable(false); // <- disable "back" button
        GOD.show();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, DELAY_DURATION);

        return v;
    }
}
