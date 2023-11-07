package com.example.app_on_phone.signin.util;

import java.io.Serializable;


public class UploadFileDataDto implements Serializable {
    private static final long serialVersionUID = 8413037220089261475L;
    private String url;
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
