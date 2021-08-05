package edu.cuhk.csci3310.musictheoryapp.QuestionBuilder;

import edu.cuhk.csci3310.musictheoryapp.Question.Description;
import edu.cuhk.csci3310.musictheoryapp.Question.QuestionGroup;

public abstract class QuestionBuilder<B, Q>
{
    protected int m_Number;
    protected QuestionGroup m_Group;
    protected String m_Topic;
    protected String m_Title;
    protected String m_Image;
    protected String m_Description;
    protected Description[] m_Descriptions;
    protected String[] m_CorrectAnswer;

    protected QuestionBuilder() {  }
    protected abstract Q newQuestion();
    protected abstract B getThis();

    protected void throwError(String field) { throw new AssertionError("Field '" + field + "' not initialized."); }

    public B number(int number) { m_Number = number; return getThis(); }
    public B group(QuestionGroup group) { m_Group = group; return getThis(); }
    public B topic(String topic) { m_Topic = topic; return getThis(); }
    @Deprecated
    public B title(String title) { m_Title = title; return getThis(); }
    @Deprecated
    public B image(String image) { m_Image = image; return getThis(); }
    public B description(String description) { m_Description = description; return getThis(); }
    public B descriptions(Description[] descriptions) { m_Descriptions = descriptions; return getThis(); }
    public B correctAnswer(String[] correctAnswer) { m_CorrectAnswer = correctAnswer; return getThis(); }
    public Q build()
    {
        if(m_Number == 0) throwError("SubNumber");
        if(m_Topic == null) throwError("Topic");
        if(m_Descriptions == null) throwError("Descriptions");
        if(m_CorrectAnswer == null) throwError("CorrectAnswer");
        return newQuestion();
    }
}
