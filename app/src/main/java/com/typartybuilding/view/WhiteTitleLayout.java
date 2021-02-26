package com.typartybuilding.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.typartybuilding.R;

public class WhiteTitleLayout extends LinearLayoutCompat {

    private ImageView btnBack ;
    private TextView textTitle;

    public WhiteTitleLayout(Context context) {
        super(context);
    }

    public WhiteTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_white_title,this);
        btnBack = view.findViewById(R.id.imageButton_back);
        textTitle = view.findViewById(R.id.textView_title);

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }

    public ImageView getBtnBack() {
        return btnBack;
    }

    public TextView getTextTitle() {
        return textTitle;
    }
}
