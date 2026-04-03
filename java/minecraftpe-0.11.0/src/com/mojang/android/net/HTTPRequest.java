package com.mojang.android.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class HTTPRequest {
    HttpRequestBase mHTTPRequest = null;
    String mURL = "";
    String mRequestBody = "";
    String mCookieData = "";
    String mRequestContentType = "text/plain";
    HTTPResponse mResponse = new HTTPResponse();

    public void setURL(String url) {
        this.mURL = url;
    }

    public void setRequestBody(String requestBody) {
        this.mRequestBody = requestBody;
    }

    public void setCookieData(String cookieData) {
        this.mCookieData = cookieData;
    }

    public void setContentType(String conentType) {
        this.mRequestContentType = conentType;
    }

    public HTTPResponse send(String method) {
        createHTTPRequest(method);
        addHeaders();
        if (this.mResponse.getStatus() == 2) {
            return this.mResponse;
        }
        try {
            HttpClient client = HTTPClientManager.getHTTPClient();
            HttpResponse response = client.execute(this.mHTTPRequest);
            this.mResponse.setResponseCode(response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            this.mResponse.setBody(EntityUtils.toString(entity));
            this.mResponse.setStatus(1);
            return this.mResponse;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            this.mHTTPRequest = null;
            return this.mResponse;
        } catch (ConnectTimeoutException e2) {
            this.mResponse.setStatus(3);
            this.mHTTPRequest = null;
            return this.mResponse;
        } catch (IOException e3) {
            e3.printStackTrace();
            this.mHTTPRequest = null;
            return this.mResponse;
        }
    }

    public synchronized void abort() {
        this.mResponse.setStatus(2);
        if (this.mHTTPRequest != null) {
            this.mHTTPRequest.abort();
        }
    }

    private synchronized void createHTTPRequest(String method) {
        if (method.equals("DELETE")) {
            this.mHTTPRequest = new HttpDelete(this.mURL);
        } else if (method.equals("PUT")) {
            HttpPut putRequest = new HttpPut(this.mURL);
            addBodyToRequest(putRequest);
            this.mHTTPRequest = putRequest;
        } else if (method.equals("GET")) {
            this.mHTTPRequest = new HttpGet(this.mURL);
        } else if (method.equals("POST")) {
            HttpPost postRequest = new HttpPost(this.mURL);
            addBodyToRequest(postRequest);
            this.mHTTPRequest = postRequest;
        } else {
            throw new InvalidParameterException("Unknown request method " + method);
        }
    }

    private void addBodyToRequest(HttpEntityEnclosingRequestBase request) {
        if (this.mRequestBody != "") {
            try {
                StringEntity se = new StringEntity(this.mRequestBody);
                se.setContentType(this.mRequestContentType);
                request.setEntity(se);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void addHeaders() {
        this.mHTTPRequest.addHeader("User-Agent", "MCPE/Android");
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 3000);
        this.mHTTPRequest.setParams(basicHttpParams);
        if (this.mCookieData != null && this.mCookieData.length() > 0) {
            this.mHTTPRequest.addHeader("Cookie", this.mCookieData);
        }
        this.mHTTPRequest.addHeader("Content-Type", this.mRequestContentType);
        this.mHTTPRequest.addHeader("Charset", "utf-8");
    }
}
