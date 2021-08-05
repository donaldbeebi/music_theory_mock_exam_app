package edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator.MultipleChoiceQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.Question.Description;
import edu.cuhk.csci3310.musictheoryapp.Question.MultipleChoiceQuestion;
import edu.cuhk.csci3310.musictheoryapp.R;
import edu.cuhk.csci3310.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGenerator;

public class SimpleCompoundQuestionGenerator extends MultipleChoiceQuestionGenerator
{
    private static final String[] QUESTION_VARIATION_NAMES = { "1c", "2c", "3c", "4c", "5c", "6c", "1s", "2s", "3s" };

    private final String STRING_SIMPLE_TIME = getContext().getString(R.string.name_simple_time);
    private final String STRING_COMPOUND_TIME = getContext().getString(R.string.name_compound_time);

    private final RandomIntegerGenerator m_RandomForVariation;

    public SimpleCompoundQuestionGenerator(SQLiteDatabase db, Context context)
    {
        super("Simple Compound", 3, MultipleChoiceQuestion.OptionType.Image, 1, db, context);
        m_RandomForVariation = new RandomIntegerGenerator(9);
    }

    @Override
    protected void setUpData()
    {
        // 1. setting basic information
        int questionVariation = m_RandomForVariation.nextInt();
        m_RandomForVariation.exclude(questionVariation);
        String questionVariationString = QUESTION_VARIATION_NAMES[questionVariation];
        String arg1;
        String arg2;
        if(questionVariationString.charAt(questionVariationString.length() - 1) == 's')
            { arg1 = STRING_SIMPLE_TIME; arg2 = STRING_COMPOUND_TIME; }
        else
            { arg1 = STRING_COMPOUND_TIME; arg2 = STRING_SIMPLE_TIME; }

        // a) first text
        addQuestionDescription(new Description(Description.TEXT_TYPE,
            getContext().getString(R.string.simple_compound_question_title, arg1)));
        // b) image
        addQuestionDescription(new Description(Description.IMAGE_TYPE,
            "q_" + getTopicSnakeCase() + "_" + questionVariationString));
        // c) second text
        addQuestionDescription(new Description(Description.TEXT_TYPE,
            getContext().getString(R.string.question_description_simple_compound, arg2)));

        // 2. setting the correct and the wrong options
        // TODO: CHANGE?
        addCorrectAnswer("a_" + getTopicSnakeCase() + "_" + questionVariationString + "_" + 1);
        for(int i = 1; i < NUMBER_OF_OPTIONS; i++)
        {
            addWrongOption("a_" + getTopicSnakeCase() + "_" + questionVariationString + "_" + (i + 1));
        }
    }
}
