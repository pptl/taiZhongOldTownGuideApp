package com.usrProject.taizhongoldtownguideapp;

public class CheckInMarkerObject {
    private String markTitle;
    private String markContent;
    private Double markLatitude;
    private Double markLongitude;
    private boolean checked = false;

    public CheckInMarkerObject(String markTitle, String markContent, Double markLatitude, Double  markLongitude) {
        this.markTitle = markTitle;
        this.markContent = markContent;
        this.markLatitude = markLatitude;
        this.markLongitude = markLongitude;
    }

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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setMarkTitle(String markTitle) {
        this.markTitle = markTitle;
    }

    public void setMarkContent(String markContent) {
        this.markContent = markContent;
    }

    public void setMarkLatitude(Double markLatitude) {
        this.markLatitude = markLatitude;
    }

    public void setMarkLongitude(Double markLongitude) {
        this.markLongitude = markLongitude;
    }
}
