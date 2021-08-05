package com.donald.musictheoryapp.ConcreteQuestionGenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.donald.musictheoryapp.AbstractQuestionGenerator.CheckBoxQuestionGenerator;
import com.donald.musictheoryapp.Question.CheckBoxQuestion;
import com.donald.musictheoryapp.Question.Description;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGenerator;
import com.donald.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGeneratorBuilder;

public class TranspositionQuestionGenerator extends CheckBoxQuestionGenerator
{

    private final RandomIntegerGenerator m_RandomForVariation;

    public TranspositionQuestionGenerator(SQLiteDatabase db, Context context)
    {
        super("Transposition", 1, 5, db, context);
        m_RandomForVariation = RandomIntegerGeneratorBuilder.generator()
            .withLowerBound(1)
            .withUpperBound(16)
            .build();
    }

    @Override
    protected void setUpData()
    {
        // 1. getting data about a random question
        int questionVariation = m_RandomForVariation.nextInt();
        m_RandomForVariation.exclude(questionVariation);

        // 2. retrieving the correct answer
        Cursor cursor = getDatabase().query(
            "answer_" + getTopicSnakeCase(),
            new String[] { "transposition", "answer" },
            null,
            null,
            null,
            null,
            "Question DESC"
        );
        cursor.move(questionVariation);
        String transposition = cursor.getString(cursor.getColumnIndex("transposition"));
        char[] answers = cursor.getString(cursor.getColumnIndex("answer")).toCharArray();
        cursor.close();

        // 3. setting information
        // a) first text
        addQuestionDescription(new Description(Description.TEXT_TYPE,
            getContext().getString(R.string.transposition_question_text1)));
        // b) first image
        addQuestionDescription(new Description(Description.IMAGE_TYPE,
            "q_" + getTopicSnakeCase() + "_original_" + questionVariation));
        // c) second text
        addQuestionDescription(new Description(Description.TEXT_TYPE,
            getContext().getString(R.string.transposition_question_text2, transposition)));
        // d) second image
        addQuestionDescription(new Description(Description.IMAGE_TYPE,
            "q_" + getTopicSnakeCase() + "_transposed_" + questionVariation));

        for(int i = 0; i < NUMBER_OF_ANSWERS; i++)
        {
            switch(answers[i]){
                case 'O':
                    addCorrectAnswer(CheckBoxQuestion.CHECK_ANSWER);
                    break;
                case 'X':
                    addCorrectAnswer(CheckBoxQuestion.CROSS_ANSWER);
                    break;
            }
        }

    }

}
