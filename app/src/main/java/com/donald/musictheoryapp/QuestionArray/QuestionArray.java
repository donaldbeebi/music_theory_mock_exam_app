package com.donald.musictheoryapp.QuestionArray;

import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.QuestionGroup;
import com.donald.musictheoryapp.Question.QuestionSection;
import com.donald.musictheoryapp.Utils.JSONArrayUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class QuestionArray
{
    private final QuestionSection[] sections;
    private final QuestionGroup[] groups;
    private final Question[] questions;

    public QuestionArray(QuestionSection[] sections, QuestionGroup[] groups, Question[] questions)
    {
        this.sections = sections;
        this.groups = groups;
        this.questions = questions;
    }

    public QuestionSection sectionAt(int sectionIndex)
    {
        return sections[sectionIndex];
    }

    public QuestionGroup groupAt(int groupIndex)
    {
        return groups[groupIndex];
    }

    public Question questionAt(int questionIndex)
    {
        return questions[questionIndex];
    }

    public int groupCount() {
        return groups.length;
    }

    public Question questionAt(int groupIndex, int localQuestionIndex)
    {
        return groups[groupIndex].questions[localQuestionIndex];
    }

    public int questionCount()
    {
        return questions.length;
    }

    public int questionIndexOf(Question question)
    {
        for(int i = 0; i < questions.length; i++)
        {
            if(questions[i] == question) return i;
        }
        return -1;
    }

    public int sectionIndexOf(QuestionSection section)
    {
        for(int i = 0; i < sections.length; i++)
        {
            if(sections[i] == section) return i;
        }
        return -1;
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
        QuestionSection[] sections = JSONArrayUtil.sections(object);
        int numberOfGroups = 0;
        int numberOfQuestions = 0;
        for(QuestionSection section : sections)
        {
            numberOfGroups += section.groups.length;
            for(QuestionGroup group : section.groups)
            {
                numberOfQuestions += group.questions.length;
            }
        }

        QuestionGroup[] groups = new QuestionGroup[numberOfGroups];
        Question[] questions = new Question[numberOfQuestions];
        int currentGroup = 0;
        int currentQuestion = 0;
        for(QuestionSection section : sections)
        {
            System.arraycopy(section.groups, 0, groups, currentGroup, section.groups.length);
            currentGroup += section.groups.length;
            for(QuestionGroup group : section.groups)
            {
                System.arraycopy(
                    group.questions, 0, questions, currentQuestion, group.questions.length
                );
                currentQuestion += group.questions.length;
            }
        }

        return new QuestionArray(sections, groups, questions);
    }
}
