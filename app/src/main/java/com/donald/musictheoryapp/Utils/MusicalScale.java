package com.donald.musictheoryapp.Utils;

// TODO: ADD SUPPORT FOR ALTERNATIVE NAMES

public class MusicalScale
{
    public static final int NUMBER_OF_SCALES = 23;

    public static final int E_FLAT_MINOR = 0;
    public static final int G_FLAT_MAJOR = 1;
    public static final int B_FLAT_MINOR = 2;
    public static final int D_FLAT_MAJOR = 3;
    public static final int F_MINOR = 4;
    public static final int A_FLAT_MAJOR = 5;
    public static final int C_MINOR = 6;
    public static final int E_FLAT_MAJOR = 7;
    public static final int G_MINOR = 8;
    public static final int B_FLAT_MAJOR = 9;
    public static final int D_MINOR = 10;
    public static final int F_MAJOR = 11;
    public static final int A_MINOR = 12;
    public static final int C_MAJOR = 13;
    public static final int E_MINOR = 14;
    public static final int G_MAJOR = 15;
    public static final int B_MINOR = 16;
    public static final int D_MAJOR = 17;
    public static final int F_SHARP_MINOR = 18;
    public static final int A_MAJOR = 19;
    public static final int C_SHARP_MINOR = 20;
    public static final int E_MAJOR = 21;
    public static final int G_SHARP_MINOR = 22;
    public static final int B_MAJOR = 23;

    public static final String[] MUSICAL_SCALE_STRINGS =
        {
            "Eb minor",
            "Gb major",
            "Bb minor",
            "Db major",
            "F minor",
            "Ab major",
            "C minor",
            "Eb major",
            "G minor",
            "Bb major",
            "D minor",
            "F major",
            "A minor",
            "C major",
            "E minor",
            "G major",
            "B minor",
            "D major",
            "F# minor",
            "A major",
            "C# minor",
            "E major",
            "G# minor",
            "B major"
        };

    public static boolean isValidScaleID(int musicScaleID)
    {
        return musicScaleID >= 0 && musicScaleID <= 23;
    }

    public static boolean isValidNumberOfAccidentals(int numberOfAccidentals)
    {
        return numberOfAccidentals >= -6 && numberOfAccidentals <= 5;
    }

    private static void throwErrorIfInvalidScaleID(int musicScaleID)
    {
        if(!isValidScaleID(musicScaleID))
            throw new IllegalArgumentException("Music Scale ID " + musicScaleID + " is not valid.");
    }

    private static void throwErrorIfInvalidNumberOfAccidentals(int numberOfAccidentals)
    {
        if(!isValidNumberOfAccidentals(numberOfAccidentals))
            throw new IllegalArgumentException("Number of accidentals " + numberOfAccidentals +
                " is not valid.");
    }

    public static boolean isMajor(int musicScaleID)
    {
        throwErrorIfInvalidScaleID(musicScaleID);
        return Math.floorMod(musicScaleID, 2) == 1;
    }

    public static int getAccidentals(int musicScaleID)
    {
        throwErrorIfInvalidScaleID(musicScaleID);
        return (musicScaleID / 2) - 6;
    }

    public static int getRelativeScale(int musicScaleID)
    {
        throwErrorIfInvalidScaleID(musicScaleID);
        if(isMajor(musicScaleID)) return Math.floorMod((musicScaleID - 1), NUMBER_OF_SCALES);
        return Math.floorMod((musicScaleID + 1), NUMBER_OF_SCALES);
    }

    public static int getParallelScale(int musicScaleID)
    {
        throwErrorIfInvalidScaleID(musicScaleID);
        if(isMajor(musicScaleID)) return Math.floorMod((musicScaleID - 7), NUMBER_OF_SCALES);
        return Math.floorMod((musicScaleID + 7), NUMBER_OF_SCALES);
    }

    public static int getAdjacentScale(int musicScaleID, int offset)
    {
        throwErrorIfInvalidScaleID(musicScaleID);
        return Math.floorMod((musicScaleID + 2 * offset), NUMBER_OF_SCALES);
    }

    public static String getString(int musicScaleID)
    {
        throwErrorIfInvalidScaleID(musicScaleID);
        return MUSICAL_SCALE_STRINGS[musicScaleID];
    }

    public static int getMusicalScaleID(String musicalScaleString)
    {
        for(int i = 0; i < MUSICAL_SCALE_STRINGS.length; i++)
        {
            if (MUSICAL_SCALE_STRINGS[i].equals(musicalScaleString))
                return i;
        }
        throw new IllegalArgumentException("No matching string found.");
    }

    public static int getMinorScaleID(int numberOfAccidentals)
    {
        // TODO: F# Gb
        throwErrorIfInvalidNumberOfAccidentals(numberOfAccidentals);
        return (numberOfAccidentals + 6) * 2;
    }

    public static int getMajorScaleID(int numberOfAccidentals)
    {
        return getMinorScaleID(numberOfAccidentals) + 1;
    }
}
