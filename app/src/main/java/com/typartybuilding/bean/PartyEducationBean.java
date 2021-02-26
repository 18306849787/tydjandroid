package com.typartybuilding.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-15
 * @describe
 */
public class PartyEducationBean implements MultiItemEntity {


        /**
         * articleLabel : 0
         * articleLabelName : 置顶
         * articleType : 6
         * rows : [{"articleBlock":null,"articleDetailUrl":null,"articleId":11144,"articleLabel":2,"articleProfile":"","articleSource":null,"articleTitle":"市委理论学习中心组举行学习会_学习《习近平谈治国理论》第三卷_罗清宇主持并讲话","articleType":6,"browseTimes":1,"collectedNum":0,"examineStatus":2,"examineUid":null,"examineUser":null,"isCollect":0,"isPraise":0,"picUrls":null,"praisedNum":0,"publishDate":1599116992000,"quotationTitle":null,"rejectCause":null,"subtitle":null,"topLevel":1,"topTime":1599117018000,"updateTime":1599616475000,"urlType":2,"videoCover":"http://39.100.61.52:8080/upload/article/20200903/202009031509528608.jpg","videoDuration":231,"videoUrl":"http://39.100.4.85:8080/upload/article/chunk/08d1540efa2cfa8f621a967094cbf369/202009031509528565.mp4"}]
         */
        @Override
        public int getItemType() {
            if (articleLabel == 0){
                return articleLabel;
            }else {
                return 1;
            }

        }


        private int articleLabel;
        private String articleLabelName;
        private int articleType;
        private List<RowsBean> rows;

        public int getArticleLabel() {
            return articleLabel;
        }

        public void setArticleLabel(int articleLabel) {
            this.articleLabel = articleLabel;
        }

        public String getArticleLabelName() {
            return articleLabelName;
        }

        public void setArticleLabelName(String articleLabelName) {
            this.articleLabelName = articleLabelName;
        }

        public int getArticleType() {
            return articleType;
        }

        public void setArticleType(int articleType) {
            this.articleType = articleType;
        }

        public List<RowsBean> getRows() {
            return rows;
        }

        public void setRows(List<RowsBean> rows) {
            this.rows = rows;
        }


    public static class RowsBean {
            /**
             * articleBlock : null
             * articleDetailUrl : null
             * articleId : 11144
             * articleLabel : 2
             * articleProfile :
             * articleSource : null
             * articleTitle : 市委理论学习中心组举行学习会_学习《习近平谈治国理论》第三卷_罗清宇主持并讲话
             * articleType : 6
             * browseTimes : 1
             * collectedNum : 0
             * examineStatus : 2
             * examineUid : null
             * examineUser : null
             * isCollect : 0
             * isPraise : 0
             * picUrls : null
             * praisedNum : 0
             * publishDate : 1599116992000
             * quotationTitle : null
             * rejectCause : null
             * subtitle : null
             * topLevel : 1
             * topTime : 1599117018000
             * updateTime : 1599616475000
             * urlType : 2
             * videoCover : http://39.100.61.52:8080/upload/article/20200903/202009031509528608.jpg
             * videoDuration : 231
             * videoUrl : http://39.100.4.85:8080/upload/article/chunk/08d1540efa2cfa8f621a967094cbf369/202009031509528565.mp4
             */

            private Object articleBlock;
            private String articleDetailUrl;
            private int articleId;
            private int articleLabel;
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

            public int getArticleLabel() {
                return articleLabel;
            }

            public void setArticleLabel(int articleLabel) {
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

}
