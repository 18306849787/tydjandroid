package com.typartybuilding.bean;

import com.typartybuilding.gsondata.MicroVideo;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-08
 * @describe
 */
public class MeShowBean {

    /**
     * pageCount : 27
     * pageEnd : 20
     * pageNo : 1
     * pageSize : 20
     * pageStart : 0
     * rows : [{"bgmName":"","createTime":1599470132000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200907/202009071715323489.jpg","videoDuration":23,"visionBrowseTimes":2,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200907/202009071715321157.mp4","visionId":2253,"visionPraisedNum":0,"visionTitle":"\u201c中国人民抗日战争胜利75周年\u201d","visionType":2},{"bgmName":"","createTime":1599469832000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200907/202009071710324667.jpg","videoDuration":18,"visionBrowseTimes":1,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200907/202009071710321560.mp4","visionId":2252,"visionPraisedNum":0,"visionTitle":"习近平\u201c两山理论\u201d15周年","visionType":2},{"bgmName":"","createTime":1599467985000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200907/202009071639446088.jpg","videoDuration":18,"visionBrowseTimes":1,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200907/202009071639447633.mp4","visionId":2249,"visionPraisedNum":0,"visionTitle":"和平不易 向抗战老兵致敬","visionType":2},{"bgmName":"","createTime":1599463378000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200907/202009071522580056.jpg","videoDuration":18,"visionBrowseTimes":5,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200907/202009071522585717.mp4","visionId":2248,"visionPraisedNum":1,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599446788000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200907/202009071046280241.jpg","videoDuration":29,"visionBrowseTimes":6,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200907/202009071046280885.mp4","visionId":2247,"visionPraisedNum":0,"visionTitle":"共同 迎接世界更加美好的未来","visionType":2},{"bgmName":"","createTime":1599445436000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200907/202009071023560246.jpg","videoDuration":29,"visionBrowseTimes":3,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200907/202009071023567679.mp4","visionId":2246,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599445058000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200907/202009071017376432.jpg","videoDuration":29,"visionBrowseTimes":3,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200907/202009071017371761.mp4","visionId":2245,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599203076000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200904/202009041504360750.jpg","videoDuration":16,"visionBrowseTimes":13,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200904/202009041504369883.mp4","visionId":2244,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599202229000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200904/202009041450283826.jpg","videoDuration":8,"visionBrowseTimes":11,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200904/202009041450285366.mp4","visionId":2243,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599201051000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200904/202009041430502465.jpg","videoDuration":17,"visionBrowseTimes":11,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200904/202009041430500591.mp4","visionId":2242,"visionPraisedNum":0,"visionTitle":"纪念抗战胜利75周年纪念日","visionType":2},{"bgmName":"","createTime":1599185891000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200904/202009041018114485.jpg","videoDuration":29,"visionBrowseTimes":11,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200904/202009041018106983.mp4","visionId":2241,"visionPraisedNum":0,"visionTitle":"浪费可耻、节约为荣，拒绝餐饮浪费","visionType":2},{"bgmName":"","createTime":1599183679000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200904/202009040941195496.jpg","videoDuration":19,"visionBrowseTimes":12,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200904/202009040941192546.mp4","visionId":2239,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599183010000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200904/202009040930092828.jpg","videoDuration":17,"visionBrowseTimes":11,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200904/202009040930098316.mp4","visionId":2237,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599182714000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269","userId":13,"userName":"137****7385","videoCover":"http://39.100.61.52:8080/upload/micro/13/20200904/202009040925133991.jpg","videoDuration":11,"visionBrowseTimes":12,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/13/20200904/202009040925139964.mp4","visionId":2236,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599180816000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/9_head_img.jpg?1590648780598","userId":9,"userName":"187****0203","videoCover":"http://39.100.61.52:8080/upload/micro/9/20200904/202009040853366795.jpg","videoDuration":18,"visionBrowseTimes":11,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/9/20200904/202009040853351862.mp4","visionId":2233,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599137252000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/9_head_img.jpg?1590648780598","userId":9,"userName":"187****0203","videoCover":"http://39.100.61.52:8080/upload/micro/9/20200903/202009032047329349.jpg","videoDuration":14,"visionBrowseTimes":10,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/9/20200903/202009032047324280.mp4","visionId":2231,"visionPraisedNum":0,"visionTitle":"习近平向中国人民警察队伍授旗并致训词。","visionType":2},{"bgmName":"","createTime":1599135957000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/9_head_img.jpg?1590648780598","userId":9,"userName":"187****0203","videoCover":"http://39.100.61.52:8080/upload/micro/9/20200903/202009032025569996.jpg","videoDuration":14,"visionBrowseTimes":6,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/9/20200903/202009032025567917.mp4","visionId":2228,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599135824000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/9_head_img.jpg?1590648780598","userId":9,"userName":"187****0203","videoCover":"http://39.100.61.52:8080/upload/micro/9/20200903/202009032023438239.jpg","videoDuration":16,"visionBrowseTimes":6,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/9/20200903/202009032023431498.mp4","visionId":2226,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599135777000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/9_head_img.jpg?1590648780598","userId":9,"userName":"187****0203","videoCover":"http://39.100.61.52:8080/upload/micro/9/20200903/202009032022560722.jpg","videoDuration":7,"visionBrowseTimes":5,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/9/20200903/202009032022561641.mp4","visionId":2225,"visionPraisedNum":0,"visionTitle":"","visionType":2},{"bgmName":"","createTime":1599135744000,"examineUid":null,"examineUser":null,"hmUser":null,"isFollowed":1,"isPraise":0,"rejectCause":null,"topLevel":null,"topTime":null,"userHeadImg":"http://39.100.61.52:8080/upload/user/headImg/9_head_img.jpg?1590648780598","userId":9,"userName":"187****0203","videoCover":"http://39.100.61.52:8080/upload/micro/9/20200903/202009032022240391.jpg","videoDuration":16,"visionBrowseTimes":5,"visionExamineStatus":2,"visionFileUrl":"http://39.100.4.85:8080/upload/micro/9/20200903/202009032022242012.mp4","visionId":2224,"visionPraisedNum":0,"visionTitle":"","visionType":2}]
     * totalCount : 521
     */

    private int pageCount;
    private int pageEnd;
    private int pageNo;
    private int pageSize;
    private int pageStart;
    private int totalCount;
    private List<MicroVideo> rows;

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

    public List<MicroVideo> getRows() {
        return rows;
    }

    public void setRows(List<MicroVideo> rows) {
        this.rows = rows;
    }

//    public static class RowsBean {
//        /**
//         * bgmName :
//         * createTime : 1599470132000
//         * examineUid : null
//         * examineUser : null
//         * hmUser : null
//         * isFollowed : 1
//         * isPraise : 0
//         * rejectCause : null
//         * topLevel : null
//         * topTime : null
//         * userHeadImg : http://39.100.61.52:8080/upload/user/headImg/13_head_img.jpg?1598365004269
//         * userId : 13
//         * userName : 137****7385
//         * videoCover : http://39.100.61.52:8080/upload/micro/13/20200907/202009071715323489.jpg
//         * videoDuration : 23
//         * visionBrowseTimes : 2
//         * visionExamineStatus : 2
//         * visionFileUrl : http://39.100.4.85:8080/upload/micro/13/20200907/202009071715321157.mp4
//         * visionId : 2253
//         * visionPraisedNum : 0
//         * visionTitle : “中国人民抗日战争胜利75周年”
//         * visionType : 2
//         */
//
//        private String bgmName;
//        private long createTime;
//        private Object examineUid;
//        private Object examineUser;
//        private Object hmUser;
//        private int isFollowed;
//        private int isPraise;
//        private Object rejectCause;
//        private Object topLevel;
//        private Object topTime;
//        private String userHeadImg;
//        private int userId;
//        private String userName;
//        private String videoCover;
//        private int videoDuration;
//        private int visionBrowseTimes;
//        private int visionExamineStatus;
//        private String visionFileUrl;
//        private int visionId;
//        private int visionPraisedNum;
//        private String visionTitle;
//        private int visionType;
//
//        public String getBgmName() {
//            return bgmName;
//        }
//
//        public void setBgmName(String bgmName) {
//            this.bgmName = bgmName;
//        }
//
//        public long getCreateTime() {
//            return createTime;
//        }
//
//        public void setCreateTime(long createTime) {
//            this.createTime = createTime;
//        }
//
//        public Object getExamineUid() {
//            return examineUid;
//        }
//
//        public void setExamineUid(Object examineUid) {
//            this.examineUid = examineUid;
//        }
//
//        public Object getExamineUser() {
//            return examineUser;
//        }
//
//        public void setExamineUser(Object examineUser) {
//            this.examineUser = examineUser;
//        }
//
//        public Object getHmUser() {
//            return hmUser;
//        }
//
//        public void setHmUser(Object hmUser) {
//            this.hmUser = hmUser;
//        }
//
//        public int getIsFollowed() {
//            return isFollowed;
//        }
//
//        public void setIsFollowed(int isFollowed) {
//            this.isFollowed = isFollowed;
//        }
//
//        public int getIsPraise() {
//            return isPraise;
//        }
//
//        public void setIsPraise(int isPraise) {
//            this.isPraise = isPraise;
//        }
//
//        public Object getRejectCause() {
//            return rejectCause;
//        }
//
//        public void setRejectCause(Object rejectCause) {
//            this.rejectCause = rejectCause;
//        }
//
//        public Object getTopLevel() {
//            return topLevel;
//        }
//
//        public void setTopLevel(Object topLevel) {
//            this.topLevel = topLevel;
//        }
//
//        public Object getTopTime() {
//            return topTime;
//        }
//
//        public void setTopTime(Object topTime) {
//            this.topTime = topTime;
//        }
//
//        public String getUserHeadImg() {
//            return userHeadImg;
//        }
//
//        public void setUserHeadImg(String userHeadImg) {
//            this.userHeadImg = userHeadImg;
//        }
//
//        public int getUserId() {
//            return userId;
//        }
//
//        public void setUserId(int userId) {
//            this.userId = userId;
//        }
//
//        public String getUserName() {
//            return userName;
//        }
//
//        public void setUserName(String userName) {
//            this.userName = userName;
//        }
//
//        public String getVideoCover() {
//            return videoCover;
//        }
//
//        public void setVideoCover(String videoCover) {
//            this.videoCover = videoCover;
//        }
//
//        public int getVideoDuration() {
//            return videoDuration;
//        }
//
//        public void setVideoDuration(int videoDuration) {
//            this.videoDuration = videoDuration;
//        }
//
//        public int getVisionBrowseTimes() {
//            return visionBrowseTimes;
//        }
//
//        public void setVisionBrowseTimes(int visionBrowseTimes) {
//            this.visionBrowseTimes = visionBrowseTimes;
//        }
//
//        public int getVisionExamineStatus() {
//            return visionExamineStatus;
//        }
//
//        public void setVisionExamineStatus(int visionExamineStatus) {
//            this.visionExamineStatus = visionExamineStatus;
//        }
//
//        public String getVisionFileUrl() {
//            return visionFileUrl;
//        }
//
//        public void setVisionFileUrl(String visionFileUrl) {
//            this.visionFileUrl = visionFileUrl;
//        }
//
//        public int getVisionId() {
//            return visionId;
//        }
//
//        public void setVisionId(int visionId) {
//            this.visionId = visionId;
//        }
//
//        public int getVisionPraisedNum() {
//            return visionPraisedNum;
//        }
//
//        public void setVisionPraisedNum(int visionPraisedNum) {
//            this.visionPraisedNum = visionPraisedNum;
//        }
//
//        public String getVisionTitle() {
//            return visionTitle;
//        }
//
//        public void setVisionTitle(String visionTitle) {
//            this.visionTitle = visionTitle;
//        }
//
//        public int getVisionType() {
//            return visionType;
//        }
//
//        public void setVisionType(int visionType) {
//            this.visionType = visionType;
//        }
//    }
}

