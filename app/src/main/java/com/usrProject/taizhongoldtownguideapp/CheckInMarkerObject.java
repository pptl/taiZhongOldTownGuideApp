package com.usrProject.taizhongoldtownguideapp;

public class CheckInMarkerObject {
    public final String markTitle;
    public final String markContent;
    public final Double markLatitude;
    public final Double markLongitude;
    private boolean checked = false;

    public CheckInMarkerObject(String markTitle, String markContent, Double markLatitude, Double  markLongitude) {
        this.markTitle = markTitle;
        this.markContent = markContent;
        this.markLatitude = markLatitude;
        this.markLongitude = markLongitude;
    }

//  取得變數內容
    public String getMarkTitle() {
        return markTitle;
    }

    public String getMarkContent() {
        return markContent;
    }

    public Double getMarkLatitude() {
        return markLatitude;
    }

    public Double getMarkLongitude() {
        return markLongitude;
    }

//  檢查是否打卡

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
