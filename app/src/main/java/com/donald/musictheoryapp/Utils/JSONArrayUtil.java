package com.donald.musictheoryapp.Utils;

import com.donald.musictheoryapp.Question.Description;
import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.QuestionGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class JSONArrayUtil
{
    public static String[] strings(JSONObject object, String key) throws JSONException
    {
        JSONArray array = object.getJSONArray(key);
        int length = array.length();
        String[] answers = new String[length];
        for(int i = 0; i < length; i++)
            answers[i] = array.getString(i);
        return answers;
    }

    public static String[] options(JSONObject object) throws JSONException
    {
        return strings(object, "options");
    }

    public static Description[] descriptions(JSONObject object) throws JSONException
    {
        JSONArray array = object.getJSONArray("descriptions");
        int length = array.length();
        Description[] descriptions = new Description[length];
        for(int i = 0; i < length; i++)
            descriptions[i] = Description.fromJSON(array.getJSONObject(i));
        return descriptions;
    }

    public static Question[] questions(JSONObject object, QuestionGroup group) throws JSONException, IOException, XmlPullParserException
    {
        JSONArray array = object.getJSONArray("questions");
        int length = array.length();
        Question[] questions = new Question[length];
        for(int i = 0; i < length; i++)
            questions[i] = Question.fromJSON(array.getJSONObject(i), group);
        return questions;
    }

    public static QuestionGroup[] groups(JSONObject object) throws JSONException, IOException, XmlPullParserException
    {
        JSONArray array = object.getJSONArray("groups");
        int length = array.length();
        QuestionGroup[] groups = new QuestionGroup[length];
        for(int i = 0; i < length; i++)
            groups[i] = QuestionGroup.fromJSON(array.getJSONObject(i));
        return groups;
    }
}
