package com.typartybuilding.activity.myRelatedActivity;

import android.graphics.Color;
import android.service.fingerprint.IFingerprintService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.activity.pbmap.ServiceCenterActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.answer.IntegralData;
import com.typartybuilding.gsondata.answer.QuestionListData;
import com.typartybuilding.gsondata.personaldata.TaMicro;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.MapUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SmartAnswerActivity extends WhiteTitleBaseActivity {

    private String TAG = "SmartAnswerActivity";

    @BindView(R.id.button_last)
    Button btnLast;               //上一题按钮
    @BindView(R.id.button_next)
    Button btnNext;               //下一题按钮
    @BindView(R.id.textView_topic)
    TextView textTopic;              //题目
    @BindViews({R.id.textView_a, R.id.textView_b, R.id.textView_c, R.id.textView_d})
    TextView textView[];             //四个选项
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private PopupWindow popupWindow;  //底部弹窗
    private View popView;
    private TextView textView1;       //答对3道，答错2道
    private TextView textView2;       //正确率：60%
    private TextView textView3;       //积分：+1

    private List<QuestionListData.TopicData> dataList = new ArrayList<>();   //保存题目
    private List<String> rightAnswer = new ArrayList<>();              //保持每一道题的正确答案
    private List<String> recordAnswer = new ArrayList<>();            //记录用户的答题 答案
    private List<String> recordWrong = new ArrayList<>();             //以0和1来记录每一道题正确与否
    private int currentTopic;          //当前题目

    String blue = "<font color='#5895FA'>(单选题)</font>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_answer);
        ButterKnife.bind(this);
        initPopupWindow();
        //获取题目列表
        getQuestionList();

    }


    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_smart_answer_ac, null);
        ImageButton btnCancel = popView.findViewById(R.id.imageButton_back);   //取消弹窗
        ImageButton button1 = popView.findViewById(R.id.imageButton1);      //再来一组
        ImageButton button2 = popView.findViewById(R.id.imageButton2);      //确定
        textView1 = popView.findViewById(R.id.textView1);
        textView2 = popView.findViewById(R.id.textView2);
        textView3 = popView.findViewById(R.id.textView3);

        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                finish();

            }
        });

        //再来一组
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQuestionList();
                popupWindow.dismiss();
            }
        });

        //确定
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                finish();
                //调保存错题的接口
              /*  String itemIds = null;          //题目
                String answerOptions = null;    //答案
                String answerJudge = null;      //结果
                for (int i =0; i < dataList.size(); i++){
                    if (i == 0){
                        itemIds = dataList.get(i).itemContent;
                        answerOptions = recordAnswer.get(i);
                        answerJudge = recordWrong.get(i);
                    }else {
                        itemIds = itemIds + "," + dataList.get(i).itemContent;
                        answerOptions = answerOptions + "," + recordAnswer.get(i);
                        answerJudge = answerJudge + "," + recordWrong.get(i);
                    }
                }
                Log.i(TAG, "onClick: itemIds : " + itemIds);
                Log.i(TAG, "onClick: answerOptions : " + answerOptions);
                Log.i(TAG, "onClick: answerJudge: " + answerJudge);
                saveAnswer(itemIds,answerOptions,answerJudge);*/
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(SmartAnswerActivity.this,1f);
            }
        });

    }

    private void showPopupWindow(int integral){
        if (!popupWindow.isShowing()){
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
        int correctNum = 0;   //正确数
        int wrongNum = 0;     //错误数
        for (int i = 0; i < dataList.size(); i++){
            Log.i(TAG, "showPopupWindow: rightAnswer : " + rightAnswer.get(i));
            Log.i(TAG, "showPopupWindow: recordAnswer : " + recordAnswer.get(i));
            if (rightAnswer.get(i).equals(recordAnswer.get(i))){
                correctNum++;
                //recordWrong.add("1");
            }else {
                wrongNum++;
               //recordWrong.add("0");
            }
        }
        int percent = 0;
        if ((correctNum+wrongNum) != 0) {
            //正确率
            percent = (correctNum * 100) / (correctNum + wrongNum);
        }
        textView1.setText("答对"+correctNum+"道"+","+"答错"+wrongNum+"道" );
        textView2.setText("正确率：" + percent+"%");
        Log.i(TAG, "showPopupWindow: integral : " + integral);
        if (correctNum >= 3 && integral == 0){
            textView3.setText("积分：+0");
            Toast.makeText(SmartAnswerActivity.this,"积分已达到每日上限",Toast.LENGTH_LONG).show();
        }else {
            textView3.setText("积分：+" + integral);
        }


       /* if (integral >= 10){
            textView3.setText("积分：+0");
            Toast.makeText(SmartAnswerActivity.this,"积分已达到每日上限",Toast.LENGTH_LONG).show();
        }else {
            if (correctNum == 3 || correctNum == 4) {
                textView3.setText("积分：+1");
            } else if (correctNum == 5) {
                textView3.setText("积分：+2");
            } else {
                textView3.setText("积分：+0");
            }
        }*/

    }

    /**
     *  提交
     */
    private void submitAnswer(){

        for (int i = 0; i < dataList.size(); i++){
            Log.i(TAG, "showPopupWindow: rightAnswer : " + rightAnswer.get(i));
            Log.i(TAG, "showPopupWindow: recordAnswer : " + recordAnswer.get(i));
            if (rightAnswer.get(i).equals(recordAnswer.get(i))){
                recordWrong.add("1");
            }else {
                recordWrong.add("0");
            }
        }

        //调保存错题的接口
        String itemIds = null;          //题目
        String answerOptions = null;    //答案
        String answerJudge = null;      //结果
        for (int i =0; i < dataList.size(); i++){
            if (i == 0){
                itemIds = dataList.get(i).itemId + "";
                answerOptions = recordAnswer.get(i);
                answerJudge = recordWrong.get(i);
            }else {
                itemIds = itemIds + "," + dataList.get(i).itemId;
                answerOptions = answerOptions + "," + recordAnswer.get(i);
                answerJudge = answerJudge + "," + recordWrong.get(i);
            }
        }
        Log.i(TAG, "onClick: itemIds : " + itemIds);
        Log.i(TAG, "onClick: answerOptions : " + answerOptions);
        Log.i(TAG, "onClick: answerJudge: " + answerJudge);
        saveAnswer(itemIds,answerOptions,answerJudge);

    }

    //第一次显示第一道题
    private void initView(){
        //第一次，显示在第一道题时的布局
        firstTopic();
        //获取数据后，选项布局可见，进度条不可见
        progressBar.setVisibility(View.GONE);
        textView[0].setVisibility(View.VISIBLE);
        textView[1].setVisibility(View.VISIBLE);
        textView[2].setVisibility(View.VISIBLE);
        textView[3].setVisibility(View.VISIBLE);
        textTopic.setVisibility(View.VISIBLE);
        //设置四个选项 未选中
        textView[0].setSelected(false);
        textView[1].setSelected(false);
        textView[2].setSelected(false);
        textView[3].setSelected(false);

        currentTopic = 0;
        QuestionListData.TopicData topicData = dataList.get(currentTopic);
        //题目
        textTopic.setText(Html.fromHtml(blue + topicData.itemContent ));
        textView[0].setText(topicData.optionA);
        textView[1].setText(topicData.optionB);
        textView[2].setText(topicData.optionC);
        textView[3].setText(topicData.optionD);

      /*  String str = "1.实事求是，是马克思主义的根本观点， 是中国共产党人认识世界、改造世界根本要求， 是我们党的基本思想方法、工作方法、（ ）方 法。";
        String strr = blue + str;
        textTopic.setText(Html.fromHtml(strr));*/
    }


    //四个选项
    @OnClick({R.id.textView_a, R.id.textView_b, R.id.textView_c, R.id.textView_d})
    public void onClickTextView(View view){
        switch (view.getId()){
            case R.id.textView_a:
                textView[0].setSelected(true);
                changeState(0);
                //保存答案
                recordAnswer.set(currentTopic,"A");
                break;
            case R.id.textView_b:
                textView[1].setSelected(true);
                changeState(1);
                //保存答案
                recordAnswer.set(currentTopic,"B");
                break;
            case R.id.textView_c:
                textView[2].setSelected(true);
                changeState(2);
                //保存答案
                recordAnswer.set(currentTopic,"C");
                break;
            case R.id.textView_d:
                textView[3].setSelected(true);
                changeState(3);
                //保存答案
                recordAnswer.set(currentTopic,"D");
                break;
        }
    }

    private void changeState(int i){
        for (int j = 0; j < 4; j++){
            if (j != i){
                textView[j].setSelected(false);
            }
        }
    }

    //在第一题时的布局
    private void firstTopic(){
        btnLast.setSelected(false);
        btnLast.setEnabled(false);
        btnLast.setTextColor(Color.parseColor("#ffaaaaaa"));
        btnNext.setSelected(false);
        btnNext.setText("下一题");
    }
    //在答题时的布局
    private void answerTopic(){
        btnLast.setSelected(true);
        btnLast.setEnabled(true);
        btnLast.setTextColor(Color.parseColor("#ffffffff"));
        btnNext.setSelected(false);
        btnNext.setText("下一题");
    }
    //到最后一题时的布局
    private void lastTopic(){
        btnLast.setSelected(true);
        btnLast.setEnabled(true);
        btnLast.setTextColor(Color.parseColor("#ffffffff"));
        btnNext.setSelected(true);
        btnNext.setText("提交");
    }

    //上一题和下一题 的按钮
    @OnClick({R.id.button_last, R.id.button_next})
    public void onClickBtn(View view){
        switch (view.getId()){
            case R.id.button_last:
                if (dataList.size() != 0) {
                    //切换题目
                    switchTopic(currentTopic - 1);
                    //设置布局
                    if (currentTopic - 1 < 0) {   //到第一题了
                        firstTopic();
                    } else {
                        answerTopic();   //在答题中
                    }
                }
                break;
            case R.id.button_next:
                if (dataList.size() != 0) {
                    if (currentTopic + 1 == dataList.size()) {   //开始提交
                        //先判断，是否所有的题 已经答完
                        int count = 0;     //记录 有多少题没有答完
                        for (int i = 0; i < recordAnswer.size(); i++) {
                            if (recordAnswer.get(i) == "0") {
                                count++;
                            }
                        }
                        if (count > 0) {
                            Toast.makeText(this, "还有" + count + "道题没有答", Toast.LENGTH_SHORT).show();
                        } else {
                            //提交答题
                            submitAnswer();
                            //showPopupWindow();
                        }
                    } else {
                        //切换题目
                        switchTopic(currentTopic + 1);
                        if (currentTopic + 1 == dataList.size()) {
                            lastTopic();
                        } else {
                            answerTopic();
                        }
                    }
                }

                break;

        }
    }

    //切换题目
    private void switchTopic(int position){
        if (dataList.size() != 0) {
            QuestionListData.TopicData topicData = dataList.get(position);
            //题目
            textTopic.setText(Html.fromHtml(blue + topicData.itemContent));
            textView[0].setText(topicData.optionA);
            textView[1].setText(topicData.optionB);
            textView[2].setText(topicData.optionC);
            textView[3].setText(topicData.optionD);
            //判断该题是否已经 答了
            String result = recordAnswer.get(position);
            if (result == "A") {
                textView[0].setSelected(true);
            } else{
                textView[0].setSelected(false);
            }
            if (result == "B") {
                textView[1].setSelected(true);
            } else {
                textView[1].setSelected(false);
            }
            if (result == "C") {
                textView[2].setSelected(true);
            } else {
                textView[2].setSelected(false);
            }
            if (result == "D") {
                textView[3].setSelected(true);
            }else {
                textView[3].setSelected(false);
            }
            currentTopic = position;
            Log.i(TAG, "switchTopic: currentTopic ：" + currentTopic);
        }
    }


    public void initData(QuestionListData questionListData){
        QuestionListData.TopicData[] topicData = questionListData.data;
        if (topicData != null){
            if (dataList.size() > 0){
                dataList.clear();
            }
            if (rightAnswer.size() > 0){
                rightAnswer.clear();
            }
            if (recordAnswer.size() > 0){
                recordAnswer.clear();
            }
            if (recordWrong.size() > 0){
                recordWrong.clear();
            }
            for (int i = 0; i < topicData.length; i++){

                dataList.add(topicData[i]);
                //保存正确答案
                rightAnswer.add(topicData[i].itemAnswer);
                //答题前，先设置 每一道题 的答案为 “0” 字符
                recordAnswer.add("0");
                Log.i(TAG, "initData: topicData size : " + topicData.length);
                Log.i(TAG, "initData: 题目 ： " + topicData[i].itemContent);
                Log.i(TAG, "initData: 题目id ：" + topicData[i].itemId);
                Log.i(TAG, "initData: 正确答案 ： " + topicData[i].itemAnswer);
            }
            initView();
        }
    }

    /**
     *  获取题目列表
     */
    private void getQuestionList(){
        //请求数据时，选项布局不可见，进度条可见
        progressBar.setVisibility(View.VISIBLE);
        textView[0].setVisibility(View.INVISIBLE);
        textView[1].setVisibility(View.INVISIBLE);
        textView[2].setVisibility(View.INVISIBLE);
        textView[3].setVisibility(View.INVISIBLE);
        textTopic.setVisibility(View.INVISIBLE);

        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Log.i(TAG, "getQuestionList: userId : " + userId);
        Log.i(TAG, "getQuestionList: token : " + token);

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getQuestionList(userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<QuestionListData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(QuestionListData questionListData) {
                        int code = Integer.valueOf(questionListData.code);
                        Log.i(TAG, "onNext: 题目列表code : " + code);
                        if (code == 0){
                            initData(questionListData);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(questionListData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(SmartAnswerActivity.this,questionListData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e : " + e );
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     *  答题保存-错题加入错题集
     * @param itemIds            题目ids,以逗号拼接
     * @param answerOptions      选项 以逗号拼接 如A,B,A,D,C
     * @param answerJudge        结果，以逗号拼接，如 1,0,1,1,0  (0：错误，1：正确。
     */
    private void saveAnswer(String itemIds,String answerOptions,String answerJudge){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Log.i(TAG, "saveAnswer: usetId : " + userId);
        Log.i(TAG, "saveAnswer: token : " + token);
        Log.i(TAG, "saveAnswer: itemIds : " + itemIds);
        Log.i(TAG, "saveAnswer: answerOptions : " + answerOptions);
        Log.i(TAG, "saveAnswer: answerJudge : " + answerJudge);

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.saveAnswer(userId,itemIds,answerOptions,answerJudge,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<IntegralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(IntegralData integralData) {
                        int code = Integer.valueOf(integralData.code);
                        Log.i(TAG, "onNext: 保存错题code : " + code);
                        if (code == 0){
                            //获取积分，因每天的上限为 10分
                            int integral = integralData.data;
                            showPopupWindow(integral);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(integralData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(SmartAnswerActivity.this,integralData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e : " + e );
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    @OnClick(R.id.textView_back)
    public void onClickBack(){
        finish();
    }




}
