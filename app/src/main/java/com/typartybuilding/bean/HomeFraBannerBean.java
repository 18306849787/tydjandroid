package com.typartybuilding.bean;

import com.typartybuilding.gsondata.ArticleBanner;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-05
 * @describe
 */
public class HomeFraBannerBean {


    private List<ArticleBanner> articleBanner;
    private List<ArticleVideoBean> articleVideo;
    private List<MicroVisionBean> microVision;

    public List<ArticleBanner> getArticleBanner() {
        return articleBanner;
    }

    public void setArticleBanner(List<ArticleBanner> articleBanner) {
        this.articleBanner = articleBanner;
    }

    public List<ArticleVideoBean> getArticleVideo() {
        return articleVideo;
    }

    public void setArticleVideo(List<ArticleVideoBean> articleVideo) {
        this.articleVideo = articleVideo;
    }

    public List<MicroVisionBean> getMicroVision() {
        return microVision;
    }

    public void setMicroVision(List<MicroVisionBean> microVision) {
        this.microVision = microVision;
    }

    public static class ArticleBannerBean {
        /**
         * articleBlock : null
         * articleDetailUrl : http://39.100.55.119:39000/web/page/html/artdetail.html?id=11159
         * articleId : 11159
         * articleLabel : null
         * articleProfile :
         * articleSource : null
         * articleTitle : 罗清宇带领市级领导干部赴国民革命军第八路驻晋办事处旧址参观
         * articleType : 4
         * browseTimes : 4
         * collectedNum : 0
         * examineStatus : 2
         * examineUid : null
         * examineUser : null
         * isCollect : 0
         * isPraise : 0
         * picUrls : http://39.100.61.52:8080/upload/article/20200904/202009040906052059.jpg
         * praisedNum : 0
         * publishDate : 1599179846000
         * quotationTitle : null
         * rejectCause : null
         * subtitle : null
         * topLevel : 6
         * topTime : 1599181594000
         * updateTime : 1599181594000
         * urlType : 1
         * videoCover : null
         * videoDuration : null
         * videoUrl : null
         */

        private Object articleBlock;
        private String articleDetailUrl;
        private int articleId;
        private Object articleLabel;
        private String articleProfile;
        private Object articleSource;
        private String articleTitle;
        private int articleType;
        private int browseTimes;
        private int collectedNum;
        private int examineStatus;
        private Object examineUid;
        private Object examineUser;
        private int isCollect;
        private int isPraise;
        private String picUrls;
        private int praisedNum;
        private long publishDate;
        private Object quotationTitle;
        private Object rejectCause;
        private Object subtitle;
        private int topLevel;
        private long topTime;
        private long updateTime;
        private int urlType;
        private Object videoCover;
        private Object videoDuration;
        private Object videoUrl;

        public Object getArticleBlock() {
            return articleBlock;
        }

        public void setArticleBlock(Object articleBlock) {
            this.articleBlock = articleBlock;
        }

        public String getArticleDetailUrl() {
            return articleDetailUrl;
        }

        public void setArticleDetailUrl(String articleDetailUrl) {
            this.articleDetailUrl = articleDetailUrl;
        }

        public int getArticleId() {
            return articleId;
        }

        public void setArticleId(int articleId) {
            this.articleId = articleId;
        }

        public Object getArticleLabel() {
            return articleLabel;
        }

        public void setArticleLabel(Object articleLabel) {
            this.articleLabel = articleLabel;
        }

        public String getArticleProfile() {
            return articleProfile;
        }

        public void setArticleProfile(String articleProfile) {
            this.articleProfile = articleProfile;
        }

        public Object getArticleSource() {
            return articleSource;
        }

        public void setArticleSource(Object articleSource) {
            this.articleSource = articleSource;
        }

        public String getArticleTitle() {
            return articleTitle;
        }

        public void setArticleTitle(String articleTitle) {
            this.articleTitle = articleTitle;
        }

        public int getArticleType() {
            return articleType;
        }

        public void setArticleType(int articleType) {
            this.articleType = articleType;
        }

        public int getBrowseTimes() {
            return browseTimes;
        }

        public void setBrowseTimes(int browseTimes) {
            this.browseTimes = browseTimes;
        }

        public int getCollectedNum() {
            return collectedNum;
        }

        public void setCollectedNum(int collectedNum) {
            this.collectedNum = collectedNum;
        }

        public int getExamineStatus() {
            return examineStatus;
        }

        public void setExamineStatus(int examineStatus) {
            this.examineStatus = examineStatus;
        }

        public Object getExamineUid() {
            return examineUid;
        }

        public void setExamineUid(Object examineUid) {
            this.examineUid = examineUid;
        }

        public Object getExamineUser() {
            return examineUser;
        }

        public void setExamineUser(Object examineUser) {
            this.examineUser = examineUser;
        }

        public int getIsCollect() {
            return isCollect;
        }

        public void setIsCollect(int isCollect) {
            this.isCollect = isCollect;
        }

        public int getIsPraise() {
            return isPraise;
        }

        public void setIsPraise(int isPraise) {
            this.isPraise = isPraise;
        }

        public String getPicUrls() {
            return picUrls;
        }

        public void setPicUrls(String picUrls) {
            this.picUrls = picUrls;
        }

        public int getPraisedNum() {
            return praisedNum;
        }

        public void setPraisedNum(int praisedNum) {
            this.praisedNum = praisedNum;
        }

        public long getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(long publishDate) {
            this.publishDate = publishDate;
        }

        public Object getQuotationTitle() {
            return quotationTitle;
        }

        public void setQuotationTitle(Object quotationTitle) {
            this.quotationTitle = quotationTitle;
        }

        public Object getRejectCause() {
            return rejectCause;
        }

        public void setRejectCause(Object rejectCause) {
            this.rejectCause = rejectCause;
        }

        public Object getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(Object subtitle) {
            this.subtitle = subtitle;
        }

        public int getTopLevel() {
            return topLevel;
        }

        public void setTopLevel(int topLevel) {
            this.topLevel = topLevel;
        }

        public long getTopTime() {
            return topTime;
        }

        public void setTopTime(long topTime) {
            this.topTime = topTime;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public int getUrlType() {
            return urlType;
        }

        public void setUrlType(int urlType) {
            this.urlType = urlType;
        }

        public Object getVideoCover() {
            return videoCover;
        }

        public void setVideoCover(Object videoCover) {
            this.videoCover = videoCover;
        }

        public Object getVideoDuration() {
            return videoDuration;
        }

        public void setVideoDuration(Object videoDuration) {
            this.videoDuration = videoDuration;
        }

        public Object getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(Object videoUrl) {
            this.videoUrl = videoUrl;
        }
    }

    public static class ArticleVideoBean {
        /**
         * articleBlock : null
         * articleDetailUrl : null
         * articleId : 8088
         * articleLabel : null
         * articleProfile :
         * articleSource : 中共娄烦县委组织部
         * articleTitle : 娄烦县开展纪念“五四”运动101周年主题团日活动
         * articleType : 3
         * browseTimes : 0
         * collectedNum : 0
         * examineStatus : 2
         * examineUid : null
         * examineUser : null
         * isCollect : 0
         * isPraise : 0
         * picUrls : null
         * praisedNum : 0
         * publishDate : 1588747592000
         * quotationTitle : null
         * rejectCause : null
         * subtitle : null
         * topLevel : 6
         * topTime : 1590135713000
         * updateTime : 1590135712000
         * urlType : 2
         * videoCover : http://39.100.61.52:8080/upload/article/20200506/202005061446324206.jpg
         * videoDuration : 56
         * videoUrl : http://39.100.4.85:8080/upload/article/chunk/c0c531f5794a03f4f725cf45f75cc753/202005061446328059.mp4
         */

        private Object articleBlock;
        private Object articleDetailUrl;
        private int articleId;
        private Object articleLabel;
        private String articleProfile;
        private String articleSource;
        private String articleTitle;
        private int articleType;
        private int browseTimes;
        private int collectedNum;
        private int examineStatus;
        private Object examineUid;
        private Object examineUser;
        private int isCollect;
        private int isPraise;
        private Object picUrls;
        private int praisedNum;
        private long publishDate;
        private Object quotationTitle;
        private Object rejectCause;
        private Object subtitle;
        private int topLevel;
        private long topTime;
        private long updateTime;
        private int urlType;
        private String videoCover;
        private int videoDuration;
        private String videoUrl;

        public Object getArticleBlock() {
            return articleBlock;
        }

        public void setArticleBlock(Object articleBlock) {
            this.articleBlock = articleBlock;
        }

        public Object getArticleDetailUrl() {
            return articleDetailUrl;
        }

        public void setArticleDetailUrl(Object articleDetailUrl) {
            this.articleDetailUrl = articleDetailUrl;
        }

        public int getArticleId() {
            return articleId;
        }

        public void setArticleId(int articleId) {
            this.articleId = articleId;
        }

        public Object getArticleLabel() {
            return articleLabel;
        }

        public void setArticleLabel(Object articleLabel) {
            this.articleLabel = articleLabel;
        }

        public String getArticleProfile() {
            return articleProfile;
        }

        public void setArticleProfile(String articleProfile) {
            this.articleProfile = articleProfile;
        }

        public String getArticleSource() {
            return articleSource;
        }

        public void setArticleSource(String articleSource) {
            this.articleSource = articleSource;
        }

        public String getArticleTitle() {
            return articleTitle;
        }

        public void setArticleTitle(String articleTitle) {
            this.articleTitle = articleTitle;
        }

        public int getArticleType() {
            return articleType;
        }

        public void setArticleType(int articleType) {
            this.articleType = articleType;
        }

        public int getBrowseTimes() {
            return browseTimes;
        }

        public void setBrowseTimes(int browseTimes) {
            this.browseTimes = browseTimes;
        }

        public int getCollectedNum() {
            return collectedNum;
        }

        public void setCollectedNum(int collectedNum) {
            this.collectedNum = collectedNum;
        }

        public int getExamineStatus() {
            return examineStatus;
        }

        public void setExamineStatus(int examineStatus) {
            this.examineStatus = examineStatus;
        }

        public Object getExamineUid() {
            return examineUid;
        }

        public void setExamineUid(Object examineUid) {
            this.examineUid = examineUid;
        }

        public Object getExamineUser() {
            return examineUser;
        }

        public void setExamineUser(Object examineUser) {
            this.examineUser = examineUser;
        }

        public int getIsCollect() {
            return isCollect;
        }

        public void setIsCollect(int isCollect) {
            this.isCollect = isCollect;
        }

        public int getIsPraise() {
            return isPraise;
        }

        public void setIsPraise(int isPraise) {
            this.isPraise = isPraise;
        }

        public Object getPicUrls() {
            return picUrls;
        }

        public void setPicUrls(Object picUrls) {
            this.picUrls = picUrls;
        }

        public int getPraisedNum() {
            return praisedNum;
        }

        public void setPraisedNum(int praisedNum) {
            this.praisedNum = praisedNum;
        }

        public long getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(long publishDate) {
            this.publishDate = publishDate;
        }

        public Object getQuotationTitle() {
            return quotationTitle;
        }

        public void setQuotationTitle(Object quotationTitle) {
            this.quotationTitle = quotationTitle;
        }

        public Object getRejectCause() {
            return rejectCause;
        }

        public void setRejectCause(Object rejectCause) {
            this.rejectCause = rejectCause;
        }

        public Object getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(Object subtitle) {
            this.subtitle = subtitle;
        }

        public int getTopLevel() {
            return topLevel;
        }

        public void setTopLevel(int topLevel) {
            this.topLevel = topLevel;
        }

        public long getTopTime() {
            return topTime;
        }

        public void setTopTime(long topTime) {
            this.topTime = topTime;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public int getUrlType() {
            return urlType;
        }

        public void setUrlType(int urlType) {
            this.urlType = urlType;
        }

        public String getVideoCover() {
            return videoCover;
        }

        public void setVideoCover(String videoCover) {
            this.videoCover = videoCover;
        }

        public int getVideoDuration() {
            return videoDuration;
        }

        public void setVideoDuration(int videoDuration) {
            this.videoDuration = videoDuration;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }

    public static class MicroVisionBean {
        /**
         * bgmName : 朴树 - 平凡之路
         * createTime : 1564467750000
         * examineUid : null
         * examineUser : null
         * hmUser : null
         * isFollowed : 0
         * isPraise : 0
         * rejectCause : null
         * topLevel : null
         * topTime : null
         * userHeadImg : http://39.100.61.52:8080/upload/user/headImg/201907301325405847.jpg?1564464347151
         * userId : 26
         * userName : 152****1223
         * videoCover : http://39.100.61.52:8080/upload/micro/26/20190730/201907301422300043.jpg
         * videoDuration : 15
         * visionBrowseTimes : 674
         * visionExamineStatus : 2
         * visionFileUrl : http://39.100.4.85:8080/upload/micro/26/20190730/201907301422309949.mp4
         * visionId : 61
         * visionPraisedNum : 13
         * visionTitle : null
         * visionType : 2
         */

        private String bgmName;
        private long createTime;
        private Object examineUid;
        private Object examineUser;
        private Object hmUser;
        private int isFollowed;
        private int isPraise;
        private Object rejectCause;
        private Object topLevel;
        private Object topTime;
        private String userHeadImg;
        private int userId;
        private String userName;
        private String videoCover;
        private int videoDuration;
        private int visionBrowseTimes;
        private int visionExamineStatus;
        private String visionFileUrl;
        private int visionId;
        private int visionPraisedNum;
        private Object visionTitle;
        private int visionType;

        public String getBgmName() {
            return bgmName;
        }

        public void setBgmName(String bgmName) {
            this.bgmName = bgmName;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public Object getExamineUid() {
            return examineUid;
        }

        public void setExamineUid(Object examineUid) {
            this.examineUid = examineUid;
        }

        public Object getExamineUser() {
            return examineUser;
        }

        public void setExamineUser(Object examineUser) {
            this.examineUser = examineUser;
        }

        public Object getHmUser() {
            return hmUser;
        }

        public void setHmUser(Object hmUser) {
            this.hmUser = hmUser;
        }

        public int getIsFollowed() {
            return isFollowed;
        }

        public void setIsFollowed(int isFollowed) {
            this.isFollowed = isFollowed;
        }

        public int getIsPraise() {
            return isPraise;
        }

        public void setIsPraise(int isPraise) {
            this.isPraise = isPraise;
        }

        public Object getRejectCause() {
            return rejectCause;
        }

        public void setRejectCause(Object rejectCause) {
            this.rejectCause = rejectCause;
        }

        public Object getTopLevel() {
            return topLevel;
        }

        public void setTopLevel(Object topLevel) {
            this.topLevel = topLevel;
        }

        public Object getTopTime() {
            return topTime;
        }

        public void setTopTime(Object topTime) {
            this.topTime = topTime;
        }

        public String getUserHeadImg() {
            return userHeadImg;
        }

        public void setUserHeadImg(String userHeadImg) {
            this.userHeadImg = userHeadImg;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getVideoCover() {
            return videoCover;
        }

        public void setVideoCover(String videoCover) {
            this.videoCover = videoCover;
        }

        public int getVideoDuration() {
            return videoDuration;
        }

        public void setVideoDuration(int videoDuration) {
            this.videoDuration = videoDuration;
        }

        public int getVisionBrowseTimes() {
            return visionBrowseTimes;
        }

        public void setVisionBrowseTimes(int visionBrowseTimes) {
            this.visionBrowseTimes = visionBrowseTimes;
        }

        public int getVisionExamineStatus() {
            return visionExamineStatus;
        }

        public void setVisionExamineStatus(int visionExamineStatus) {
            this.visionExamineStatus = visionExamineStatus;
        }

        public String getVisionFileUrl() {
            return visionFileUrl;
        }

        public void setVisionFileUrl(String visionFileUrl) {
            this.visionFileUrl = visionFileUrl;
        }

        public int getVisionId() {
            return visionId;
        }

        public void setVisionId(int visionId) {
            this.visionId = visionId;
        }

        public int getVisionPraisedNum() {
            return visionPraisedNum;
        }

        public void setVisionPraisedNum(int visionPraisedNum) {
            this.visionPraisedNum = visionPraisedNum;
        }

        public Object getVisionTitle() {
            return visionTitle;
        }

        public void setVisionTitle(Object visionTitle) {
            this.visionTitle = visionTitle;
        }

        public int getVisionType() {
            return visionType;
        }

        public void setVisionType(int visionType) {
            this.visionType = visionType;
        }
    }

}
