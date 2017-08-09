package com.utilfreedom.brainmath.controller;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.utilfreedom.brainmath.R;

public class ExitDialog {
    Activity mActivity;
    Dialog mDialog;

    public ExitDialog(Activity activity) {
        mActivity = activity;
        mDialog = new Dialog(activity);
    }

    public void showDialog(String msg) {
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // NO TITLE
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.custom_dialog_exit);

        TextView msgTV = (TextView) mDialog.findViewById(R.id.msg_dialog);
        msgTV.setText(msg);

        Button cancelBtn = (Button) mDialog.findViewById(R.id.cancelBtn);
        Button exitBtn = (Button) mDialog.findViewById(R.id.exitBtn);

        cancelBtn.setOnClickListener(new exitDialogClickListener(0));
        exitBtn.setOnClickListener(new exitDialogClickListener(1));

        mDialog.show();
    }

    private class exitDialogClickListener implements View.OnClickListener {
        private int _option;

        public exitDialogClickListener(int option) {
            _option = option;
        }

        @Override
        public void onClick(View view) {
            if (_option == 0) {
                mDialog.dismiss();
            } else if (_option == 1) {
                mDialog.dismiss();
                mActivity.finish();
            }
        }
    }
}

