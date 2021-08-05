package edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator.MultipleChoiceQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.Question.Description;
import edu.cuhk.csci3310.musictheoryapp.Question.MultipleChoiceQuestion;
import edu.cuhk.csci3310.musictheoryapp.R;
import edu.cuhk.csci3310.musictheoryapp.Utils.Accidental;
import edu.cuhk.csci3310.musictheoryapp.Utils.LetterName;
import edu.cuhk.csci3310.musictheoryapp.Utils.Note;
import edu.cuhk.csci3310.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGenerator;
import edu.cuhk.csci3310.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGeneratorBuilder;

import static edu.cuhk.csci3310.musictheoryapp.Utils.Note.CLEFS;
import static edu.cuhk.csci3310.musictheoryapp.Utils.Note.HIGHEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF;
import static edu.cuhk.csci3310.musictheoryapp.Utils.Note.LOWEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF;

public class EnharmonicEquivalentQuestionGenerator extends MultipleChoiceQuestionGenerator
{
    private static final int NUMBER_OF_OPTIONS = 3;

    private final Random m_Random;
    private final RandomIntegerGenerator[] m_RandomsByClef;
    private final RandomIntegerGenerator m_RandomForWrongAccidentals;

    public EnharmonicEquivalentQuestionGenerator(SQLiteDatabase db, Context context)
    {
        super("Enharmonic Equivalent", NUMBER_OF_OPTIONS, MultipleChoiceQuestion.OptionType.Image,
            1, db, context);
        m_Random = new Random();
        m_RandomsByClef = new RandomIntegerGenerator[4];
        {
            for (int i = 0; i < m_RandomsByClef.length; i++)
            {
                final int index = i;
                m_RandomsByClef[i] = RandomIntegerGeneratorBuilder.generator()
                    .withLowerBound(LOWEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF[index] + 6)
                    .withUpperBound(HIGHEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF[index] - 6)
                    .excluding(n -> n * 6 + Math.floorMod((5 - LOWEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF[index]), 6))
                    .build();
            }
        }
        m_RandomForWrongAccidentals = new RandomIntegerGenerator(5); // this only generates 0, 1, 3, 4
        m_RandomForWrongAccidentals.exclude(2);

        addGroupDescription(new Description(Description.TEXT_TYPE,
            getContext().getResources().getString(R.string.enharmonic_equivalent_question_title)));
    }

    @Override
    protected void setUpData()
    {
        // 1. pick a clef first
        int clefIndex = m_Random.nextInt(CLEFS.length);
        String clefName = CLEFS[clefIndex];
        RandomIntegerGenerator chosenRandom = m_RandomsByClef[clefIndex];
        Note lowestPossibleWhiteNote = new Note((LOWEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF[clefIndex] / 6) * 6 + 2);
        Note highestPossibleWhiteNote = new Note((HIGHEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF[clefIndex] / 6) * 6 + 2);
        Log.d("chosen clef", clefName);
        Log.d("lowest possible white note id", String.valueOf(lowestPossibleWhiteNote.getNoteID()));

        // 2. generating a random note as the target note
        int noteID = chosenRandom.nextInt();
        chosenRandom.exclude(noteID);
        Note targetNote = new Note(noteID);
        addQuestionDescription(new Description(Description.IMAGE_TYPE,
            "g_" + clefName + "_" + targetNote.getStringForImage()));
        Log.d("target note string and pitch value", targetNote.getStringWithRange() + " " + targetNote.getPitchValue());

        // 4. generating a random note as the correct note
        // a) the correct note shares the same value as the target note
        // TODO: OPTIMIZE - SEARCH STRAIGHT FROM THE WHITE NOTE 2 DISTANCE AWAY
        int targetPitchValue = targetNote.getPitchValue();

        // b) preparing a list of possible letter names
        ArrayList<LetterName> possibleLetterNames = new ArrayList<>();
        ArrayList<Integer> possibleRanges = new ArrayList<>();
        {
            Note lowerCNote = new Note(Math.max(targetNote.getRange() - 1, 0),LetterName.C, Accidental.PLAIN);
            Note currentNote; // always white note
            if(lowerCNote.getPitchValue() < lowestPossibleWhiteNote.getPitchValue())
                currentNote = lowestPossibleWhiteNote;
            else
                currentNote = lowerCNote;

            // initializing for loop
            int startingLetterOrdinal = currentNote.getLetterName().ordinal();
            //int startingRangeForSearch = currentNote.getRange();
            //int startingPitchValueForSearch = currentNote.getRange() * 12;

            //LetterName currentLetterName;
            int distance = 0;

            // basically going through the keyboard on white notes and see if the white note
            // is 2 or less semitones away from the target note
            // if the current note has the same letter as the target note, it is note included
            // because that would result in identical note (and identical accidental)
            while(distance <= 2 && currentNote.getPitchValue() <= highestPossibleWhiteNote.getPitchValue())
            {
                //currentLetterName = LetterName.values()[i];
                distance = currentNote.getPitchValue() - targetPitchValue;
                if(Math.abs(distance) <= 2 && targetNote.getLetterName() != currentNote.getLetterName())
                {
                    possibleLetterNames.add(currentNote.getLetterName());
                    possibleRanges.add(currentNote.getRange());
                }
                // about to go to the next octave
                if(currentNote.getLetterName() == LetterName.values()[LetterName.values().length - 1])
                {
                    currentNote.changeRangeBy(1);
                    //startingPitchValueForSearch = startingRangeForSearch * 12;
                }
                currentNote.changeLetterNameBy(1);
            }
        }

        Log.d("all possible letters", possibleLetterNames.toString());

        {
            // c) randomly choosing a letter name
            int chosenIndex = m_Random.nextInt(possibleLetterNames.size());
            LetterName chosenLetterName = possibleLetterNames.get(chosenIndex);
            int chosenRange = possibleRanges.get(chosenIndex);
            // d) getting the right accidental
            int difference = targetPitchValue - (chosenLetterName.value + chosenRange * 12);
            Accidental chosenAccidental = Accidental.values()[difference + 2]; // [-2, -1, 0, +1, +2, 0]
            Note correctNote = new Note(chosenRange, chosenLetterName, chosenAccidental);
            addCorrectAnswer("g_" + clefName + "_" + correctNote.getStringForImage());
        }

        // TODO: LATEST ISSUE --> CHOSEN CLEF BASS, TARGET NOTE 1Abb, BUT THE LOWEST NOTE POSSIBLE IS 1Abb, NO ENHARMONIC UNLESS SAME LETTER (NOT ENHARMONIC POSSIBLE WITH 1B)

        // 5. generating wrong options
        // technically, the only source of wrongness comes from choosing the wrong accidental
        for(int i = 1; i < NUMBER_OF_OPTIONS; i++)
        {
            // reusing the possible letter names and possible ranges but with wrong accidentals
            // a) randomly choosing a letter name
            int chosenIndex = m_Random.nextInt(possibleLetterNames.size());
            LetterName chosenLetterName = possibleLetterNames.get(chosenIndex);
            int chosenRange = possibleRanges.get(chosenIndex);
            // b) getting the wrong accidental
            // note: difference is one of the follow values: -2, -1, 1, 2 (never 0)
            int difference = targetPitchValue - (chosenLetterName.value + chosenRange * 12);
            int correctAccidentalIndexToExclude = difference + 2;
            m_RandomForWrongAccidentals.exclude(correctAccidentalIndexToExclude);
            Accidental wrongAccidental = Accidental.values()[m_RandomForWrongAccidentals.nextInt()];
            Note wrongNote = new Note(chosenRange, chosenLetterName, wrongAccidental);
            addWrongOption("g_" + clefName + "_" + wrongNote.getStringForImage());
            m_RandomForWrongAccidentals.clearAllExcludedIntegers();
            m_RandomForWrongAccidentals.exclude(2);
        }
        // TODO: INCLUDE A HARD EXCLUSION FOR THE RANDOM INTEGER GENERATOR
    }
}

/*
    2021-08-02 13:55:37.503 28467-28467/edu.cuhk.csci3310.musictheoryapp D/chosen clef: alto
    2021-08-02 13:55:37.503 28467-28467/edu.cuhk.csci3310.musictheoryapp D/lowest possible white note id: 110
    2021-08-02 13:55:37.503 28467-28467/edu.cuhk.csci3310.musictheoryapp D/target note string and pitch value: 5E# 65
    2021-08-02 13:55:37.503 28467-28467/edu.cuhk.csci3310.musictheoryapp D/all possible letters: []
    2021-08-02 13:55:37.504 28467-28467/edu.cuhk.csci3310.musictheoryapp D/AndroidRuntime: Shutting down VM
    2021-08-02 13:55:37.505 28467-28467/edu.cuhk.csci3310.musictheoryapp E/AndroidRuntime: FATAL EXCEPTION: main
    Process: edu.cuhk.csci3310.musictheoryapp, PID: 28467
    java.lang.IllegalArgumentException: bound must be positive
    at java.util.Random.nextInt(Random.java:388)
    at edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator.EnharmonicEquivalentQuestionGenerator.setUpData(EnharmonicEquivalentQuestionGenerator.java:127)
    at edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator.MultipleChoiceQuestionGenerator.onGenerate(MultipleChoiceQuestionGenerator.java:43)
    at edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator.QuestionGenerator.generate(QuestionGenerator.java:53)
    at edu.cuhk.csci3310.musictheoryapp.Exercise.ExerciseGenerator.addQuestions(ExerciseGenerator.java:95)
    at edu.cuhk.csci3310.musictheoryapp.Exercise.ExerciseGenerator.generateExercise(ExerciseGenerator.java:86)
    at edu.cuhk.csci3310.musictheoryapp.QuestionScreen.prepareExercise(QuestionScreen.java:83)
    at edu.cuhk.csci3310.musictheoryapp.MainActivity.onStartExercise(MainActivity.java:84)
    at edu.cuhk.csci3310.musictheoryapp.ExerciseMenuScreen$1.onClick(ExerciseMenuScreen.java:24)
    at android.view.View.performClick(View.java:7448)
    at com.google.android.material.button.MaterialButton.performClick(MaterialButton.java:1119)
    at android.view.View.performClickInternal(View.java:7425)
    at android.view.View.access$3600(View.java:810)
    at android.view.View$PerformClick.run(View.java:28305)
    at android.os.Handler.handleCallback(Handler.java:938)
    at android.os.Handler.dispatchMessage(Handler.java:99)
    at android.os.Looper.loop(Looper.java:223)
    at android.app.ActivityThread.main(ActivityThread.java:7656)
    at java.lang.reflect.Method.invoke(Native Method)
    at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:592)
    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:947)

 */