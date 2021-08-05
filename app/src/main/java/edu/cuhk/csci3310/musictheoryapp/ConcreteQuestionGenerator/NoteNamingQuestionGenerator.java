package edu.cuhk.csci3310.musictheoryapp.ConcreteQuestionGenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Random;

import edu.cuhk.csci3310.musictheoryapp.AbstractQuestionGenerator.MultipleChoiceQuestionGenerator;
import edu.cuhk.csci3310.musictheoryapp.Question.Description;
import edu.cuhk.csci3310.musictheoryapp.Question.MultipleChoiceQuestion;
import edu.cuhk.csci3310.musictheoryapp.R;
import edu.cuhk.csci3310.musictheoryapp.Utils.Note;
import edu.cuhk.csci3310.musictheoryapp.Utils.RandomIntegerGenerator.RandomIntegerGenerator;

import static edu.cuhk.csci3310.musictheoryapp.Utils.Note.CLEFS;
import static edu.cuhk.csci3310.musictheoryapp.Utils.Note.LOWEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF;
import static edu.cuhk.csci3310.musictheoryapp.Utils.Note.NOTE_ID_RANGE_FOR_EVERY_CLEF;

public class NoteNamingQuestionGenerator extends MultipleChoiceQuestionGenerator
{

    private final Random m_Random;
    private final RandomIntegerGenerator[] m_RandomsByClef;

    public NoteNamingQuestionGenerator(SQLiteDatabase db, Context context)
    {
        super("Note Naming", 4, MultipleChoiceQuestion.OptionType.Text, 1, db, context);
        m_Random = new Random();
        m_RandomsByClef = new RandomIntegerGenerator[]
            {
                new RandomIntegerGenerator(NOTE_ID_RANGE_FOR_EVERY_CLEF), // treble
                new RandomIntegerGenerator(NOTE_ID_RANGE_FOR_EVERY_CLEF), // alto
                new RandomIntegerGenerator(NOTE_ID_RANGE_FOR_EVERY_CLEF), // tenor
                new RandomIntegerGenerator(NOTE_ID_RANGE_FOR_EVERY_CLEF)  // bass
            };

        addGroupDescription(new Description(Description.TEXT_TYPE,
            getContext().getString(R.string.note_naming_question_title)));
    }

    @Override
    protected void setUpData()
    {
        // 1. generating question
        int clefIndex = m_Random.nextInt(CLEFS.length);
        String clefName = CLEFS[clefIndex];
        RandomIntegerGenerator chosenRandom = m_RandomsByClef[clefIndex];
        int noteID = chosenRandom.nextInt();
        chosenRandom.exclude(noteID);
        noteID += LOWEST_POSSIBLE_NOTE_IDS_GENERATED_BY_CLEF[clefIndex];
        Note correctNote = new Note(noteID);
        addQuestionDescription(new Description(Description.IMAGE_TYPE,
            "g_" + clefName + "_" + correctNote.getStringForImage()));

        // TODO: GENERALIZE THIS QUERY FUNCTION???
        // 2. retrieving the options
        Cursor cursor = getDatabase().query(
            "Answers",
            new String[] { "Question" },
            "Topic = ? AND Answer = ?",
            new String[] { getTopicSnakeCase(), correctNote.getString() },
            null,
            null,
            "Question DESC"
        );
        cursor.moveToFirst();
        Log.d("inside note naming", correctNote.getStringWithRange());
        String[] letters = cursor.getString(cursor.getColumnIndex("Question")).split(",");
        cursor.close();

        // 3. setting the options
        String[] wrongOptions = new String[letters.length - 2];
        int letterIndexToExclude = m_Random.nextInt(letters.length - 1) + 1;
        int currentOptionIndexForCopying = 0;
        for(int i = 1; i < letters.length; i++)
        {
            if(i != letterIndexToExclude)
            {
                wrongOptions[currentOptionIndexForCopying] = letters[i];
                currentOptionIndexForCopying++;
            }
        }
        addCorrectAnswer(correctNote.getString());
        setWrongOptions(wrongOptions);
    }
}
