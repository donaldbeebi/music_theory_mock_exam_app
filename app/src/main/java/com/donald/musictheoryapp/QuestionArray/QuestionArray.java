package com.donald.musictheoryapp.QuestionArray;

import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.QuestionGroup;
import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class QuestionArray
{
    private final Question[] questions;
    private final QuestionGroup[] groups;

    public QuestionArray(Question[] questions, QuestionGroup[] groups)
    {
        this.questions = questions;
        this.groups = groups;
    }

    public Question question(int questionIndex)
    {
        return questions[questionIndex];
    }

    public Question question(int groupIndex, int localQuestionIndex)
    {
        return groups[groupIndex].questions[localQuestionIndex];
    }

    public int numberOfQuestions()
    {
        return questions.length;
    }

    public QuestionGroup group(int groupIndex)
    {
        return groups[groupIndex];
    }

    public int numberOfGroups() {
        return groups.length;
    }

    public int questionIndex(Question question)
    {
        int index = -1;
        for(int i = 0; i < questions.length; i++)
        {
            if(questions[i] == question) index = i;
        }
        return index;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(QuestionGroup group : groups)
        {
            builder.append(group.toString()).append("\n");
            for(Question question : group.getQuestions())
            {
                builder.append("    ").append(question.toString()).append("\n");
            }
        }
        return builder.toString();
    }

    public static QuestionArray fromJSON(JSONObject object) throws JSONException, IOException, XmlPullParserException
    {
        QuestionArrayBuilder builder = new QuestionArrayBuilder();
        builder.addGroups(JSONArrayUtil.groups(object));
        return builder.build();
    }
}
