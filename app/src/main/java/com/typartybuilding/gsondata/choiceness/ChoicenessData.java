package com.typartybuilding.gsondata.choiceness;

import com.typartybuilding.gsondata.ArticleBanner;

import java.io.Serializable;
import java.util.Date;

public class ChoicenessData {

    public String code;
    public Data data;
    public String message;


    public class Data{

        public ArticleBanner[] articleBanner;
        public ArticleBanner[] articleVideo;
        public MicroVision [] microVision;

    }

    //轮播图数据
   /* public class ArticleBanner implements Serializable{

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
        public String urlType;    // 1：图片，2：视频
        public String videoUrl;   //视频链接
        public int isPraise;      //是否点赞：0：否，1：是
        public int isCollect;     //是否收藏：0：否，1：是

    }*/

    //热门视频
   /* public class ArticleVideo implements Serializable {
        public String articleBlock;
        public String articleDetailUrl;
        public int articleId;
        public String articleProfile;
        public String articleTitle;
        public int articleType;
        public int browseTimes;  //浏览次数
        public int collectedNum;  //收藏量
        public int examineStatus;
        public String picUrls;
        public int praisedNum;    //获赞
        public long publishDate;   //发布时间
        public String rejectCause;
        public long updateTime;    //修改时间
        public String urlType;    // 1：图片，2：视频
        public String videoUrl;   //视频链接
        public int isPraise;      //是否点赞：0：否，1：是
        public int isCollect;     //是否收藏：0：否，1：是

    }*/

    //微视频
    public class MicroVision implements Serializable{

        public String userHeadImg;  //发布者头像
        public int userId;
        public String userName;        //发布者昵称
        public int visionBrowseTimes;    //浏览数/播放量
        public String visionFileUrl;   //图片、视频、音频链接
        public int visionId;
        public int visionPraisedNum;    //获赞数
        public String visionTitle;
        public int visionType;         //1：图片，2：视频，3：音频
        public int isPraise;      //是否点赞：0：否，1：是
        public int isFollowed;     //是否关注：0：否，1：是
        public String videoCover;   // 视频封面
    }

}
