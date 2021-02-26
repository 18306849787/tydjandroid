package com.typartybuilding.gsondata;


import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 我的收藏， 我的足迹， 党建热搜， 搜索   通用类；
 * 包含： 资讯文章，视频，微视(视频，音频，图片)
 */
public class MixtureData implements Serializable, MultiItemEntity {

    public int hisId;                  //足迹 的 id
    public String articleDetailUrl;    //详情富文本链接
    public String articleProfile;      //简介
    public String articleSource;      //出处
    public int articleType;
    public int browseTimes;  //浏览次数
    public int collectedNum;  //收藏量
    public String fileUrl;     //文件url根据urlType而定，多图以逗号隔开
    public int isCollect;     //是否收藏：0：否，1：是
    public int isFollowed;
    public int isPraise;      //是否点赞：0：否，1：是
    public int praisedNum;     //获赞
    public long publishDate;   //发布时间
    public int targetId;      // 文章ID/微视ID
    public String targetTitle;
    public int targetType;    // 1：文章，2：微视 3.组工
    public int urlType;    // 1：图片，2：视频  3,音频
    public String userHeadImg;   // 头像（微视发布者）
    public int userId;
    public String userName;
    public String videoCover;     //视频封面,
    public long createTime;       //收藏时间
    public int videoDuration;     ////音/视频时长（s）
    public String bgmName;        //微视背景音乐名称

    //新增  引标题， 副标题
    public String quotationTitle;      //引标题
    public String subtitle;            //副标题

    public String title;           //推送的标题
    public String text;            //推送的内容描述



    //targetType：   1：文章(新闻，普通视频)，2：微视
    //urlType：      1：图片，2：视频，3：音频
    //fileUrl:       文件url根据urlType而定，多图以逗号隔开

    @Override
    public int getItemType() {
        if (targetType == 2) {
            return 1200;
        } else {
            if (urlType==2){
                return 1100;
            }
            if (getPicUrlsList().size() == 0) {
                return 1000;
            } else if (getPicUrlsList().size() == 1 || getPicUrlsList().size() == 2) {
                return 1001;
            } else if (getPicUrlsList().size() > 2) {
                return 1003;
            }
            return 1001;
        }

    }

    public List<String> getPicUrlsList() {
        if (TextUtils.isEmpty(fileUrl)) {
            return new ArrayList<>();
        }
        String[] str = fileUrl.split(",");
        List<String> resultList = new ArrayList<>(Arrays.asList(str));
        return resultList;

    }
}
