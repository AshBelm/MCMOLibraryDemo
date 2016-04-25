package com.mcmo.z.library.widget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mcmo.z.library.R;


/**
 * Created by weizhang210142 on 2016/4/25.
 */
public class BottomDialog {
    private Dialog mDialog;

    public BottomDialog(Context context) {
        mDialog = new Dialog(context, R.style.Dialog_Bottom);
    }

    public BottomDialog setView(View view) {
        mDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
//        window.getDecorView().setPadding(0,0,0,0);
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.BOTTOM;
        mDialog.onWindowAttributesChanged(wl);
        mDialog.setCanceledOnTouchOutside(true);
        return this;
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
}
