package com.usrProject.taizhongoldtownguideapp.schema;

public class UserSchema {
    public enum SharedPreferences{
        USER_INFO("userInfo");
        public static String USER_DATA = "userData";
        public String field;


        SharedPreferences(String field){
            this.field = field;
        }
    }
}
