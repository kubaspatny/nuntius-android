package com.kubaspatny.startupanimation.data;

import android.provider.BaseColumns;

/**
 * Created by Kuba on 21/9/2014.
 */
public final class NuntiusDataContract {

    public NuntiusDataContract() {
    }

    public static abstract class MessageEntry implements BaseColumns {

        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_NAME_MESSAGE_ID = "messageid";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

    }

}
