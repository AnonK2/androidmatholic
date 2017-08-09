package com.utilfreedom.brainmath.model;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.utilfreedom.brainmath.Config;
import com.utilfreedom.brainmath.StartGameFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by kennywang on 6/19/17.
 */

public class Operation {
    Random rand = new Random();
    StartGameFragment SGF;

    private String _answer1Text;
    private String _answer2Text;
    private boolean _answer;

    public Operation(StartGameFragment sgf) {
        SGF = sgf;
    }

    // Operation enum
    private enum OperationType {
        MINUS ("-"),
        PLUS("+"),
        MULTIPLY("x");

        private String operationSymbol;

        private OperationType(String autoAssign) { // whenever Operation."KEY" called, the "CONSTRUCTOR" assign
            // the "VALUE" of the specific "KEY" to "autoAssign" pass it to "operationSymbol".
            operationSymbol = autoAssign;
        }

        public String getOperationSymbol() {
            return operationSymbol;
        }

    }

    public void setNumberAndOperation(boolean _isOnline, String roomUID, int _score, int _range) {


        String num1Text = "";
        String num2Text = "";
        String num3Text = "";
        int result = 0;
//        int num3 = 0;
        int num1 = 0;
        int num2 = 0;

        // DIFFICULT, "RANGE" INCREASE WHEN "SCORE" INCREASE.
        if (_score < 10) {
            num1 = rand.nextInt(_range) + 1;
            num2 = rand.nextInt(_range) + 1;
        } else if (_score > 10 && _score < 21) {
            num1 = rand.nextInt(_range + 5) + 1;
            num2 = rand.nextInt(_range + 5) + 1;
        } else {
            num1 = rand.nextInt(_range + 25) + 1;
            num2 = rand.nextInt(_range + 25) + 1;
        }


        OperationType randOperation = OperationType.values()[rand.nextInt(2)];
//        System.out.println(randOperation + " equals TO: " + randOperation.operationSymbol);

//        operationSymbol.setText(randOperation.getOperationSymbol()); // randOperation -> Operation."KEY", "KEY" -> MINUS, PLUS, or MULTIPLY

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
//            case MULTIPLY:
//                result = num1 * num2;
//                break;
        }

//        // VERSION 1 GAME -> CORRECT or WRONG VERSION
//        if (Math.random() <= 0.5) { // 50% always num3
//            num3 = result;
//        } else { // 50% -> num3 + random num range(-1 to 1)
//            num3 = (result != 0) ?  result + rand.nextInt(3) + (-1) : result; // range -1 to 1
//        }

        int randomBlank = rand.nextInt(3);

        switch (randomBlank) {
            case 0:
                num1Text = "_";
                num2Text = String.valueOf(num2);
                num3Text = String.valueOf(result);
                settingButton(num1);
                break;
            case 1:
                num1Text = String.valueOf(num1);
                num2Text = "_";
                num3Text = String.valueOf(result);
                settingButton(num2);
                break;
            case 2:
                num1Text = String.valueOf(num1);
                num2Text = String.valueOf(num2);
                num3Text = "_";
                settingButton(result);
                break;
        }

        String operation = randOperation.getOperationSymbol();
//        number1.setText(num1Text);
//        number2.setText(num2Text);
//        number3.setText(num3Text);
//        answer1Btn.setText(_answer1Text);
//        answer2Btn.setText(_answer2Text);
        System.out.println("INFO :" + randOperation.getOperationSymbol() + " " + num1Text + " " + num2Text + " " + num3Text + " " + _answer1Text + " " + _answer2Text + " " + _answer);


        if (_isOnline) {
            final String finalOperation = operation;
            final String finalNum1Text = num1Text;
            final String finalNum2Text = num2Text;
            final String finalNum3Text = num3Text;

            Map<String, Object> map = new HashMap<>();
            map.put("operation", finalOperation);
            map.put("num1Text", finalNum1Text);
            map.put("num2Text", finalNum2Text);
            map.put("num3Text", finalNum3Text);
            map.put("answer1Text", _answer1Text);
            map.put("answer2Text", _answer2Text);
            map.put("answer", _answer);

            DatabaseReference ref = Config.DB_BASE.child("games")
                                                .child(roomUID);
            ref.updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    SGF.setupUIValueWithAnswer(finalOperation, finalNum1Text, finalNum2Text, finalNum3Text, _answer1Text, _answer2Text, _answer);
                }
            });
        } else {
            SGF.setupUIValueWithAnswer(operation, num1Text, num2Text, num3Text, _answer1Text, _answer2Text, _answer);
        }

//        VERSION 1 GAME -> CORRECT or WRONG VERSION
//        if (num3 == result) {
//            answer = true;
//        } else {
//            answer = false;
//        }
    }

    public void settingButton(int result) {

        if (Math.random() <= 0.5) {
            int wrongChoice = 0;

            if (result == 0) {
                wrongChoice = result + rand.nextInt(3) + (1); // range 1 to 3
            } else if (result >= 1) {
                int[] randMinus = new int[] {-1, 1, 2};
                wrongChoice = result + randMinus[rand.nextInt(3)];
            }

            _answer1Text = String.valueOf(result);
            _answer2Text = String.valueOf(wrongChoice);
            _answer = true;
        } else {
            int wrongChoice = 0;

            if (result == 0) {
                wrongChoice = result + rand.nextInt(3) + (1); // range 1 to 3
            } else if (result >= 1) {
                int[] randMinus = new int[] {-1, 1, 2};
                wrongChoice = result + randMinus[rand.nextInt(3)];
            }

            _answer1Text = String.valueOf(wrongChoice);
            _answer2Text = String.valueOf(result);
            _answer = false;
        }
    }
}
