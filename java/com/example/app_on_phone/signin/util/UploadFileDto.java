package com.example.app_on_phone.signin.util;

import java.io.Serializable;


public class UploadFileDto implements Serializable {
    private static final long serialVersionUID = 1975550949021371732L;
    private String msg;
    private String code;
    private String res_code;
    private String res_msg;

    public String getRes_code() {
        return res_code;
    }

    public void setRes_code(String res_code) {
        this.res_code = res_code;
    }

    public String getRes_msg() {
        return res_msg;
    }

    public void setRes_msg(String res_msg) {
        this.res_msg = res_msg;
    }

    private  UploadFileDataDto data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UploadFileDataDto getData() {
        return data;
    }

    public void setData(UploadFileDataDto data) {
        this.data = data;
    }
}
