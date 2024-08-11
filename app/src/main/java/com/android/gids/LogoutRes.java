package com.android.gids;

public class LogoutRes {


    Gidssurveyapp GIDS_SURVEY_APP;


    public Gidssurveyapp getGIDS_SURVEY_APP() {
        return GIDS_SURVEY_APP;
    }

    public void setGIDS_SURVEY_APP(Gidssurveyapp GIDS_SURVEY_APP) {
        this.GIDS_SURVEY_APP = GIDS_SURVEY_APP;
    }

    public class Gidssurveyapp {

        public String res_code = "";
        public String res_msg = "";
        public String id = "";
        public String name = "";

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
