package com.typartybuilding.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-19
 * @describe
 */
public class LiveRoomBean implements Serializable {



        /**
         * pageCount : 1
         * pageEnd : 1
         * pageNo : 1
         * pageSize : 10
         * pageStart : 0
         * rows : [{"account":null,"confId":"1150700","createTime":1599192219000,"createrId":3,"createrName":"admin","examineStatus":2,"examineUid":3,"examineUser":"admin","heatNum":0,"liveCover":"http://39.100.61.52:8080/upload/live/3/202009041429510018.jpg","liveStatus":1,"liveType":1,"rejectCause":null,"themeId":2,"titleName":"测试的直播数据01","topTime":null,"updateTime":1600311972000}]
         * totalCount : 1
         */

        private int pageCount;
        private int pageEnd;
        private int pageNo;
        private int pageSize;
        private int pageStart;
        private int totalCount;
        private List<RowsBean> rows;

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public int getPageEnd() {
            return pageEnd;
        }

        public void setPageEnd(int pageEnd) {
            this.pageEnd = pageEnd;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageStart() {
            return pageStart;
        }

        public void setPageStart(int pageStart) {
            this.pageStart = pageStart;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public List<RowsBean> getRows() {
            return rows;
        }

        public void setRows(List<RowsBean> rows) {
            this.rows = rows;
        }

        public static class RowsBean {
            /**
             * account : null
             * confId : 1150700
             * createTime : 1599192219000
             * createrId : 3
             * createrName : admin
             * examineStatus : 2
             * examineUid : 3
             * examineUser : admin
             * heatNum : 0
             * liveCover : http://39.100.61.52:8080/upload/live/3/202009041429510018.jpg
             * liveStatus : 1
             * liveType : 1
             * rejectCause : null
             * themeId : 2
             * titleName : 测试的直播数据01
             * topTime : null
             * updateTime : 1600311972000
             */

            private Object account;
            private String confId;
            private long createTime;
            private int createrId;
            private String createrName;
            private int examineStatus;
            private int examineUid;
            private String examineUser;
            private int heatNum;
            private String liveCover;
            private int liveStatus;
            private int liveType;
            private Object rejectCause;
            private int themeId;
            private String titleName;
            private Object topTime;
            private long updateTime;

            public Object getAccount() {
                return account;
            }

            public void setAccount(Object account) {
                this.account = account;
            }

            public String getConfId() {
                return confId;
            }

            public void setConfId(String confId) {
                this.confId = confId;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public int getCreaterId() {
                return createrId;
            }

            public void setCreaterId(int createrId) {
                this.createrId = createrId;
            }

            public String getCreaterName() {
                return createrName;
            }

            public void setCreaterName(String createrName) {
                this.createrName = createrName;
            }

            public int getExamineStatus() {
                return examineStatus;
            }

            public void setExamineStatus(int examineStatus) {
                this.examineStatus = examineStatus;
            }

            public int getExamineUid() {
                return examineUid;
            }

            public void setExamineUid(int examineUid) {
                this.examineUid = examineUid;
            }

            public String getExamineUser() {
                return examineUser;
            }

            public void setExamineUser(String examineUser) {
                this.examineUser = examineUser;
            }

            public int getHeatNum() {
                return heatNum;
            }

            public void setHeatNum(int heatNum) {
                this.heatNum = heatNum;
            }

            public String getLiveCover() {
                return liveCover;
            }

            public void setLiveCover(String liveCover) {
                this.liveCover = liveCover;
            }

            public int getLiveStatus() {
                return liveStatus;
            }

            public void setLiveStatus(int liveStatus) {
                this.liveStatus = liveStatus;
            }

            public int getLiveType() {
                return liveType;
            }

            public void setLiveType(int liveType) {
                this.liveType = liveType;
            }

            public Object getRejectCause() {
                return rejectCause;
            }

            public void setRejectCause(Object rejectCause) {
                this.rejectCause = rejectCause;
            }

            public int getThemeId() {
                return themeId;
            }

            public void setThemeId(int themeId) {
                this.themeId = themeId;
            }

            public String getTitleName() {
                return titleName;
            }

            public void setTitleName(String titleName) {
                this.titleName = titleName;
            }

            public Object getTopTime() {
                return topTime;
            }

            public void setTopTime(Object topTime) {
                this.topTime = topTime;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }
        }

}
