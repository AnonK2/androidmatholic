package com.utilfreedom.brainmath.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.utilfreedom.brainmath.GameOverFragment;
import com.utilfreedom.brainmath.R;
import com.utilfreedom.brainmath.model.DBHelper;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by kennywang on 6/23/17.
 */

public class GameOverDialog extends Dialog {
    private TextView newScoreTV;
    private TextView bestScoreTV;
    private TextView newHighScoreTV;
    private Button saveBtn;
    private Button retryBtn;
    private Button mainMenuBtn;
    private EditText nameET;

    private int _newScore;
    private String _difficulty;
    private DBHelper mydb ;

    private Context mContext;
    private GameOverFragment mGameOverFragment;
    private boolean _isOnline;

    public GameOverDialog(@NonNull Context context, @StyleRes int themeResId, GameOverFragment gof, int score, String difficulty, boolean isOnline) {
        super(context, themeResId);

        mContext = context;
        mGameOverFragment = gof;
        _newScore = score;
        _difficulty = difficulty;
        _isOnline = isOnline;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_dialog_game_over);

        mydb = new DBHelper(mContext);

        newHighScoreTV = (TextView) findViewById(R.id.newHighScoreTV);
        newHighScoreTV.setVisibility(View.GONE);
        newScoreTV = (TextView) findViewById(R.id.newScoreTV);
        newScoreTV.setText(String.valueOf(_newScore));
        bestScoreTV = (TextView) findViewById(R.id.bestScoreTV);
        nameET = (EditText) findViewById(R.id.nameET);
        nameET.setVisibility(View.GONE); // GONE -> because first we haven't check if the score is passed the "top ten".
        saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setVisibility(View.GONE); // GONE -> because first we haven't check if the score is passed the "top ten".

        retryBtn = (Button) findViewById(R.id.retryBtn);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameOverDialog.this.dismiss();
                if (_isOnline) {
                    mGameOverFragment.getFragmentManager().popBackStack("MultiplayerMenuFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    mGameOverFragment.getFragmentManager().popBackStack();
                }
            }
        });

        mainMenuBtn = (Button) findViewById(R.id.mainMenuBtn);
        mainMenuBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                System.out.println("MAIN MENU");
                GameOverDialog.this.dismiss();
                mGameOverFragment.getFragmentManager().popBackStack("MainMenuFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });


        setupBestScore();
    }

    private void setupBestScore() {System.out.println("STRING ARRAY: ");
        // BEST SCORE
        Cursor mCursor1 = mydb.getBestScore(_difficulty); // mCursor return column "score".
        int bestScore = 0;
        // if "mCursor.moveToLast()" return "true", then it means there is a "BEST SCORE".
        if (mCursor1.moveToLast()) { // the first row is the name of the row which is "score", REMEMBER: it is cursor, so must move it.
            bestScore = mCursor1.getInt(0); // get the "index 0" value INTEGER from "score" COLUMN.)
        }
//        System.out.println("BEST SCORE: " + mCursor.getInt(0));
        if (_newScore > bestScore) {
            newHighScoreTV.setVisibility(View.VISIBLE);
            bestScore = _newScore;
        }

        bestScoreTV.setText(String.valueOf(bestScore));

        // CHECK "FOR" SCORE, newScore is "greater" than "current top ten score"?
        ArrayList<String[]> getTopTenScoreOfCertainDifficulty = mydb.getAllScore(_difficulty);
        System.out.println("TOP TEN: " + getTopTenScoreOfCertainDifficulty.size());
        // CHECK IF _newScore > 0 and THERE IS AT LEAST 1 item(score) in getTopTenScoreOfCertainDifficulty's array.
        if (_newScore > 0 && getTopTenScoreOfCertainDifficulty.size() != 0) {
            for (String[] stringArr : getTopTenScoreOfCertainDifficulty) {
//            System.out.println("STRING ARRAY: " + stringArr[1]);
                if (_newScore > Integer.valueOf(stringArr[1])) { // stringArr[0] -> name, stringArr[1] -> score
                    nameET.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
                    saveBtn.setOnClickListener(new saveBtnClickListener((Activity) mContext));
                    break; // the "for loop" is immediately terminated.
                }
            }
            // CHECK IF _newScore > 0 and THERE IS NO item(score) in getTopTenScoreOfCertainDifficulty's array.
        } else if(_newScore > 0 && getTopTenScoreOfCertainDifficulty.size() == 0) {
            nameET.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
            saveBtn.setOnClickListener(new saveBtnClickListener((Activity) mContext));
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void save() {

        if(mydb.insertScore(nameET.getText().toString(),
                _newScore,
                _difficulty)){
//            Toast.makeText(getActivity(), "done",
//                    Toast.LENGTH_SHORT).show();
            this.dismiss();
            mGameOverFragment.getFragmentManager().popBackStack("MainMenuFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else{
//            Toast.makeText(getActivity(), "not done",
//                    Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//
//    }

    private class saveBtnClickListener implements View.OnClickListener {
        private Activity mActivity;

        private saveBtnClickListener(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onClick(View view) {
            if (isEmpty(nameET)) {
                Toast.makeText(mActivity, "PLEASE ENTER YOUR NAME", Toast.LENGTH_SHORT)
                        .show();
            } else {
                try {
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    System.out.println("KEYBOARD CATCH EXCEPTION: " + e);
                }
                save();
            }
        }
    }
}
