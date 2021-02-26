package com.typartybuilding.bean.pblibrary;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-17
 * @describe
 */
public class SysMessageBean {


        /**
         * pageCount : 1
         * pageEnd : 1
         * pageNo : 1
         * pageSize : 10
         * pageStart : 0
         * rows : [{"createTime":null,"delState":null,"examineStatus":null,"examineUid":null,"examineUser":null,"messageCode":null,"messageContent":"明天xxx将组织优秀党员，xxxx","messageId":null,"messageStatus":null,"messageTargets":null,"messageTitle":"系统通知","messageType":null,"rejectCause":null,"sendTime":1599555543000}]
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

        public static class RowsBean implements MultiItemEntity {
            /**
             * createTime : null
             * delState : null
             * examineStatus : null
             * examineUid : null
             * examineUser : null
             * messageCode : null
             * messageContent : 明天xxx将组织优秀党员，xxxx
             * messageId : null
             * messageStatus : null
             * messageTargets : null
             * messageTitle : 系统通知
             * messageType : null
             * rejectCause : null
             * sendTime : 1599555543000
             */

            private Object createTime;
            private Object delState;
            private Object examineStatus;
            private Object examineUid;
            private Object examineUser;
            private Object messageCode;
            private String messageContent;
            private Object messageId;
            private Object messageStatus;
            private Object messageTargets;
            private String messageTitle;
            private int messageType;
            private Object rejectCause;
            private long sendTime;

            public Object getCreateTime() {
                return createTime;
            }

            public void setCreateTime(Object createTime) {
                this.createTime = createTime;
            }

            public Object getDelState() {
                return delState;
            }

            public void setDelState(Object delState) {
                this.delState = delState;
            }

            public Object getExamineStatus() {
                return examineStatus;
            }

            public void setExamineStatus(Object examineStatus) {
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

            public Object getMessageCode() {
                return messageCode;
            }

            public void setMessageCode(Object messageCode) {
                this.messageCode = messageCode;
            }

            public String getMessageContent() {
                return messageContent;
            }

            public void setMessageContent(String messageContent) {
                this.messageContent = messageContent;
            }

            public Object getMessageId() {
                return messageId;
            }

            public void setMessageId(Object messageId) {
                this.messageId = messageId;
            }

            public Object getMessageStatus() {
                return messageStatus;
            }

            public void setMessageStatus(Object messageStatus) {
                this.messageStatus = messageStatus;
            }

            public Object getMessageTargets() {
                return messageTargets;
            }

            public void setMessageTargets(Object messageTargets) {
                this.messageTargets = messageTargets;
            }

            public String getMessageTitle() {
                return messageTitle;
            }

            public void setMessageTitle(String messageTitle) {
                this.messageTitle = messageTitle;
            }

            public int getMessageType() {
                return messageType;
            }

            public void setMessageType(int messageType) {
                this.messageType = messageType;
            }

            public Object getRejectCause() {
                return rejectCause;
            }

            public void setRejectCause(Object rejectCause) {
                this.rejectCause = rejectCause;
            }

            public long getSendTime() {
                return sendTime;
            }

            public void setSendTime(long sendTime) {
                this.sendTime = sendTime;
            }

            @Override
            public int getItemType() {
                return messageType;
            }
        }

}
