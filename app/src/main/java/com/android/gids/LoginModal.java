package com.android.gids;

public class LoginModal {


    Gidssurveyapp GIDS_SURVEY_APP;


    public Gidssurveyapp getGIDS_SURVEY_APP() {
        return GIDS_SURVEY_APP;
    }

    public void setGIDS_SURVEY_APP(Gidssurveyapp GIDS_SURVEY_APP) {
        this.GIDS_SURVEY_APP = GIDS_SURVEY_APP;
    }

    public class Gidssurveyapp{

        public String res_code="";
        public String res_msg="";
        public String id="";
        public String name="";
        public String email="";
        public String mobile="";
        public String image="";
        public String role="";
        public String company_id="";
        public String supervisor_id="";
        public String created_at="";

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getSupervisor_id() {
            return supervisor_id;
        }

        public void setSupervisor_id(String supervisor_id) {
            this.supervisor_id = supervisor_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }

}
