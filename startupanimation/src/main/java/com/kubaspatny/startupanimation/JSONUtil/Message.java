package com.kubaspatny.startupanimation.JSONUtil;

import org.joda.time.DateTime;

/**
 * Created by Kuba on 11/9/2014.
 */
public class Message {

    private Long id;
    private String mMessageBody;
    private DateTime mMessageTimestamp;
    private String timestampString;

    public Message() {
    }

    public Message(Long id, String mMessageBody, DateTime mMessageTimestamp, String timestampString) {
        this.id = id;
        this.mMessageBody = mMessageBody;
        this.mMessageTimestamp = mMessageTimestamp;
        this.timestampString = timestampString;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getmMessageBody() {
        return mMessageBody;
    }

    public void setmMessageBody(String mMessageBody) {
        this.mMessageBody = mMessageBody;
    }

    public DateTime getmMessageTimestamp() {
        return mMessageTimestamp;
    }

    public void setmMessageTimestamp(DateTime mMessageTimestamp) {
        this.mMessageTimestamp = mMessageTimestamp;
    }

    public String getTimestampString() {
        return timestampString;
    }

    public void setTimestampString(String timestampString) {
        this.timestampString = timestampString;
    }

}
