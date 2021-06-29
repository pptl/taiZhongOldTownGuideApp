package com.usrProject.taizhongoldtownguideapp.model.CheckIn;

public class CheckInMarkerObject {
    public String markTitle;
    public String markContent;
    public Double markLatitude;
    public Double markLongitude;
    private boolean checked = false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
