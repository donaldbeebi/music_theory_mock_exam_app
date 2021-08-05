package com.donald.musictheoryapp.Utils;

import java.util.Locale;

public class TimeFormatter
{
    public static String convert(long timeInMillis)
    {
        int time = (int) (timeInMillis / 1000);
        int seconds = time % 60;
        int minutes = (time / 60) % (60);
        int hours = time / (60 * 60);
        return hours + ":" + String.format(Locale.US, "%02d", minutes) + ":" + String.format(Locale.US, "%02d", seconds);
    }
}
