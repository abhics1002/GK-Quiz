package com.abhi.gk;

public class RowItem {
    private int imageId;
    private String question;

 
    public RowItem(int imageId, String question) {
        this.imageId = imageId;
        this.question = question;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    
    public String getquestion() {
        return question;
    }
    public void setquestion(String question) {
        this.question = question;
    }
    
}
