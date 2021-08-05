package edu.cuhk.csci3310.musictheoryapp.Exercise;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator.QuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator.EnharmonicEquivalentQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator.NoteGroupingQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator.NoteNamingQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator.RestsJudgementQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator.RhythmQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator.SimpleCompoundQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator.TimeSignatureQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator.TranspositionQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.FeedReaderDbHelper;
import edu.cuhk.csci3310.musictheoryapp.QuestionArray.QuestionArray;
import edu.cuhk.csci3310.musictheoryapp.QuestionArray.QuestionArrayBuilder;

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
