package edu.cuhk.csci3310.musictheoryapp.Utils;

public enum LetterName
{
    C(0, "C"), D(2, "D"), E(4, "E"), F(5, "F"), G(7, "G"), A(9, "A"), B(11, "B");

    public final int value;
    public final String string;
    public final String string_for_image;
    LetterName(int value, String string)
    {
        this.value = value;
        this.string = string;
        string_for_image = string.toLowerCase();
    }
}
