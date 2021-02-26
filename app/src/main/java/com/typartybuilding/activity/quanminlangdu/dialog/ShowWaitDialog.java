package com.typartybuilding.activity.quanminlangdu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.typartybuilding.R;

public class ShowWaitDialog extends Dialog {

    private TextView bigTextView;
    private TextView smallTextView;

    private Context context;

    public ShowWaitDialog(@NonNull Context context, OnTimeOverListener mListener) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_wait_view);
        smallTextView = findViewById(R.id.small_time_text);

    }

    public void setSmallText(String text){
        smallTextView.setText(text);
    }

    public interface OnTimeOverListener{
        void OnTimeOverToDo();
    }

}
