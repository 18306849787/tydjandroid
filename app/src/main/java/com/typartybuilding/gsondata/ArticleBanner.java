package com.typartybuilding.gsondata;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 具体的 资讯文章 ，学习时刻视频 通用对象，用于activity之间传递
 */

public class ArticleBanner implements Serializable , MultiItemEntity {

    public String articleBlock;
    public String articleDetailUrl;    //详情富文本链接
    public int articleId;
    public String articleTitle;
    public String articleProfile;      //简介
    public String articleSource;      //出处
    public int articleType;
    public int browseTimes;  //浏览次数
    public int collectedNum;  //收藏量
    public String picUrls;       //"http://xxxxxxx,http://xxxdsdf",  图片多图以英文逗号隔开
    public int praisedNum;     //获赞
    public long publishDate;   //发布时间
    public long updateTime;     //修改时间
    public int urlType;    // 1：图片，2：视频  3,音频
    public String videoUrl;   //视频链接
    public int isPraise;      //是否点赞：0：否，1：是
    public int isCollect;     //是否收藏：0：否，1：是
    public int videoDuration;   //视频时长（s）
    public String videoCover;   //视频封面

    //新增  引标题， 副标题
    public String quotationTitle;      //引标题
    public String subtitle;            //副标题
    public MicroVideo[] visionList;    //精选 新增微视 字段
    //热门视频所有
    public int examineStatus;
    public String rejectCause;

    @Override
    public int getItemType() {
        if (urlType==1){
            if (getPicUrls().size()==0){
                return 1000;
            }else if (getPicUrls().size()==1||getPicUrls().size()==2){
                return 1001;
            }else if (getPicUrls().size()>2){
                return 1003;
            }
        }
        return urlType;
    }

    public List<String> getPicUrls() {
        if (TextUtils.isEmpty(picUrls)){
            return new ArrayList<>();
        }
        String [] str = picUrls.split(",");
        List<String> resultList= new ArrayList<>(Arrays.asList(str));
        return resultList;
    }

    //我的足迹
    //public String fileUrl;     //文件url根据urlType而定，多图以逗号隔开
    //public int isFollowed;
    //public int targetId;      // 文章ID/微视ID
    //public String targetTitle;
    //public int targetType;    // 1：文章，2：微视
    //public String userHeadImg;   // 头像（微视发布者）
    //public int userId;
    //public String userName;
    //public String videoCover;     //视频封面

}
