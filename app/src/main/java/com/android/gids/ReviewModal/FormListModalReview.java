package com.android.gids.ReviewModal;

import java.util.List;

public class FormListModalReview {

    Gidssurevyapp GIDS_SURVEY_APP;

    public Gidssurevyapp getGIDS_SURVEY_APP() {
        return GIDS_SURVEY_APP;
    }

    public void setGIDS_SURVEY_APP(Gidssurevyapp GIDS_SURVEY_APP) {
        this.GIDS_SURVEY_APP = GIDS_SURVEY_APP;
    }

    public class Gidssurevyapp {

        public String res_code;
        public String res_msg;
        List<DataListModalReview> DataList;

        public String getRes_code() {
            return res_code;
        }

        public void setRes_code(String res_code) {
            this.res_code = res_code;
        }

        public String getRes_msg() {
            return res_msg;
        }

        public void setRes_msg(String res_msg) {
            this.res_msg = res_msg;
        }

        public List<DataListModalReview> getDataList() {
            return DataList;
        }

        public void setDataList(List<DataListModalReview> dataList) {
            DataList = dataList;
        }
    }

}
