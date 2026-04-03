package com.mojang.android.net;

import android.util.Log;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HTTPClientManager {
    static HTTPClientManager instance = new HTTPClientManager();
    HttpClient mHTTPClient;
    String mHttpClient;

    private HTTPClientManager() {
        this.mHTTPClient = null;
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpProtocolParams.setVersion(basicHttpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(basicHttpParams, "utf-8");
        ConnManagerParams.setTimeout(basicHttpParams, 30000L);
        basicHttpParams.setBooleanParameter("http.protocol.expect-continue", false);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        try {
            SSLSocketFactory sslSocketFactory = NoCertSSLSocketFactory.createDefault();
            registry.register(new Scheme("https", sslSocketFactory, 443));
        } catch (Exception e) {
            Log.e("MCPE_ssl", "Couldn't create SSLSocketFactory");
        }
        this.mHTTPClient = new DefaultHttpClient(new ThreadSafeClientConnManager(basicHttpParams, registry), basicHttpParams);
    }

    public static HttpClient getHTTPClient() {
        return instance.mHTTPClient;
    }
}
