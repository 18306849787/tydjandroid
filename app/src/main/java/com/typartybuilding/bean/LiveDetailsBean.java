package com.typartybuilding.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-29
 * @describe
 */
public class LiveDetailsBean implements Serializable {


        /**
         * allowguest : 1
         * author : kedacom
         * authority : reserve
         * content : reserve
         * heatNum : 2
         * host : http://101.231.162.247
         * modifyTime : 1996968305
         * name : 001号直播间
         * needStrongAuth : false
         * proList : [{"SecStream":false,"id":0,"indexList":[{"hlsIndex":"/api/v1/vrs/upstream/be5c2368-e1b8-11ea-a4a8-ecd68a2efa35/hlsfile/2016/03/24/20160324032559_882001/1280_720/playlist.m3u8","resolution":"1280_720"},{"hlsIndex":"/api/v1/vrs/upstream/be5c2368-e1b8-11ea-a4a8-ecd68a2efa35/hlsfile/2016/03/24/20160324032559_882001/704_576/playlist.m3u8","resolution":"704_576"},{"hlsIndex":"/api/v1/vrs/upstream/be5c2368-e1b8-11ea-a4a8-ecd68a2efa35/hlsfile/2016/03/24/20160324032559_882001/352_288/playlist.m3u8","resolution":"352_288"}],"name":"视频0","resolutionNum":3}]
         * published : false
         * resolutionList : ["1280_720","704_576","352_288"]
         * sso_token : ee847aec-8c00-48d2-ab42-2d9b6fe51f69
         * streamNum : 1
         * themeId : 8
         * type : 1
         * userdomainmoid : hwu4u8rfwt7ub2marz0lvij6
         */

        private int allowguest;
        private String author;
        private String authority;
        private String content;
        private int heatNum;
        private String host;
        private String modifyTime;
        private String name;
        private boolean needStrongAuth;
        private boolean published;
        private String sso_token;
        private int streamNum;
        private int themeId;
        private int type;
        private String userdomainmoid;
        private List<ProListBean> proList;
        private List<String> resolutionList;

        public int getAllowguest() {
            return allowguest;
        }

        public void setAllowguest(int allowguest) {
            this.allowguest = allowguest;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getAuthority() {
            return authority;
        }

        public void setAuthority(String authority) {
            this.authority = authority;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getHeatNum() {
            return heatNum;
        }

        public void setHeatNum(int heatNum) {
            this.heatNum = heatNum;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(String modifyTime) {
            this.modifyTime = modifyTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isNeedStrongAuth() {
            return needStrongAuth;
        }

        public void setNeedStrongAuth(boolean needStrongAuth) {
            this.needStrongAuth = needStrongAuth;
        }

        public boolean isPublished() {
            return published;
        }

        public void setPublished(boolean published) {
            this.published = published;
        }

        public String getSso_token() {
            return sso_token;
        }

        public void setSso_token(String sso_token) {
            this.sso_token = sso_token;
        }

        public int getStreamNum() {
            return streamNum;
        }

        public void setStreamNum(int streamNum) {
            this.streamNum = streamNum;
        }

        public int getThemeId() {
            return themeId;
        }

        public void setThemeId(int themeId) {
            this.themeId = themeId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUserdomainmoid() {
            return userdomainmoid;
        }

        public void setUserdomainmoid(String userdomainmoid) {
            this.userdomainmoid = userdomainmoid;
        }

        public List<ProListBean> getProList() {
            return proList;
        }

        public void setProList(List<ProListBean> proList) {
            this.proList = proList;
        }

        public List<String> getResolutionList() {
            return resolutionList;
        }

        public void setResolutionList(List<String> resolutionList) {
            this.resolutionList = resolutionList;
        }

        public static class ProListBean implements Serializable {
            /**
             * SecStream : false
             * id : 0
             * indexList : [{"hlsIndex":"/api/v1/vrs/upstream/be5c2368-e1b8-11ea-a4a8-ecd68a2efa35/hlsfile/2016/03/24/20160324032559_882001/1280_720/playlist.m3u8","resolution":"1280_720"},{"hlsIndex":"/api/v1/vrs/upstream/be5c2368-e1b8-11ea-a4a8-ecd68a2efa35/hlsfile/2016/03/24/20160324032559_882001/704_576/playlist.m3u8","resolution":"704_576"},{"hlsIndex":"/api/v1/vrs/upstream/be5c2368-e1b8-11ea-a4a8-ecd68a2efa35/hlsfile/2016/03/24/20160324032559_882001/352_288/playlist.m3u8","resolution":"352_288"}]
             * name : 视频0
             * resolutionNum : 3
             */

            private boolean SecStream;
            private int id;
            private String name;
            private int resolutionNum;
            private List<IndexListBean> indexList;

            public boolean isSecStream() {
                return SecStream;
            }

            public void setSecStream(boolean SecStream) {
                this.SecStream = SecStream;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getResolutionNum() {
                return resolutionNum;
            }

            public void setResolutionNum(int resolutionNum) {
                this.resolutionNum = resolutionNum;
            }

            public List<IndexListBean> getIndexList() {
                return indexList;
            }

            public void setIndexList(List<IndexListBean> indexList) {
                this.indexList = indexList;
            }

            public static class IndexListBean implements Serializable{
                /**
                 * hlsIndex : /api/v1/vrs/upstream/be5c2368-e1b8-11ea-a4a8-ecd68a2efa35/hlsfile/2016/03/24/20160324032559_882001/1280_720/playlist.m3u8
                 * resolution : 1280_720
                 */

                private String hlsIndex;
                private String resolution;

                public String getHlsIndex() {
                    return hlsIndex;
                }

                public void setHlsIndex(String hlsIndex) {
                    this.hlsIndex = hlsIndex;
                }

                public String getResolution() {
                    return resolution;
                }

                public void setResolution(String resolution) {
                    this.resolution = resolution;
                }
            }
        }

}
