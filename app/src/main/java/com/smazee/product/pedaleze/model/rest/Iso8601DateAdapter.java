package com.smazee.product.pedaleze.model.rest;

/**
 * Created by N.Mahesh on 09/03/2019.
 */


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Iso8601DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    private static final String TAG = Iso8601DateAdapter.class.getSimpleName() + "--->";
        private final DateFormat iso8601Format;

    public Iso8601DateAdapter() {
            this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
//            this.iso8601Format.setTimeZone(TimeZone.getTimeZone("IST"));
        }

        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            String dateFormatAsString = iso8601Format.format(src);
            StringBuffer toFix = new StringBuffer(dateFormatAsString);
            toFix.insert(toFix.length()-2, ':');
            return new JsonPrimitive(toFix.toString());
        }

        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (!(json instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            Date date = deserializeToDate(json);
            if (typeOfT == Date.class) {
                return date;
            } else if (typeOfT == Timestamp.class) {
                return new Timestamp(date.getTime());
            } else if (typeOfT == java.sql.Date.class) {
                return new java.sql.Date(date.getTime());
            } else {
                throw new IllegalArgumentException(getClass() + " cannot deserialize to " + typeOfT);
            }
        }

    /**
     * Modified to remove the last ':' which should be in the timezone part
     * @param json
     * @return
     */
    private Date deserializeToDate(JsonElement json) {
            try {
                String jsonString = json.getAsString();
                StringBuffer toFix = new StringBuffer(jsonString);
                toFix.deleteCharAt(toFix.length()-3);
                return iso8601Format.parse(new String(toFix));
            } catch (ParseException e) {
                throw new JsonSyntaxException(json.getAsString(), e);
            }
        }
    }
