package edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator.TextInputQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.Question.Description;
import edu.cuhk.csci3310.musictheoryapp.Question.TextInputQuestion;
import edu.cuhk.csci3310.musictheoryapp.R;
import edu.cuhk.csci3310.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGenerator;

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
