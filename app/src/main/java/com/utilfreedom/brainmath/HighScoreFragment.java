package com.utilfreedom.brainmath;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.utilfreedom.brainmath.model.DBHelper;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HighScoreFragment extends Fragment {
    // DATABASE
    DBHelper mydb;
    ArrayList<String[]> scoreEasyArrayList;
    ArrayList<String[]> scoreMediumArrayList;
    ArrayList<String[]> scoreHardArrayList;
    ArrayList<String[]> scoreInsaneArrayList;
    Context mContext;
    LinearLayout.LayoutParams noTVLayout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f);
    LinearLayout.LayoutParams nameTVLayout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f);
    LinearLayout.LayoutParams scoreTVLayout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);

    private LinearLayout easyWrapper;
    private LinearLayout mediumWrapper;
    private LinearLayout hardWrapper;
    private LinearLayout insaneWrapper;

    private TextView easyTV;
    private TextView mediumTV;
    private TextView hardTV;
    private TextView insaneTV;


    public HighScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // DATABASE
        mydb = new DBHelper(getActivity());
        scoreEasyArrayList = mydb.getAllScore("easy");
        scoreMediumArrayList = mydb.getAllScore("medium");
        scoreHardArrayList = mydb.getAllScore("hard");
        scoreInsaneArrayList = mydb.getAllScore("insane");

//        System.out.println("ARRAYLIST FUCK" + scoreEasyArrayList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_high_score, container, false);
        mContext = this.getActivity();

        easyTV = (TextView) v.findViewById(R.id.easyTV);
        mediumTV = (TextView) v.findViewById(R.id.mediumTV);
        hardTV = (TextView) v.findViewById(R.id.hardTV);
        insaneTV = (TextView) v.findViewById(R.id.insaneTV);

        easyTV.setVisibility(View.INVISIBLE);
        mediumTV.setVisibility(View.INVISIBLE);
        hardTV.setVisibility(View.INVISIBLE);
        insaneTV.setVisibility(View.INVISIBLE);

        easyWrapper = (LinearLayout) v.findViewById(R.id.easyWrapper);
        mediumWrapper = (LinearLayout) v.findViewById(R.id.mediumWrapper);
        hardWrapper = (LinearLayout) v.findViewById(R.id.hardWrapper);
        insaneWrapper = (LinearLayout) v.findViewById(R.id.insaneWrapper);

        checkScoreBeforeCreateLayout();

        return v;
    }

    private void checkScoreBeforeCreateLayout() {
        if (scoreEasyArrayList.size() != 0) {
            easyTV.setVisibility(View.VISIBLE);

            for (int i = 0; i < scoreEasyArrayList.size(); i++) {
                createLayout(i, scoreEasyArrayList, easyWrapper);
            }
        }

        if (scoreMediumArrayList.size() != 0) {
            mediumTV.setVisibility(View.VISIBLE);

            for (int i = 0; i < scoreMediumArrayList.size(); i++) {
                createLayout(i, scoreMediumArrayList, mediumWrapper);
            }
        }

        if (scoreHardArrayList.size() != 0) {
            hardTV.setVisibility(View.VISIBLE);

            for (int i = 0; i < scoreHardArrayList.size(); i++) {
                createLayout(i, scoreHardArrayList, hardWrapper);
            }
        }

        if (scoreInsaneArrayList.size() != 0) {
            insaneTV.setVisibility(View.VISIBLE);

            for (int i = 0; i < scoreInsaneArrayList.size(); i++) {
                createLayout(i, scoreInsaneArrayList, insaneWrapper);
            }
        }
    }

    private void createLayout(int i, ArrayList<String[]> scoreArrayList,LinearLayout wrapper) {
        LinearLayout parent = new LinearLayout(mContext);
        parent.setOrientation(LinearLayout.HORIZONTAL);

        TextView noTV = new TextView(mContext);
        noTV.setLayoutParams(noTVLayout);
        noTV.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        TextView nameTV = new TextView(mContext);
        nameTV.setLayoutParams(nameTVLayout);
        nameTV.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        TextView scoreTV = new TextView(mContext);
        scoreTV.setLayoutParams(scoreTVLayout);
        scoreTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        noTV.setText(i + 1 + ".");
        nameTV.setText(scoreArrayList.get(i)[0]);
        scoreTV.setText(scoreArrayList.get(i)[1]);

        parent.addView(noTV);
        parent.addView(nameTV);
        parent.addView(scoreTV);

        wrapper.addView(parent);
    }

}
