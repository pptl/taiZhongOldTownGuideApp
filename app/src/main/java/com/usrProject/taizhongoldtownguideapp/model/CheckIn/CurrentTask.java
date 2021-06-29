package com.usrProject.taizhongoldtownguideapp.model.CheckIn;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.List;

public class CurrentTask implements Serializable {
    public String taskTitle;

    public String taskDesc;

    public String taskImg;

    public List<CheckInMarkerObject> tasksContent;
}
