package com.ldkj.portable.beans;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by john on 15-3-2.
 */
public class GroupEntity {
    private Drawable groupIcon;
    private ArrayList<ChildStatusEntity> childList;
    public void setChildList(ArrayList<ChildStatusEntity> childList) {
        this.childList = childList;
    }
    public ArrayList<ChildStatusEntity> getChildList() {
        return childList;
    }
    public void setGroupIcon(Drawable groupIcon) {
        this.groupIcon = groupIcon;
    }
    public Drawable getGroupIcon() {
        return groupIcon;
    }
}
