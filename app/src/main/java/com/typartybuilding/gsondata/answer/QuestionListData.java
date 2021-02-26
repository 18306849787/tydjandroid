package com.typartybuilding.gsondata.answer;


/**
 * 题目列表-随机抽取
 */
public class QuestionListData {

    public String code;
    public TopicData[] data;
    public String message;


    public class TopicData{

         public int itemId;               //主键ID
         public int itemTypeId;           //题型ID
         public String itemContent;       //"题目"，
         public String optionA;           //"XXXXX",答案A
         public String optionB;           //"XXXXX",答案B
         public String optionC;           //"XXXXX",答案C
         public String optionD;           //"XXXXX",答案D
         public String itemAnswer;        //A",正确答案
         public String testingPoints;     //:"",考点
         public String itemAnalysis;      //:""解析
         public long createTime;
    }

}
