package edu.cuhk.csci3310.musictheoryapp.Utils;

public enum Accidental
{
    DOUBLE_FLAT(-2, "bb", "bb"), FLAT(-1, "b", "b"), PLAIN(0, "", ""), SHARP(1, "#", "s"), DOUBLE_SHARP(2, "##", "ss"), NATURAL(0, "", "n");

    public final int value;
    public final String string;
    public final String string_for_image;
    Accidental(int value, String string, String string_for_image)
    {
        this.value = value;
        this.string = string;
        this.string_for_image = string_for_image;
    }
}
