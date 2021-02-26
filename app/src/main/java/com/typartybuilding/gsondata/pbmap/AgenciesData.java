package com.typartybuilding.gsondata.pbmap;

import java.io.Serializable;

/**
 *  服务机构  1：党组织机构，2：党群服务中心，3：教育基地
 */

public class AgenciesData {

    public String code;
    public Data data;
    public String message;


    public class Data{
        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public AgencyData rows[];

        public int totalCount;

    }


    public class AgencyData implements Serializable {


        public String businessHours;    //营业时间',
        public int distance;            //距离
        public String orgAddress;      //地址',
        public String orgContacts;      //联系人',
        public int orgId;

        public float orgLongitude;     //经度',
        public float orgLatitude;      //`:'纬度',

        public String orgName;         //名称',
        public String orgPanoramaUrl;   // '全景图多张以英文逗号隔开',
        public String orgPhone;         //电话,
        public int orgType;            //1：党组织机构，2：党群服务中心，3：教育基地',
        public String regionName;      //区域名称",

        //public int regionId;           //区域id',

    }

}
