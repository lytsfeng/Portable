package com.ldkj.portable.beans;

import android.graphics.drawable.Drawable;

/**
 * Created by john on 15-3-2.
 */
public class ChildStatusEntity {
    private Drawable childIcon;
    private String childName;
    public void setChildName(String childName) {
        this.childName = childName;
    }
    public String getChildName() {
        return childName;
    }
    public void setChildIcon(Drawable childIcon) {
        this.childIcon = childIcon;
    }
    public Drawable getChildIcon() {
        return childIcon;
    }

    public ChildStatusEntity(String childName,Drawable childIcon){
        this.childIcon = childIcon;
        this.childName = childName;
    }

}
