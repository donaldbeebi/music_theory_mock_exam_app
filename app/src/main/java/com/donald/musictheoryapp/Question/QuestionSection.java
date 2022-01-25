package com.donald.musictheoryapp.Question;

import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class QuestionSection
{
	public int number;
	public String name;
	public QuestionGroup[] groups;

	public static QuestionSection fromJSON(JSONObject object) throws JSONException, IOException, XmlPullParserException
	{
		QuestionSection section = new QuestionSection();
		section.number = object.getInt("number");
		section.name = object.getString("name");
		section.groups = JSONArrayUtil.groups(object, section);
		return section;
	}
}
