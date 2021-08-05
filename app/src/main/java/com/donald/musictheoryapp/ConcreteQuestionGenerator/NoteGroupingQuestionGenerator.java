package com.donald.musictheoryapp.ConcreteQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

import com.donald.musictheoryapp.AbstractQuestionGenerator.MultipleChoiceQuestionGenerator;
import com.donald.musictheoryapp.Question.Description;
import com.donald.musictheoryapp.Question.MultipleChoiceQuestion;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGenerator;

public class NoteGroupingQuestionGenerator extends MultipleChoiceQuestionGenerator
{
    private static final int NUMBER_OF_QUESTION_VARIATIONS = 10;

    private final RandomIntegerGenerator m_RandomForVariation;

    public NoteGroupingQuestionGenerator(SQLiteDatabase db, Context context)
    {
        super("Note Grouping", 4, MultipleChoiceQuestion.OptionType.Image, 1, db, context);
        m_RandomForVariation = new RandomIntegerGenerator(NUMBER_OF_QUESTION_VARIATIONS);

        addGroupDescription(new Description(Description.TEXT_TYPE,
            getContext().getString(R.string.note_grouping_question_title)));
    }

    @Override
    protected void setUpData()
    {
        // 1. setting basic information
        int questionVariation = m_RandomForVariation.nextInt();
        m_RandomForVariation.exclude(questionVariation);
        String questionVariationString = String.format(Locale.ENGLISH, "%02d", questionVariation + 1);

        // 2. setting the first option with the correct option
        // TODO: USE STANDARDIZED FORMATS
        // TODO: CHANGE?
        addCorrectAnswer("notegrouping_" + questionVariationString + "t");

        // 3. populating the rest of the options with wrong options
        for(int i = 1; i < NUMBER_OF_OPTIONS; i++)
        {
            addWrongOption("notegrouping_" + questionVariationString + "f" + i);
        }
    }
}
