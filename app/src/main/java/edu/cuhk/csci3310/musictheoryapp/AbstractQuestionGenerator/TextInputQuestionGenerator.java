package edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.cuhk.csci3310.musictheoryapp.Question.Question;
import edu.cuhk.csci3310.musictheoryapp.Question.TextInputQuestion;
import edu.cuhk.csci3310.musictheoryapp.QuestionBuilder.TextInputQuestionBuilder;

public abstract class TextInputQuestionGenerator extends QuestionGenerator
{
    protected final TextInputQuestion.InputType m_InputType;

    protected TextInputQuestionGenerator(String topic, TextInputQuestion.InputType inputType,
                                         int numberOfSubQuestionsPerGeneration, int numberOfAnswers,
                                         SQLiteDatabase db, Context context)
    {
        super(topic, numberOfSubQuestionsPerGeneration, numberOfAnswers, db, context);
        m_InputType = inputType;
    }

    @Override
    protected final void onGenerate() { setUpData(); }

    @Override
    protected final Question onBuildQuestion()
    {
        return TextInputQuestionBuilder
            .question()
            .number(getNumber())
            .group(getGroup())
            .topic(getTopic())
            .descriptions(getQuestionDescriptions())
            .correctAnswer(getCorrectAnswer())
            .inputType(m_InputType)
            .build();
    }

    // getters
    protected TextInputQuestion.InputType getInputType() { return m_InputType; }
}
