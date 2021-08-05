package edu.cuhk.csci3310.musictheoryapp.Question;

public abstract class Question
{
    public interface QuestionVisitor
    {
        void visit(MultipleChoiceQuestion question);
        void visit(TextInputQuestion question);
        void visit(TruthQuestion question);
        void visit(CheckBoxQuestion question);
    }

    private final int m_Number;
    private final QuestionGroup m_Group;
    private final String m_Topic;
    private final Description[] m_Descriptions;
    private final String[] m_Answer;
    private final String[] m_CorrectAnswer;

    Question(int subNumber, QuestionGroup group, String topic,
             Description[] descriptions, String[] correctAnswer)
    {
        m_Number = subNumber;
        m_Group = group;
        m_Topic = topic;
        m_Descriptions = descriptions;
        m_Answer = new String[correctAnswer.length];
        m_CorrectAnswer = correctAnswer;
    }

    // TODO: CALL SUB NUMBER A NUMBER
    public int getNumber() { return m_Number; }

    public QuestionGroup getGroup() { return m_Group; }

    public String getTopic() { return m_Topic; }

    public Description[] getDescriptions() { return m_Descriptions; }

    public void setAnswer(int index, String answer) { m_Answer[index] = answer; }

    public String getAnswer(int index) { return m_Answer[index]; }
    public String getAnswer() { return m_Answer[0]; }

    public int getNumberOfAnswers() { return m_Answer.length; }

    public String getCorrectAnswer(int index) { return m_CorrectAnswer[index]; }
    public String getCorrectAnswer() { return m_CorrectAnswer[0]; }

    public boolean isCorrect(int index) { return m_CorrectAnswer[index].equals(m_Answer[index]); }
    public boolean isCorrect() { return m_CorrectAnswer[0].equals(m_Answer[0]); }

    public int getScore() {
        int score = 0;
        int numberOfAnswers = m_CorrectAnswer.length;
        for(int i = 0; i < numberOfAnswers; i++)
        {
            if(m_CorrectAnswer[i].equals(m_Answer[i]))
                score++;
        }
        return score;
    }

    public abstract void acceptVisitor(QuestionVisitor visitor);
}
