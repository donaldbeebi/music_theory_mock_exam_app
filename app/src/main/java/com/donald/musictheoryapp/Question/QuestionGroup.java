package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;

public class QuestionGroup
{
    public QuestionSection section;
    public int number;
    public Description[] descriptions;
    public Question[] questions;
    public String name;

    @Deprecated
    public int getNumber() { return number; }
    @Deprecated
    public Question getQuestion(int index) { return questions[index]; }
    @Deprecated
    public Question[] getQuestions() { return questions; }
    @Deprecated
    public String getName() { return name; }

    public int points()
    {
        int points = 0;
        for(Question question : questions)
            points += question.points();
        return points;
    }

    public int maxPoints()
    {
        int maxPoints = 0;
        for(Question question : questions)
        {
            maxPoints += question.maxPoints();
        }
        return maxPoints;
    }

    @Override
    public String toString()
    {
        return
            "[Question Group] " +
            "Number: " + number + " " +
            "Topic: " + name + " " +
            "Descriptions: " + Arrays.toString(descriptions) + " "+
            "Number of questions: " + questions.length + " ";
    }

    public static QuestionGroup fromJSON(JSONObject object, QuestionSection section) throws JSONException, IOException, XmlPullParserException
    {
        QuestionGroup group = new QuestionGroup();
        group.section = section;
        group.number = object.getInt("number");
        group.name = object.getString("topic");
        group.descriptions = JSONArrayUtil.descriptions(object);
        group.questions = JSONArrayUtil.questions(object, group);
        return group;
    }
}
