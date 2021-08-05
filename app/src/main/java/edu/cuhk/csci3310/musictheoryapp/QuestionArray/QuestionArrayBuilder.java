package edu.cuhk.csci3310.musictheoryapp.QuestionArray;

import java.util.ArrayList;
import java.util.Collections;

import edu.cuhk.csci3310.musictheoryapp.Question.Question;
import edu.cuhk.csci3310.musictheoryapp.Question.QuestionGroup;

public class QuestionArrayBuilder
{
    private final ArrayList<Question> m_Questions;
    private final ArrayList<QuestionGroup> m_Groups;

    public QuestionArrayBuilder()
    {
        m_Questions = new ArrayList<>();
        m_Groups = new ArrayList<>();
    }

    public void addGroup(QuestionGroup group)
    {
        m_Groups.add(group);
        Collections.addAll(m_Questions, group.getQuestions());
    }

    public QuestionArray build()
    {
        Question[] questions = new Question[m_Questions.size()];
        questions = m_Questions.toArray(questions);

        QuestionGroup[] groups = new QuestionGroup[m_Groups.size()];
        groups = m_Groups.toArray(groups);

        return new QuestionArray(questions, groups);
    }
}
