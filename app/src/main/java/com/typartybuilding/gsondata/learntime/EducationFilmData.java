package com.typartybuilding.gsondata.learntime;

import com.typartybuilding.gsondata.ArticleBanner;

public class EducationFilmData {

    public String code;
    public EducationFilm data[];
    public String message;

    public class EducationFilm{

        public int articleType;
        public String articleLabelName;   //视频分类的 名称
        public int articleLabel;         //2
        public ArticleBanner rows[];

    }

}
