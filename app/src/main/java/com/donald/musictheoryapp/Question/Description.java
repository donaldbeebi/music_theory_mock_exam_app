package com.donald.musictheoryapp.Question;

public class Description
{
    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 1;

    public final int type;
    public final String content;

    public Description(int type, String content) { this.type = type; this.content = content; }
}
