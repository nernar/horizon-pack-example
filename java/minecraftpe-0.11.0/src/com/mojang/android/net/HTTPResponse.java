package com.mojang.android.net;

public class HTTPResponse {
    public static final int ABORTED = 2;
    public static final int DONE = 1;
    public static final int IN_PROGRESS = 0;
    public static final int TIME_OUT = 3;
    int status = 0;
    String body = "";
    int responseCode = -100;

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int newStatus) {
        this.status = newStatus;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String newBody) {
        this.body = newBody;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(int newResonseCode) {
        this.responseCode = newResonseCode;
    }
}
