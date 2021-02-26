package com.typartybuilding.bean;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-08
 * @describe
 */
public class PoliciesBean {

        /**
         * pageCount : 1
         * pageEnd : 1
         * pageNo : 1
         * pageSize : 20
         * pageStart : 0
         * rows : [{"createTime":1599475680000,"delState":0,"examineStatus":2,"examineUid":1,"examineUser":"admin","rejectCause":null,"statuteContent":null,"statuteId":2,"statuteTitle":"国务院关于中新广州知识城  总体发展规划（2020\u20142035年）的批复","statuteUrl":"http://39.100.55.119:39000/web/page/html/statute.html?id=2","topTime":null,"updateTime":1599475680000}]
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
             * createTime : 1599475680000
             * delState : 0
             * examineStatus : 2
             * examineUid : 1
             * examineUser : admin
             * rejectCause : null
             * statuteContent : null
             * statuteId : 2
             * statuteTitle : 国务院关于中新广州知识城  总体发展规划（2020—2035年）的批复
             * statuteUrl : http://39.100.55.119:39000/web/page/html/statute.html?id=2
             * topTime : null
             * updateTime : 1599475680000
             */

            private long createTime;
            private int delState;
            private int examineStatus;
            private int examineUid;
            private String examineUser;
            private Object rejectCause;
            private Object statuteContent;
            private int statuteId;
            private String statuteTitle;
            private String statuteUrl;
            private Object topTime;
            private long updateTime;

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public int getDelState() {
                return delState;
            }

            public void setDelState(int delState) {
                this.delState = delState;
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

            public Object getRejectCause() {
                return rejectCause;
            }

            public void setRejectCause(Object rejectCause) {
                this.rejectCause = rejectCause;
            }

            public Object getStatuteContent() {
                return statuteContent;
            }

            public void setStatuteContent(Object statuteContent) {
                this.statuteContent = statuteContent;
            }

            public int getStatuteId() {
                return statuteId;
            }

            public void setStatuteId(int statuteId) {
                this.statuteId = statuteId;
            }

            public String getStatuteTitle() {
                return statuteTitle;
            }

            public void setStatuteTitle(String statuteTitle) {
                this.statuteTitle = statuteTitle;
            }

            public String getStatuteUrl() {
                return statuteUrl;
            }

            public void setStatuteUrl(String statuteUrl) {
                this.statuteUrl = statuteUrl;
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
