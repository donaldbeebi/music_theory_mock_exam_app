package edu.cuhk.csci3310.musictheoryapp.Utils;

public enum ScaleType
{
    Major(new int[] { 0, 2, 2, 1, 2, 2, 2 }),
    Minor(new int[] { 0, 2, 1, 2, 2, 1, 2 });

    public final int[] intervals;
    ScaleType(int[] intervals) { this.intervals = intervals; }
}
