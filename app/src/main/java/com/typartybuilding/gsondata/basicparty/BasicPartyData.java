package com.typartybuilding.gsondata.basicparty;

import com.typartybuilding.gsondata.ArticleBanner;


/**
 *  基层党建 数据
 */

public class BasicPartyData {

    public String code;
    public BasicParty [] data;
    public String message;



    public class BasicParty{

        public String articleSource;    //中共太原市晋源区委组织部, 新闻出处
        public ArticleBanner [] rows;

    }

}
