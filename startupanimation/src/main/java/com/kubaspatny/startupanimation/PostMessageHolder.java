package com.kubaspatny.startupanimation;

import org.apache.http.NameValuePair;

import java.net.URL;
import java.util.List;

public class PostMessageHolder {

    private URL url;
    private List<NameValuePair> parms;

    public PostMessageHolder(URL url, List<NameValuePair> parms) {
        this.url = url;
        this.parms = parms;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public List<NameValuePair> getParms() {
        return parms;
    }

    public void setParms(List<NameValuePair> parms) {
        this.parms = parms;
    }

}
