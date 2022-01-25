package com.donald.musictheoryapp.QuestionArray;

import java.util.ArrayList;
import java.util.Collections;

import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.QuestionGroup;
import com.donald.musictheoryapp.Question.QuestionSection;

public class QuestionArrayBuilder
{
    private final ArrayList<QuestionSection> sections;
    private final ArrayList<QuestionGroup> groups;
    private final ArrayList<Question> questions;

    public QuestionArrayBuilder()
    {
        sections = new ArrayList<>();
        groups = new ArrayList<>();
        questions = new ArrayList<>();
    }

    public void addSection(QuestionSection section)
    {
        sections.add(section);
        Collections.addAll(groups, section.groups);
        for(QuestionGroup group : section.groups)
        {
            Collections.addAll(questions, group.questions);
        }
    }

    public QuestionArray build()
    {
        QuestionSection[] sections = new QuestionSection[this.sections.size()];
        sections = this.sections.toArray(sections);

        QuestionGroup[] groups = new QuestionGroup[this.groups.size()];
        groups = this.groups.toArray(groups);

        Question[] questions = new Question[this.questions.size()];
        questions = this.questions.toArray(questions);

        return new QuestionArray(sections, groups, questions);
    }
}
