package edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator.MultipleChoiceQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.Question.Description;
import edu.cuhk.csci3310.musictheoryapp.Question.MultipleChoiceQuestion;
import edu.cuhk.csci3310.musictheoryapp.R;
import edu.cuhk.csci3310.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGenerator;

public class TimeSignatureQuestionGenerator extends MultipleChoiceQuestionGenerator
{
    private static final String[] TIME_SIGNATURE_POSSIBLE_ANSWERS =
        {
            "2_2", "2_4", "2_8", "3_2", "3_4", "3_8", "4_2", "4_4", "4_8", "5_4", "5_8",
            "6_4", "6_8", "6_16", "7_4", "7_8", "9_4", "9_8", "9_16", "12_4", "12_8", "12_16"
        };

    private final RandomIntegerGenerator m_RandomForVariation;
    private final RandomIntegerGenerator m_RandomForOptions;

    public TimeSignatureQuestionGenerator(SQLiteDatabase db, Context context)
    {
        super("Time Signature", 3, MultipleChoiceQuestion.OptionType.Image, 3, db, context);
        m_RandomForVariation = new RandomIntegerGenerator(24);
        m_RandomForOptions = new RandomIntegerGenerator(TIME_SIGNATURE_POSSIBLE_ANSWERS.length);

        addGroupDescription(new Description(Description.TEXT_TYPE,
            getContext().getString(R.string.time_signature_question_title)));
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        m_RandomForOptions.clearAllExcludedIntegers();
    }

    @Override
    protected void setUpData()
    {
        // 1. setting basic information
        int questionVariation = m_RandomForVariation.nextInt();
        m_RandomForVariation.exclude(questionVariation);
        addQuestionDescription(new Description(Description.IMAGE_TYPE,
            "q_" + getTopicSnakeCase() + "_" + (questionVariation + 1)));

        // 2. setting the first option with the correct option
        String correctOption;

        Cursor cursor = getDatabase().query(
            "Answers",
            new String[] { "Answer" },
            "Topic = ? AND Question = ?",                                               // column
            new String[] { getTopicSnakeCase(), String.valueOf(questionVariation + 1) },   // criteria
            null,
            null,
            "Answer DESC"
        );
        cursor.moveToFirst();
        correctOption = cursor.getString(cursor.getColumnIndex("Answer"));
        cursor.close();

        // TODO: CHANGE?
        addCorrectAnswer("a_" + getTopicSnakeCase() + "_" + correctOption);

        // 3. generating wrong options
        int indexOfCorrectAnswerInPossibleAnswersToExclude = -1;
        for(int j = 0; j < TIME_SIGNATURE_POSSIBLE_ANSWERS.length; j++)
        {
            if(correctOption.equals(TIME_SIGNATURE_POSSIBLE_ANSWERS[j]))
            {
                indexOfCorrectAnswerInPossibleAnswersToExclude = j;
                break;
            }
        }

        m_RandomForOptions.exclude(indexOfCorrectAnswerInPossibleAnswersToExclude);

        // starting from one because the first answer is the correct answer
        for(int i = 1; i < NUMBER_OF_OPTIONS; i++)
        {
            int randomIndex = m_RandomForOptions.nextInt();
            addWrongOption("a_" + getTopicSnakeCase() + "_" + TIME_SIGNATURE_POSSIBLE_ANSWERS[randomIndex]);
            m_RandomForOptions.exclude(randomIndex);
        }

        m_RandomForOptions.clearAllExcludedIntegers();
    }
}
