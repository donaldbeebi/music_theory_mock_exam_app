package com.donald.musictheoryapp.Question;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public abstract class Question
{
    // TODO: MAKE INTEGER NULLABLE
    public static final int MULTIPLE_CHOICE = 0;
    public static final int TEXT_INPUT = 1;
    public static final int CHECK_BOX = 2;
    public static final int TRUTH = 3;
    public static final int INTERVAL_INPUT = 4;

    public interface QuestionVisitor
    {
        void visit(MultipleChoiceQuestion question);
        void visit(TextInputQuestion question);
        void visit(TruthQuestion question);
        void visit(CheckBoxQuestion question);
        void visit(IntervalInputQuestion question);
    }

    public interface Answer
    {
        boolean correct();
    }

    public int number;
    public QuestionGroup group;
    public Description[] descriptions;

    public abstract int points();

    public abstract void acceptVisitor(QuestionVisitor visitor);

    public static Question fromJSON(JSONObject object, QuestionGroup group) throws JSONException, IOException, XmlPullParserException
    {
        Question question;
        switch (object.getInt("type"))
            {
                case MULTIPLE_CHOICE:
                    question = MultipleChoiceQuestion.fromJSON(object, group);
                    break;
                case TEXT_INPUT:
                    question = TextInputQuestion.fromJSON(object, group);
                    break;
                case CHECK_BOX:
                    question = CheckBoxQuestion.fromJSON(object, group);
                    break;
                case TRUTH:
                    question = TruthQuestion.fromJSON(object, group);
                    break;
                case INTERVAL_INPUT:
                    question = IntervalInputQuestion.fromJSON(object, group);
                    break;
                default:
                    throw new JSONException("Question type " + object.getInt("type") + " unrecognized.");
            };
        return question;
    }
}
