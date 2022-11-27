package com.example.baseapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.example.baseapp.R;
import com.example.baseapp.view.CircleProgressBar;


public class LoadingDialogVn extends Dialog {
    CircleProgressBar progress;
    private Handler handler;
    private Runnable run;
    private Dialog dialog;

    public LoadingDialogVn(Context context) {
        super(context);
        init();
    }


    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
        handler = new Handler();
        dialog = this;
        run = new Runnable() {
            @Override
            public void run() {
                try {
                    if (dialog != null && isShowing()) {
                        dismiss();
                    }
                } catch (Exception e) {
                    ////LogVnp.Shape1(Shape1);
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog_vn);
        progress = (CircleProgressBar) findViewById(R.id.progressLoadingVn);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void setCancel(boolean isCancel) {
        setCancelable(isCancel);
        setCanceledOnTouchOutside(isCancel);
    }

    @Override
    public void show() {
        super.show();
        handler.postDelayed(run, 90000);
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            handler.removeCallbacks(run);
        } catch (Exception ex) {
            ////LogVnp.Shape1(ex);
        }
    }
}
