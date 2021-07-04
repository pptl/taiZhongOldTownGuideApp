package com.usrProject.taizhongoldtownguideapp.schema;

public class UserSchema {
    public enum SharedPreferences{
        InTeam("inTeam");
        public static String USER_DATA = "userData";
        public String field;


        SharedPreferences(String field){
            this.field = field;
        }
    }
}
