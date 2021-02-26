package com.typartybuilding.activity.quanminlangdu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.typartybuilding.R;


public class ShowTimeDialog extends Dialog {

    private TextView bigTextView;
    private TextView smallTextView;

    private Context context;
    private int sec = 3;

    private OnTimeOverListener mListener;

    private Handler handler = new Handler();

    public ShowTimeDialog(@NonNull Context context, OnTimeOverListener mListener) {
        super(context);
        this.context = context;
        this.mListener = mListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_time_view);
        bigTextView = findViewById(R.id.big_sec);
        smallTextView = findViewById(R.id.small_time_text);
        bigTextView.setText(String.valueOf(sec));
        smallTextView.setText(sec+"秒后开始录音");
        handler.postDelayed(runnable,1000);
        sec = 3;
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sec--;
            if (sec==0){
                if (mListener != null){
                    mListener.OnTimeOverToDo();
                    ShowTimeDialog.this.dismiss();
                }
            }else {
                bigTextView.setText(String.valueOf(sec));
                smallTextView.setText(sec+"秒后开始录音");
                handler.postDelayed(runnable,1000);
            }
        }
    };

    public interface OnTimeOverListener{
        void OnTimeOverToDo();
    }

}
