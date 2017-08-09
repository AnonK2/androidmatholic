package com.utilfreedom.brainmath;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.utilfreedom.brainmath.controller.ExitDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment {
    private Button startGameBtn;
    private Button multiplayerBtn;
    private Button highScoreBtn;
    private Button infoBtn;
    private Button exitBtn;

    public MainMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        startGameBtn = (Button) v.findViewById(R.id.startGameBtn);
        multiplayerBtn = (Button) v.findViewById(R.id.multiplayerBtn);
        highScoreBtn = (Button) v.findViewById(R.id.highscoreBtn);
        infoBtn = (Button) v.findViewById(R.id.infoBtn);
        exitBtn = (Button) v.findViewById(R.id.exitBtn);

        startGameBtn.setOnClickListener(new MainMenuBtnClicked(0, new StartGameLevelFragment()));
        multiplayerBtn.setOnClickListener(new MainMenuBtnClicked(0, new MultiplayerFragment()));
        highScoreBtn.setOnClickListener(new MainMenuBtnClicked(0, new HighScoreFragment()));
        infoBtn.setOnClickListener(new MainMenuBtnClicked(0, new InfoFragment()));
        exitBtn.setOnClickListener(new MainMenuBtnClicked(1, null));

        return v;
    }

    private class MainMenuBtnClicked implements View.OnClickListener {
        private Fragment targetFragment;
        private int _option;

        FragmentManager fm = getFragmentManager();

        private MainMenuBtnClicked(int option, Fragment f) {
            _option = option;
            targetFragment = f;
        }

        @Override
        public void onClick(View view) {
            if (_option == 0) {
                fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .replace(R.id.mainLayout, targetFragment)
                .addToBackStack("MainMenuFragment")
                .commit();
            } else if (_option == 1) {
                ExitDialog mExitDialog = new ExitDialog(getActivity());
                mExitDialog.showDialog("Are you sure?");
                return;
            }
        }
    }
}
