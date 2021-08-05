package edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

import edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator.CheckBoxQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.Question.Description;
import edu.cuhk.csci3310.musictheoryapp.R;
import edu.cuhk.csci3310.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGenerator;

public class RestsJudgementQuestionGenerator extends CheckBoxQuestionGenerator
{
    private static final int NUMBER_OF_VARIATIONS = 12;

    private final RandomIntegerGenerator m_RandomForVariation;
    private int m_CurrentQuestionVariation; // to remember which question variation when generating sub questions

    public RestsJudgementQuestionGenerator(SQLiteDatabase db, Context context)
    {
        super("Rests Judgement", 1, 3, db, context);
        m_RandomForVariation = new RandomIntegerGenerator(NUMBER_OF_VARIATIONS);

        addGroupDescription(new Description(Description.TEXT_TYPE,
            getContext().getResources().getString(R.string.rests_judgement_question_title)));
    }

    @Override
    protected void setUpData()
    {
        // 1. setting basic information
        m_CurrentQuestionVariation = m_RandomForVariation.nextInt();
        m_RandomForVariation.exclude(m_CurrentQuestionVariation);
        String questionVariationString = String.format(Locale.US, "%02d", m_CurrentQuestionVariation + 1);
        addQuestionDescription(new Description(Description.IMAGE_TYPE,
            "rest_tick_" + questionVariationString));

        // 2. retrieving the answer
        Cursor cursor = getDatabase().query(
            "Answers",
            new String[] { "Answer" },
            "Topic = ? AND Question = ?",
            new String[] { getTopicSnakeCase(), (m_CurrentQuestionVariation + 1) + "." + getNumber() },
            null,
            null,
            "Answer DESC"
        );
        cursor.moveToFirst();
        addCorrectAnswer(cursor.getString(cursor.getColumnIndex("Answer")));
        cursor.close();
    }
}
