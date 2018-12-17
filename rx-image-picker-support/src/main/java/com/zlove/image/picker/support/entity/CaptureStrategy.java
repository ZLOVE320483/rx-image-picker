package com.zlove.image.picker.support.entity;

public class CaptureStrategy {

    public boolean isPublic;
    public String authority;

    public CaptureStrategy(boolean isPublic, String authority) {
        this.isPublic = isPublic;
        this.authority = authority;
    }
}
