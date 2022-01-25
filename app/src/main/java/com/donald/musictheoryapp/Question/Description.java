package com.donald.musictheoryapp.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Description
{
    public static final int TEXT_TYPE = 0;
    public static final int TEXT_EMPHASIZE_TYPE = 1;
    public static final int IMAGE_TYPE = 2;
    public static final int SCORE_TYPE = 3;

    public final int type;
    public final String content;

    public Description(int type, String content) { this.type = type; this.content = content; }

    @Override
    public String toString()
    {
        return
            "Type: " + type + " " +
            "Content: " + content;
    }

    public static Description fromJSON(JSONObject object) throws JSONException
    {
        String content = new String(
            object.getString("content").getBytes(StandardCharsets.ISO_8859_1),
            StandardCharsets.UTF_8
        );
        return new Description(object.getInt("type"), content);
    }
}
