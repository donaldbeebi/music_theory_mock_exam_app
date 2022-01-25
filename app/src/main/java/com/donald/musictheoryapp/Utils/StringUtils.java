package com.donald.musictheoryapp.Utils;

public class StringUtils
{
    public static String capitalize(String string)
    {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < string.length(); i++)
        {
            char currentChar = string.charAt(i);
            if(i == 0 || (string.charAt(i - 1) == ' ' && Character.isAlphabetic(currentChar)))
            {
                builder.append(Character.toUpperCase(currentChar));
            }
            else
            {
                builder.append(currentChar);
            }
        }

        return builder.toString();
    }

    public static String snakeCase(String string)
    {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < string.length(); i++)
        {
            if(Character.isAlphabetic(string.charAt(i)))
                builder.append(Character.toLowerCase(string.charAt(i)));
            else if(string.charAt(i) == ' ')
                builder.append('_');
            //else throw new IllegalArgumentException("Illegal character detected");
            else builder.append(string.charAt(i));
        }

        return builder.toString();
    }
}
