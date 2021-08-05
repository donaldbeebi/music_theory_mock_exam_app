package com.donald.musictheoryapp.Exercise;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.donald.musictheoryapp.AbstractQuestionGenerator.QuestionGenerator;
import com.donald.musictheoryapp.ConcreteQuestionGenerator.EnharmonicEquivalentQuestionGenerator;
import com.donald.musictheoryapp.ConcreteQuestionGenerator.NoteGroupingQuestionGenerator;
import com.donald.musictheoryapp.ConcreteQuestionGenerator.NoteNamingQuestionGenerator;
import com.donald.musictheoryapp.ConcreteQuestionGenerator.RestsJudgementQuestionGenerator;
import com.donald.musictheoryapp.ConcreteQuestionGenerator.RhythmQuestionGenerator;
import com.donald.musictheoryapp.ConcreteQuestionGenerator.SimpleCompoundQuestionGenerator;
import com.donald.musictheoryapp.ConcreteQuestionGenerator.TimeSignatureQuestionGenerator;
import com.donald.musictheoryapp.ConcreteQuestionGenerator.TranspositionQuestionGenerator;
import com.donald.musictheoryapp.Utils.FeedReaderDbHelper;
import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.QuestionArray.QuestionArrayBuilder;

/*
 * Image naming scheme
 * [question/answer]_[question name]_[variation]
 * variation: indicates the index of the variation and sometimes the question to which the answer belongs
 */

public class ExerciseGenerator
{
    // generators
    private final QuestionGenerator[] m_QuestionGenerators;

    public ExerciseGenerator(Context context)
    {
        SQLiteDatabase db = (new FeedReaderDbHelper(context)).getReadableDatabase();

        m_QuestionGenerators = new QuestionGenerator[]
            {
                new TimeSignatureQuestionGenerator(db, context),
                new SimpleCompoundQuestionGenerator(db, context),
                new RhythmQuestionGenerator(db, context),
                new NoteGroupingQuestionGenerator(db, context),
                new RestsJudgementQuestionGenerator(db, context),
                new NoteNamingQuestionGenerator(db, context),
                new EnharmonicEquivalentQuestionGenerator(db, context),
                new TranspositionQuestionGenerator(db, context)
            };
    }

    public QuestionArray generateExercise()
    {
        QuestionArrayBuilder builder = new QuestionArrayBuilder();
        for(int i = 0; i < m_QuestionGenerators.length; i++)
             builder.addGroup(m_QuestionGenerators[i].generate(i + 1));
        return builder.build();
    }
}
