package com.utilfreedom.brainmath;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mLinearLayout;
    private TextView indicator;
    private TextView gameOver;
    private EditText second;
    private Button startBtn;

    private boolean starting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indicator = (TextView) findViewById(R.id.indicator);
        gameOver = (TextView) findViewById(R.id.gameOver);
        second = (EditText) findViewById(R.id.second);
        startBtn = (Button) findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameOver.setText("");

                long _editTextTime = Long.valueOf(String.valueOf(second.getText()));

                Animation mAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.indicatoranimation);
                mAnimation.setDuration(_editTextTime * 1000);
                indicator.startAnimation(mAnimation);

//                updateGame(_editTextTime);

                CheckIndicatorToZero task = new CheckIndicatorToZero(_editTextTime);

                if (starting == true) {
                    task.cancel(true);
                    task.counting = false;
                    new CheckIndicatorToZero(_editTextTime).cancel(true);
                    new CheckIndicatorToZero(_editTextTime).execute();
                    System.out.println("TASK CANCEL!");

//                    if (task.isCancelled()) {
//                        System.out.println("CANCELLED");
//                        task.execute();
//                    }

                } else {
                    System.out.println("TASK START!");
                    new CheckIndicatorToZero(_editTextTime).execute();
                    starting = true;
                }




            }
        });
    }

//    public void updateGame(double editTextTime) {
//        CheckIndicatorToZero task = new CheckIndicatorToZero(editTextTime);
//        System.out.println("STATUS CODE: " + task.getStatus());
//
////        task.execute();
//        if (task.getStatus() == AsyncTask.Status.PENDING) {
//            System.out.println("STATUS CODE: " + task.getStatus());
//            System.out.println("TASK CANCEL!");
//             task.cancel(true);
//             task = new CheckIndicatorToZero(editTextTime);
//             task.execute();
//        } else {
//            System.out.println("STATUS CODE: " + task.getStatus());
//            System.out.println("TASK START!");
//            task = new CheckIndicatorToZero(editTextTime);
//            task.execute();
//        }
//    }

    public void decreaseIndicator(double editTextTime, boolean counting) {
        final double _endTime = editTextTime + (System.currentTimeMillis() / 1000.0);
//        double now;

        while ((System.currentTimeMillis() / 1000.0) < _endTime && counting == true) {
//            now = _endTime - (System.currentTimeMillis() / 1000.0);

        }
    }

    private class CheckIndicatorToZero extends AsyncTask<Void, Void, Void> {
        private double _editTextTime;
        private final double nowSecond = System.currentTimeMillis() / 1000.0;
        public boolean counting = true;

        public CheckIndicatorToZero(double editTextTime) {
            _editTextTime = editTextTime;
        }

        @Override
        protected Void doInBackground(Void... objects) {
//                decreaseIndicator(_editTextTime, true);

            return null;
        }


        @Override
        protected void onPostExecute(Void b) {
            super.onPostExecute(b);

            System.out.println("GAME OVER");
            gameOver.setText("GAME OVER");
            starting = false;
        }
    }




}
