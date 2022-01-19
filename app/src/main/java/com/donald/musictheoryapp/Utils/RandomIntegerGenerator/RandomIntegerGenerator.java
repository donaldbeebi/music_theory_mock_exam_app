package com.donald.musictheoryapp.Utils.RandomIntegerGenerator;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

// TODO: FIGURE OUT A WAY TO DETERMINE IF NO NUMBERS CAN BE GENERATED DUE TO THE EVALUATORS
// TODO: IMPLEMENT NON DISTINCTION BETWEEN LOWER AND UPPER BOUNDS

public class RandomIntegerGenerator
{
    public interface ExclusionFunctor { int value(int n); }
    public interface IntegerExcluder
    { boolean excludes(int value); }

    private final int m_InclusiveLowerBound;
    private final int m_ExclusiveUpperBound;
    private final Random m_Random;
    private final TreeSet<Integer> m_HardExcludedIntegers;
    private final TreeSet<Integer> m_SoftExcludedIntegers;

    @Deprecated
    public RandomIntegerGenerator(int exclusiveBound)
    {
        m_InclusiveLowerBound = 0;
        m_ExclusiveUpperBound = exclusiveBound;
        m_Random = new Random();
        m_HardExcludedIntegers = new TreeSet<>();
        m_SoftExcludedIntegers = new TreeSet<>();
    }

    protected RandomIntegerGenerator(int inclusiveLowerBound, int exclusiveUpperBound,
                                     TreeSet<Integer> hardExcludedIntegers,
                                     ArrayList<ExclusionFunctor> functors,
                                     ArrayList<IntegerExcluder> excluders)
    {
        if(exclusiveUpperBound <= inclusiveLowerBound)
            throw new IllegalStateException("Inclusive lower bound is not lower than exclusive upper bound.");
        m_InclusiveLowerBound = inclusiveLowerBound;
        m_ExclusiveUpperBound = exclusiveUpperBound;
        m_Random = new Random();
        m_HardExcludedIntegers = hardExcludedIntegers;
        for(ExclusionFunctor functor : functors) exclude(functor, m_HardExcludedIntegers);
        for(IntegerExcluder excluder : excluders) excludeIf(excluder, m_HardExcludedIntegers);
        m_SoftExcludedIntegers = new TreeSet<>(m_HardExcludedIntegers);
    }

    public int nextInt()
    {
        if((m_ExclusiveUpperBound - m_InclusiveLowerBound) - m_SoftExcludedIntegers.size() <= 0)
            throw new IllegalStateException("All of the possible integers have been excluded.");

        // 1. generate an integer within the bound of the number of possible values
        // e.g. Possible values: 0, 1, 2, 3, 4; excluded: 2; new possible values: 0, 1, 2, 3
        int randomInteger = m_Random.nextInt(m_ExclusiveUpperBound - m_SoftExcludedIntegers.size() - m_InclusiveLowerBound);
        randomInteger += m_InclusiveLowerBound;

        // 2. adjusting the numbers to match the exclusion
        // e.g. generated: 2; after adjustment: 3;
        // very similar to shifting the frame
        // 0, 1, 2, 3 --> 0, 1, _, 3, 4
        //                        +1 +1
        // for every integer excluded
        for(int excludedInteger : m_SoftExcludedIntegers)
        {
            // if the generated integer is less than the current excluded integer, then it is fine
            if(randomInteger < excludedInteger) { break; }
            // if the generated integer is higher than / equal to the current excluded integer
            // then increment it
            else randomInteger++;
        }
        return randomInteger;
    }

    private void exclude(int integer, TreeSet<Integer> treeSet)
    {
        if(integer < m_InclusiveLowerBound)
            throw new IllegalArgumentException("Integer to exclude is beyond the lower bound.");

        if(integer >= m_ExclusiveUpperBound)
            throw new IllegalArgumentException("Integer to exclude is beyond the upper bound.");

        treeSet.add(integer);
    }

    private void exclude(int[] integers, TreeSet<Integer> treeSet)
    {
        for(int integer : integers) exclude(integer, treeSet);
    }

    private void exclude(ExclusionFunctor functor, TreeSet<Integer> treeSet)
    {
        for(int n = m_InclusiveLowerBound; functor.value(n) < m_ExclusiveUpperBound; n++)
        {
            if(functor.value(n) < 0)
                throw new IllegalStateException("Index " + n + " returned value: " + functor.value(n) +
                    " which is lower than the lower bound.");
            if(n == m_ExclusiveUpperBound)
                throw new IllegalStateException("Functor provided is excluding too many integers.");
            treeSet.add(functor.value(n));
        }
    }

    private void excludeIf(IntegerExcluder excluder, TreeSet<Integer> treeSet)
    {
        // iterating through all possible values
        for(int n = m_InclusiveLowerBound; n < m_ExclusiveUpperBound; n++)
        {
            if(excluder.excludes(n)) treeSet.add(n);
        }
    }

    public void exclude(int integer) { exclude(integer, m_SoftExcludedIntegers); }

    public void exclude(int[] integers) { exclude(integers, m_SoftExcludedIntegers); }

    public void exclude(ExclusionFunctor functor) { exclude(functor, m_SoftExcludedIntegers); }

    public void excludeIf(IntegerExcluder excluder) { excludeIf(excluder, m_SoftExcludedIntegers); };

    public void clearAllExcludedIntegers()
    {
        m_SoftExcludedIntegers.clear();
        m_SoftExcludedIntegers.addAll(m_HardExcludedIntegers);
    }

    public int getLowerBound() { return m_InclusiveLowerBound; }

    public int getUpperBound() { return m_ExclusiveUpperBound; }
}