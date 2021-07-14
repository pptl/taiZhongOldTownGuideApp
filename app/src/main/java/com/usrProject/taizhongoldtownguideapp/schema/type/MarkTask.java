package com.usrProject.taizhongoldtownguideapp.schema.type;

public enum MarkTask {
    TASK_INFO,
    CURRENT_TASK;

    public String key;

    MarkTask(){
        this.key = this.toString();
    }
}
