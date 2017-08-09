package com.utilfreedom.brainmath;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.utilfreedom.brainmath.model.DifficultyOption;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartGameLevelFragment extends Fragment {
    private Button easyBtn;
    private Button mediumBtn;
    private Button hardBtn;
    private Button insaneBtn;
    private Button backBtn;

    public StartGameLevelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start_game_level, container, false);

        easyBtn = (Button) v.findViewById(R.id.easyBtn);
        mediumBtn = (Button) v.findViewById(R.id.mediumBtn);
        hardBtn = (Button) v.findViewById(R.id.hardBtn);
        insaneBtn = (Button) v.findViewById(R.id.insaneBtn);
        backBtn = (Button) v.findViewById(R.id.backBtn);

        easyBtn.setOnClickListener(new LevelBtnClicked(DifficultyOptionAs("easy")));
        mediumBtn.setOnClickListener(new LevelBtnClicked(DifficultyOptionAs("medium")));
        hardBtn.setOnClickListener(new LevelBtnClicked(DifficultyOptionAs("hard")));
        insaneBtn.setOnClickListener(new LevelBtnClicked(DifficultyOptionAs("insane")));
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        return v;
    }

    private DifficultyOption DifficultyOptionAs(String str) {
        return new DifficultyOption(str);
    }

    private class LevelBtnClicked implements View.OnClickListener {
        private double _duration;
        private int _range;
        private String _difficulty;

        private LevelBtnClicked(DifficultyOption difficultyOption) {
            _duration = difficultyOption.getDuration();
            _range = difficultyOption.getRange();
            _difficulty = difficultyOption.getDifficulty();
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            //main bundle -> "online" or "offline" game
            bundle.putBoolean(getString(R.string.IS_ONLINE), false);
            //additional bundle
            bundle.putDouble(getString(R.string.game_setup_duration), _duration);
            bundle.putInt(getString(R.string.game_setup_range), _range);
            bundle.putString(getString(R.string.game_setup_difficulty), _difficulty);

            StartGameFragment SGF = new StartGameFragment();
            SGF.setArguments(bundle);

            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
              .setTransition(FragmentTransaction.TRANSIT_NONE)
              .replace(R.id.mainLayout, SGF)
              .addToBackStack(null)
              .commit();
        }
    }

}
