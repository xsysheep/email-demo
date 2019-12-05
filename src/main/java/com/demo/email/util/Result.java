package com.demo.email.util;

public class Result {

    private String message;
    private Boolean flag;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Result() {
    }

    public Result(String message, Boolean flag) {
        this.message = message;
        this.flag = flag;
    }
}
