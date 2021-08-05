package edu.cuhk.csci3310.musictheoryapp.Utils.RandomIntegerGenerator;

import java.util.ArrayList;
import java.util.TreeSet;

public class RandomIntegerGeneratorBuilder
{
    private Integer m_LowerBound = null; // inclusive
    private Integer m_UpperBound = null; // exclusive
    private final TreeSet<Integer> m_HardExcludedIntegers = new TreeSet<>();
    private final ArrayList<RandomIntegerGenerator.ExclusionFunctor> m_ExclusionFunctors = new ArrayList<>();

    protected void throwError(String field) { throw new AssertionError("Field '" + field + "' not initialized."); }

    public static RandomIntegerGeneratorBuilder generator()
    {
        return new RandomIntegerGeneratorBuilder();
    }

    public RandomIntegerGeneratorBuilder withLowerBound(int inclusiveLowerBound)
    {
        m_LowerBound = inclusiveLowerBound;
        return this;
    }

    public RandomIntegerGeneratorBuilder withUpperBound(int exclusiveUpperBound)
    {
        m_UpperBound = exclusiveUpperBound;
        return this;
    }

    public RandomIntegerGeneratorBuilder excluding(int excludedInteger)
    {
        m_HardExcludedIntegers.add(excludedInteger);
        return this;
    }

    public RandomIntegerGeneratorBuilder excluding(int[] excludedIntegers)
    {
        for(int integer : excludedIntegers) excluding(integer);
        return this;
    }

    public RandomIntegerGeneratorBuilder excluding(RandomIntegerGenerator.ExclusionFunctor functor)
    {
        m_ExclusionFunctors.add(functor);
        return this;
    }

    public RandomIntegerGenerator build()
    {
        if(m_LowerBound == null) throwError("LowerBound");
        if(m_UpperBound == null) throwError("UpperBound");
        return new RandomIntegerGenerator
            (m_LowerBound, m_UpperBound, m_HardExcludedIntegers, m_ExclusionFunctors);
    }
}
