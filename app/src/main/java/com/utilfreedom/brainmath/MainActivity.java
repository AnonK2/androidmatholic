package com.utilfreedom.brainmath;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.utilfreedom.brainmath.controller.ExitDialog;

public class MainActivity extends AppCompatActivity {
    private static final int DELAY_DURATION = 4500;
    private static final int START_PERCENT = 0;
    private static final int END_PERCENT = 100;
//    private Handler mHandler = new Handler();
//    Bundle _savedInstanceState;

    private TextView splashScreenLoading;
    private TextView progressTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        immersiveMode();

//        _savedInstanceState = savedInstanceState;

        splashScreenLoading = (TextView) findViewById(R.id.splashScreenLoading);
        progressTV = (TextView) findViewById(R.id.progressTV);

        if (savedInstanceState == null) {
            Animation mAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_loading_animation);
            mAnimation.setDuration(DELAY_DURATION);
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    startCountAnimation();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction()
                      .add(R.id.mainLayout, new MainMenuFragment())
                      .commit();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }


            });

            splashScreenLoading.startAnimation(mAnimation);
        }

//        mHandler.postDelayed(new Runnable(){
//            @Override
//            public void run() {
//                if (_savedInstanceState == null) {
//                    FragmentManager fm = getSupportFragmentManager(); // getSupportFragmentManager() -> because AppCompatActivity.
//                    fm.beginTransaction()
//                            .setTransition(FragmentTransaction.TRANSIT_NONE)
//                            .add(R.id.mainLayout, new MainMenuFragment())
//                            .commit();
//                }
//            }
//        }, DELAY_DURATION);


    }

//    public void immersiveMode()
//    {
//        final View decorView = getWindow().getDecorView();
//        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                    decorView.setSystemUiVisibility(
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                }
//            }
//        });
//    }

    private void startCountAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(START_PERCENT, END_PERCENT);
        animator.setDuration(DELAY_DURATION - 25);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                progressTV.setText(animation.getAnimatedValue().toString() + "%");
            }
        });
        
        animator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        immersiveMode();
    }


    // override this onBackPressed to tell the activity that when we press the back key,
    // if there are any fragments in back stack, pop them out (and this is when the addToBackStack() comes into picture).
    // Otherwise follow the default behaviour.
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        System.out.println("COUNT: " + count);

        if (count > 0 && count != 3) {
            getSupportFragmentManager().popBackStack();
        } else if (count == 3) {
            getSupportFragmentManager().popBackStack("MainMenuFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (count == 0) {
            ExitDialog mExitDialog = new ExitDialog(this);
            mExitDialog.showDialog("Are you sure?");
            return;
        } else {
            super.onBackPressed();
        }
    }
}
