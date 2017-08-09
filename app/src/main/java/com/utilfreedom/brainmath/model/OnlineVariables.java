package com.utilfreedom.brainmath.model;

/**
 * Created by kennywang on 8/5/17.
 */

public class OnlineVariables {
    private String _Operation;
    private String _num1;
    private String _num2;
    private String _num3;
    private String _ans1;
    private String _ans2;
    private boolean _answer;

    private OnlineVariables() {}

    public OnlineVariables(String os, String num1, String num2, String num3, String ans1, String ans2, boolean answer) {
        _Operation = os;
        _num1 = num1;
        _num2 = num2;
        _num3 = num3;
        _ans1 = ans1;
        _ans2 = ans2;
        _answer = answer;
    }

    public String get_Operation() {
        return _Operation;
    }

    public String get_num1() {
        return _num1;
    }

    public String get_num2() {
        return _num2;
    }

    public String get_num3() {
        return _num3;
    }

    public String get_ans1() {
        return _ans1;
    }

    public String get_ans2() {
        return _ans2;
    }

    public boolean is_answer() {
        return _answer;
    }
}
