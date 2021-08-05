package com.donald.musictheoryapp.ConcreteQuestionGenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.donald.musictheoryapp.AbstractQuestionGenerator.TextInputQuestionGenerator;
import com.donald.musictheoryapp.Question.Description;
import com.donald.musictheoryapp.Question.TextInputQuestion;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGenerator;

public class RhythmQuestionGenerator extends TextInputQuestionGenerator
{
    private final RandomIntegerGenerator m_RandomForVariation;

    public RhythmQuestionGenerator(SQLiteDatabase db, Context context)
    {
        super("Rhythm", TextInputQuestion.InputType.Number, 2, 1, db, context);
        int numberOfQuestionVariations = (int) DatabaseUtils
            .queryNumEntries(db, "Answers", "Topic = ?", new String[]{"rhythm"});
        m_RandomForVariation = new RandomIntegerGenerator(numberOfQuestionVariations);

        // TODO: STRING FORMATTING
        addGroupDescription(new Description(Description.TEXT_TYPE,
            getContext().getResources().getString(R.string.rhythm_question_title)));
    }

    @Override
    protected void setUpData()
    {
        // 1. setting basic information
        int questionVariation = m_RandomForVariation.nextInt();
        m_RandomForVariation.exclude(questionVariation);

        // 2. choosing a random question from database
        Cursor cursor = getDatabase().query(
            "Answers",
            new String[] { "Question", "Answer" },
            "Topic = ?",        // column
            new String[] { "rhythm" },  // criteria
            null,
            null,
            "Question DESC"
        );
        cursor.moveToFirst();
        cursor.move(questionVariation);

        addQuestionDescription(new Description(Description.TEXT_TYPE,
            cursor.getString(cursor.getColumnIndex("Question"))));

        addCorrectAnswer(cursor.getString(cursor.getColumnIndex("Answer")));
        cursor.close();
    }
}
