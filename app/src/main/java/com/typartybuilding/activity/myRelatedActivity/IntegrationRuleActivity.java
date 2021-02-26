package com.typartybuilding.activity.myRelatedActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.WhiteTitleBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntegrationRuleActivity extends WhiteTitleBaseActivity {

    @BindView(R.id.textView_title)
    TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integration_rule);
        ButterKnife.bind(this);
        textTitle.setText("积分规则");
    }
}
