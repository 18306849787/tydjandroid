package com.typartybuilding.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.typartybuilding.R;

public class WhiteTitle2Layout extends LinearLayoutCompat {

    private ImageButton btnBack ;
    private TextView textBack;
    private TextView textTitle;

    public WhiteTitle2Layout(Context context) {
        super(context);
    }

    public WhiteTitle2Layout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_white_title2,this);
        btnBack = view.findViewById(R.id.imageButton_back);
        textBack = view.findViewById(R.id.textView_back);
        textTitle = view.findViewById(R.id.textView_title);

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });

        textBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }

    public ImageButton getBtnBack() {
        return btnBack;
    }

    public TextView getTextTitle() {
        return textTitle;
    }
}
