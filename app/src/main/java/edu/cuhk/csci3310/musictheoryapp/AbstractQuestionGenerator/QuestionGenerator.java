package edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.CallSuper;

import java.util.ArrayList;

import edu.cuhk.csci3310.musictheoryapp.Question.Description;
import edu.cuhk.csci3310.musictheoryapp.Question.Question;
import edu.cuhk.csci3310.musictheoryapp.Question.QuestionGroup;
import edu.cuhk.csci3310.musictheoryapp.Utils.SnakeCaseConverter;

// TODO: SHOULD SETTING STATIC TITLES BE MOVED TO CONSTRUCTORS?
// TODO: INITIALIZE V.S. RESET FUNCTION

public abstract class QuestionGenerator
{
    protected final int NUMBER_OF_QUESTIONS_PER_GENERATION;
    protected final int NUMBER_OF_ANSWERS;
    private int m_CurrentCorrectAnswerToSet;

    private final SQLiteDatabase m_DB;
    private final Context m_Context;
    private int m_Number;
    private QuestionGroup m_Group;
    private final String m_Topic;
    private final String m_TopicSnakeCase;
    private final ArrayList<Description> m_GroupDescriptions;
    private ArrayList<Description> m_QuestionDescriptions;
    private String[] m_CorrectAnswer;

    protected QuestionGenerator(String topic, int numberOfQuestionsPerGeneration,
                                int numberOfCorrectAnswers, SQLiteDatabase db, Context context)
    {
        m_Number = 0;
        m_Topic = topic;
        m_TopicSnakeCase = SnakeCaseConverter.convert(topic);
        m_GroupDescriptions = new ArrayList<Description>();
        NUMBER_OF_QUESTIONS_PER_GENERATION = numberOfQuestionsPerGeneration;
        NUMBER_OF_ANSWERS = numberOfCorrectAnswers;
        m_CurrentCorrectAnswerToSet = 0;
        m_DB = db;
        m_Context = context;
    }

    public final QuestionGroup generate(int questionNumber)
    {
        Question[] questions = new Question[NUMBER_OF_QUESTIONS_PER_GENERATION];
        Description[] descriptions = new Description[m_GroupDescriptions.size()];
        descriptions = m_GroupDescriptions.toArray(descriptions);
        m_Group = new QuestionGroup(questionNumber, descriptions);

        // 1. parent question generation
        onInitialize();
        onGenerate();
        questions[0] = onBuildQuestion();

        // 2. sub question generation
        for(int i = 1; i < NUMBER_OF_QUESTIONS_PER_GENERATION; i++)
        {
            onInitialize();
            onGenerate();
            questions[i] = onBuildQuestion();
        }

        // 3. wrapping everything into a question group
        m_Group.setQuestions(questions);

        return m_Group;
    }

    // setters
    protected void addGroupDescription(Description description) { m_GroupDescriptions.add(description); }
    protected void addQuestionDescription(Description description) { m_QuestionDescriptions.add(description); }
    protected void addCorrectAnswer(String correctAnswer)
    {
        if(m_CurrentCorrectAnswerToSet >= NUMBER_OF_ANSWERS)
            throw new IllegalStateException("You are adding too many correct answers.");
        m_CorrectAnswer[m_CurrentCorrectAnswerToSet] = correctAnswer;
        m_CurrentCorrectAnswerToSet++;
    }

    // getters
    protected final Context getContext() { return m_Context; }
    protected final SQLiteDatabase getDatabase() { return m_DB; }
    @Deprecated
    protected final Resources getResources() { return m_Context.getResources(); }   // TODO: MAKE ALL GENERATORS USE THIS FUNCTION INSTEAD
    protected final QuestionGroup getGroup() { return m_Group; }
    protected final int getNumber() { return m_Number; }
    protected final String getTopic() { return m_Topic; }
    protected final String getTopicSnakeCase() { return m_TopicSnakeCase; }
    protected final Description[] getQuestionDescriptions()
    {
        Description[] array = new Description[m_QuestionDescriptions.size()];
        array = m_QuestionDescriptions.toArray(array);
        return array;
    }
    protected final String[] getCorrectAnswer() { return m_CorrectAnswer; }

    /*
     * ABSTRACT FUNCTIONS
     */
    @CallSuper
    protected void onInitialize()
    {
        m_Number++;
        m_QuestionDescriptions = new ArrayList<>();
        m_CorrectAnswer = new String[NUMBER_OF_ANSWERS];
        m_CurrentCorrectAnswerToSet = 0;
    }
    protected abstract void onGenerate();
    protected abstract void setUpData();
    protected abstract Question onBuildQuestion();
}
