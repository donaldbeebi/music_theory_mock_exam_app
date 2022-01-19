package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;

public class QuestionGroup
{
    public int number;
    public Description[] descriptions;
    public Question[] questions;
    public String topic;

    public QuestionGroup() {}

    @Deprecated
    public QuestionGroup(int number, String topic, Description[] descriptions)
    {
        this.number = number;
        this.topic = topic;
        this.descriptions = descriptions;
    }

    @Deprecated
    public void setQuestions(Question[] questions)
    {
        this.questions = questions;
    }

    @Deprecated
    public int getNumber() { return number; }
    @Deprecated
    public Description[] getDescriptions() { return descriptions; }
    @Deprecated
    public Question getQuestion(int index) { return questions[index]; }
    @Deprecated
    public Question[] getQuestions() { return questions; }
    @Deprecated
    public String getTopic() { return topic; }

    public int totalPoints()
    {
        int score = 0;
        for(Question question : questions)
            score += question.points();
        return score;
    }

    @Override
    public String toString()
    {
        return
            "[Question Group] " +
            "Number: " + number + " " +
            "Topic: " + topic + " " +
            "Descriptions: " + Arrays.toString(descriptions) + " "+
            "Number of questions: " + questions.length + " ";
    }

    public static QuestionGroup fromJSON(JSONObject object) throws JSONException, IOException, XmlPullParserException
    {
        QuestionGroup group = new QuestionGroup();
        group.number = object.getInt("number");
        group.topic = object.getString("topic");
        group.descriptions = JSONArrayUtil.descriptions(object);
        group.questions = JSONArrayUtil.questions(object, group);
        return group;
    }
}
