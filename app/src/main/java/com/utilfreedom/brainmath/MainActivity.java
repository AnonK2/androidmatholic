package com.utilfreedom.brainmath;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView indicator;
    private TextView gameOver;
    private EditText second;
    private Button startBtn;
    private  Button cancelBtn;
    private TextView number1;
    private TextView number2;
    private TextView number3;
    private TextView operationSymbol;
    private boolean answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        immersiveMode();

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should NEVER SHOW the ACTION BAR if the
        // STATUS BAR is HIDDEN, so hide that too if necessary.
        getSupportActionBar().hide();

        indicator = (TextView) findViewById(R.id.indicator);
        gameOver = (TextView) findViewById(R.id.gameOver);
        second = (EditText) findViewById(R.id.second);
        startBtn = (Button) findViewById(R.id.startBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        number1 = (TextView) findViewById(R.id.number1);
        number2 = (TextView) findViewById(R.id.number2);
        number3 = (TextView) findViewById(R.id.number3);
        operationSymbol = (TextView) findViewById(R.id.operationSymbol);

        setNumberAndOperation();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answer == true) {
                    stillPlaying();
                } else {
                    gameOver.setText("GAME OVER");
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answer == false) {
                    stillPlaying();
                } else {
                    gameOver.setText("GAME OVER");
                }
            }
        });
    }

    private void stillPlaying() {
        gameOver.setText("");
        setNumberAndOperation();

        long _editTextTime = Long.valueOf(String.valueOf(second.getText()));
        // set the animation.
        Animation mAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.indicatoranimation);
        mAnimation.setDuration(_editTextTime * 1000);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                gameOver.setText("GAME OVER"); // after "indicator" decrease to / scaleX to "0".
            }
        });
        // attach the animation and start the animation.
        indicator.startAnimation(mAnimation);
    }

    // Operation enum
    private enum Operation {
        MINUS ("-"),
        PLUS("+"),
        MULTIPLY("x");

        private String operationSymbol;

        private Operation(String autoAssign) { // whenever Operation."KEY" called, the "CONSTRUCTOR" assign
                                               // the "VALUE" of the specific "KEY" to "autoAssign" pass it to "operationSymbol".
            operationSymbol = autoAssign;
        }

        public String getOperationSymbol() {
            return operationSymbol;
        }

    }

    public void setNumberAndOperation() {
        Random rand = new Random();
        int result = 0;
        int num3 = 0;
        int num1 = rand.nextInt(30) + 1;
        int num2 = rand.nextInt(30) + 1;
        Operation randOperation = Operation.values()[rand.nextInt(3)];
//        System.out.println(randOperation + " equals TO: " + randOperation.operationSymbol);
        operationSymbol.setText(randOperation.getOperationSymbol()); // randOperation -> Operation."KEY", "KEY" -> MINUS, PLUS, or MULTIPLY

        switch (randOperation) {
            case MINUS:
                if (num1 < num2) {
                    int a = num1, b = num2;

                    num1 = b;
                    num2 = a;
                }
                result = num1 - num2;
                break;
            case PLUS:
                result = num1 + num2;
                break;
            case MULTIPLY:
                result = num1 * num2;
                break;
        }

        if (Math.random() <= 0.5) { // 50% always num3
            num3 = result;
        } else { // 50% -> num3 + random num range(-1 to 1)
            num3 = (result != 0) ?  result + rand.nextInt(3) + (-1) : result; // range -1 to 1
        }



        number1.setText("" + num1);
        number2.setText("" + num2);
        number3.setText("" + num3);
        System.out.printf("num1: %d, operation: %s, num2: %d, num3:%d\n", num1, String.valueOf(randOperation), num2, num3);

        if (num3 == result) {
            answer = true;
        } else {
            answer = false;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        immersiveMode();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        immersiveMode();
                    }
                });
    }

    public void immersiveMode() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        immersiveMode();
    }
}
