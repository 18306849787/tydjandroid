package com.typartybuilding.gsondata.pbmicrovideo;

import com.typartybuilding.gsondata.MicroVideo;

/**
 *  党建微视-发现精彩
 */
public class FindFascinatingData {

    public String code;
    public Data data;
    public String message;


    public class Data{

        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public MicroVideo [] rows;

        public int totalCount;
    }
}
