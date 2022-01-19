package com.donald.musictheoryapp.Question;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import com.donald.musictheoryapp.BuildConfig;
import com.donald.musictheoryapp.Music.MusicXML.Measure;
import com.donald.musictheoryapp.Music.MusicXML.Score;
import com.donald.musictheoryapp.Music.ScoreView.ScoreView;
import com.donald.musictheoryapp.PanelOnTouchListener;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.Screen.Screen;
import com.donald.musictheoryapp.Utils.QuestionButton;
import com.donald.musictheoryapp.Utils.QuestionCheckBox;

import org.xmlpull.v1.XmlPullParserException;

public class QuestionDisplayHelper implements Question.QuestionVisitor
{
    // TODO: THE FREAKING BUTTON SHRINKS WHEN STROKE IS APPLIED
    private static class EditTextInputSaver implements Runnable
    {
        private final TextInputQuestion question;
        private final EditText editText;
        private final int answerIndex;

        EditTextInputSaver(TextInputQuestion question, EditText editText, int answerIndex)
        {
            this.question = question;
            this.editText = editText;
            this.answerIndex = answerIndex;
        }

        @Override
        public void run()
        {
            question.answers[answerIndex].userAnswer = editText.getText().toString();
        }
    }

    private static final int[] BUTTON_BACKGROUNDS = new int[]
        {
            R.drawable.background_red_button,
            R.drawable.background_blue_button,
            R.drawable.background_green_button,
            R.drawable.background_yellow_button
        };

    private static final int BTN_MARGIN = 16;
    private static final int BTN_PADDING = 16;
    private static final int CB_MARGIN = 16;

    private final TextView sectionView;
    private final TextView numberView;
    private final LinearLayout content;
    private final ArrayList<View> questionDescriptionViews;

    private Question currentQuestion;

    private final GridLayout gridLayout;
    private final Context context;
    private final LayoutInflater inflater;
    private boolean readingMode;

    public QuestionDisplayHelper(Screen screen)
    {
        final View view = screen.getView();
        context = screen.getContext();
        inflater = screen.getLayoutInflater();

        if (BuildConfig.DEBUG && !view.getTag().toString().equals("question_layout"))
        {
            throw new AssertionError(
                "View passed into question display unit loader is not a question screen layout."
            );
        }

        sectionView = view.findViewById(R.id.question_section);
        numberView = view.findViewById(R.id.question_number);
        content = view.findViewById(R.id.question_content);
        questionDescriptionViews = new ArrayList<>();

        currentQuestion = null;

        gridLayout = view.findViewById(R.id.question_answer_grid_layout);
        readingMode = false;
    }

    public void setReadingMode(boolean readingMode)
    {
        this.readingMode = readingMode;
    }

    public void displayQuestion(Question question)
    {
        // Setting up the question section and number
        StringBuilder numberStringBuilder = new StringBuilder();
        sectionView.setText(1 + "  " + "IMPLEMENT SECTION NAME");
        numberStringBuilder.append(question.group.number);

        if(question.group.questions.length > 1)
        {
            int questionSubNumber = question.number % 26;
            numberStringBuilder.append((char) (questionSubNumber  - 1 + 'a'));
        }

        numberStringBuilder.append(')');
        numberView.setText(numberStringBuilder.toString());

        // handling group descriptions
        if(currentQuestion == null || currentQuestion.group != question.group)
        {
            content.removeAllViews();
            Description[] descriptions = question.group.descriptions;
            for(Description description : descriptions)
                addDescription(description, false);
        }
        else for(View view : questionDescriptionViews) content.removeView(view);

        // handling question descriptions
        Description[] descriptions = question.descriptions;
        for(Description description : descriptions)
            addDescription(description, true);

        gridLayout.removeAllViews();
        // handling input panel
        // TODO: DEBUG
        question.acceptVisitor(this);
        //NoteInputPanel panel = new NoteInputPanel(m_Inflater, m_List);
        currentQuestion = question;
    }

    private void addDescription(Description description, boolean isQuestionDescription)
    {
        switch(description.type)
        {
            case Description.TEXT_TYPE:
            {
                TextView textView = new TextView(context);
                textView.setText(description.content);
                content.addView(textView);
                if(isQuestionDescription) questionDescriptionViews.add(textView);
                break;
            }
            case Description.IMAGE_TYPE:
            {
                // TODO: PRELOAD AND CACHING
                ImageView imageView = retrieveImage(description.content);
                content.addView(imageView);
                if(isQuestionDescription) questionDescriptionViews.add(imageView);
                break;
            }
            case Description.SCORE_TYPE:
            {
                try
                {
                    Score score = Score.fromXML(description.content);
                    ScoreView scoreView = new ScoreView(context);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                    scoreView.setLayoutParams(params);
                    scoreView.setScore(score);
                    content.addView(scoreView);
                    if(isQuestionDescription) questionDescriptionViews.add(scoreView);
                }
                catch (IOException | XmlPullParserException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void visit(MultipleChoiceQuestion question)
    {
        QuestionButton[] buttons = new QuestionButton[question.options.length];
        switch(question.optionType)
        {
            case IMAGE:
            {
                int column = 2;
                ImageView[] images = new ImageView[question.options.length];
                float buttonRatio;
                for(int i = 0; i < images.length; i++)
                {
                    images[i] = retrieveImage(question.options[i]);
                }
                buttonRatio = images[0].getDrawable().getIntrinsicWidth()
                        / (float) images[0].getDrawable().getIntrinsicHeight();
                if (buttonRatio > 2f)
                {
                    column = 1;
                }
                buttonRatio = Math.max(buttonRatio, 1f);
                final int row = (question.options.length + 1) / column;
                gridLayout.setColumnCount(column);
                gridLayout.setRowCount(row);

                // Populating the list
                for (int i = 0; i < question.options.length; i++)
                {
                    // 1. inflate view preset
                    QuestionButton button = new QuestionButton(context);
                    button.setBackgroundResource(BUTTON_BACKGROUNDS[i % BUTTON_BACKGROUNDS.length]);
                    button.getViewTreeObserver().addOnGlobalLayoutListener(
                        () ->
                        {
                            //int padding = button.getHeight() / 8;
                            //button.setPadding(padding, padding, padding, padding);
                        }
                    );
                    //int padding = button.getWidth() / 8;
                    //button.setPadding(padding, padding, padding, padding);
                    button.setPadding(BTN_PADDING, BTN_PADDING, BTN_PADDING, BTN_PADDING);
                    int columnSize;
                    if(column == 2 && i == question.options.length - 1 &&
                        question.options.length % 2 == 1)
                    {
                        columnSize = 2;
                        button.setRatio(buttonRatio * 2f);
                    }
                    else
                    {
                        columnSize = 1;
                        button.setRatio(buttonRatio);
                    }
                    GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
                    buttonParams.width = 0;
                    buttonParams.columnSpec = GridLayout.spec(i % column, columnSize, GridLayout.FILL, 1f);
                    buttonParams.rowSpec = GridLayout.spec(i / column, 1, GridLayout.CENTER, 1f);
                    buttonParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN);
                    button.setLayoutParams(buttonParams);
                    gridLayout.addView(button);

                    // 2. setting up the content
                    ImageView imageView = images[i];
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    //imageView.setAdjustViewBounds(true);
                    //imageView.setBackgroundResource(R.drawable.background_image);
                    //imageView.setClipToOutline(true);
                    imageView.setLayoutParams(params);
                    button.addView(imageView);
                    buttons[i] = button;
                }

                break;
            }
            case SCORE:
            case TEXT:
            {

                final int column = 2;
                final int row = (question.options.length + 1) / column;
                gridLayout.setColumnCount(column);
                gridLayout.setRowCount(row);
                final float ratio;
                switch(row)
                {
                    case 2:
                        ratio = 3f / 2f;
                        break;
                    case 3:
                        ratio = 2f / 1f;
                        break;
                    default:
                        ratio = 1f;
                        break;
                }

                // Populating the list
                for (int i = 0; i < question.options.length; i++)
                {
                    // 1. inflate view preset
                    QuestionButton button = new QuestionButton(context);
                    button.setBackgroundResource(BUTTON_BACKGROUNDS[i % BUTTON_BACKGROUNDS.length]);
                    button.setPadding(BTN_PADDING, BTN_PADDING, BTN_PADDING, BTN_PADDING);
                    int columnSize;
                    if(i == question.options.length - 1 && question.options.length % 2 == 1)
                    {
                        columnSize = 2;
                        button.setRatio(ratio * 2f);
                    }
                    else
                    {
                        columnSize = 1;
                        button.setRatio(ratio);
                    }
                    GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
                    buttonParams.width = 0;
                    buttonParams.columnSpec = GridLayout.spec(i % column, columnSize, GridLayout.FILL, 1f);
                    buttonParams.rowSpec = GridLayout.spec(i / column, 1, GridLayout.CENTER, 1f);
                    buttonParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN);
                    button.setLayoutParams(buttonParams);
                    gridLayout.addView(button);

                    // 2. setting up the content
                    // TODO: USE REUSABLE XML ATTRIBUTE SET (MAYBE NOT LOL)
                    if(question.optionType == MultipleChoiceQuestion.OptionType.TEXT)
                    {
                        TextView textView = new TextView(context);
                        textView.setText(question.options[i]);
                        textView.setGravity(Gravity.CENTER);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        textView.setTextColor(Color.BLACK);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        textView.setLayoutParams(params);
                        button.addView(textView);
                    }
                    else if(question.optionType == MultipleChoiceQuestion.OptionType.SCORE)
                    {
                        ScoreView scoreView = new ScoreView(context);
                        try
                        {
                            scoreView.setScore(Score.fromXML(question.options[i]));
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.addRule(RelativeLayout.CENTER_IN_PARENT);
                            scoreView.setFixedRatio(1.5f);
                            scoreView.setLayoutParams(params);
                            button.addView(scoreView);
                        }
                        catch(IOException | XmlPullParserException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    buttons[i] = button;
                }

                break;
            }
        }

        if(!readingMode)
        {
            // Setting up onClick listeners
            for (int i = 0; i < question.options.length; i++)
            {
                final int index = i;
                buttons[i].setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (buttons[index].isSelected())
                        {
                            // if the user is clicking an already selected button, deselect it
                            question.answer.userAnswer = -1;
                            buttons[index].setSelected(false);
                        }
                        else
                        {
                            // if the user is clicking a non-selected button, select it
                            question.answer.userAnswer = index;
                            buttons[index].setSelected(true);
                            for (int j = 0; j < question.options.length; j++)
                            {
                                if (index != j) buttons[j].setSelected(false);
                            }
                        }
                    }
                });
            }

            // Restoring answer if any
            if(question.answer.userAnswer != -1)
            {
                View selectedButton = buttons[question.answer.userAnswer];
                selectedButton.setSelected(true);
                selectedButton.jumpDrawablesToCurrentState();
            }
        }
        else
        {
            for (QuestionButton button : buttons)
            {
                button.setEnabled(false);
            }

            if(question.answer.userAnswer != -1)
            {
                buttons[question.answer.userAnswer].setSelected(true);
                if(question.answer.correct())
                {
                    buttons[question.answer.userAnswer].setCorrect(true);
                }
                else
                {
                    buttons[question.answer.correctAnswer].setCorrect(true);
                }
            }
            else
            {
                buttons[question.answer.correctAnswer].setCorrect(true);
            }
        }
    }

    @Override
    public void visit(TextInputQuestion question)
    {
        int numberOfItems = question.answers.length;
        for (int i = 0; i < numberOfItems; i++)
        {
            // 1. populating the list
            View item = inflater.inflate(R.layout.item_text_entry, gridLayout, false);
            EditText editText = item.findViewById(R.id.text_entry_item_text_entry);

            // changing the keyboard layout as appropriate
            if (question.inputType == TextInputQuestion.InputType.Number)
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            gridLayout.addView(item);

            // 2. restoring answer if any
            if (question.answers[i].userAnswer != null)
            {
                editText.setText(question.answers[i].userAnswer);
            }

            if (readingMode)
            {
                editText.setEnabled(false);
                if (question.answers[i].correct())
                    editText.setTextColor(Color.GREEN);
                else
                    editText.setTextColor(Color.RED);
            }

            // 3. setting up edit text saver
            Handler handler = new Handler(Looper.getMainLooper());

            final EditTextInputSaver saver = new EditTextInputSaver(question, editText, i);
            editText.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s)
                {
                    handler.removeCallbacks(saver);
                    handler.postDelayed(saver, 500);
                }
            });
        }
    }

    @Override
    public void visit(TruthQuestion question)
    {
        gridLayout.setColumnCount(2);
        gridLayout.setRowCount(1);

        QuestionButton trueButton = new QuestionButton(context);
        trueButton.setBackgroundResource(R.drawable.background_green_button);
        trueButton.setRatio(2f);
        QuestionButton falseButton = new QuestionButton(context);
        falseButton.setBackgroundResource(R.drawable.background_red_button);
        falseButton.setRatio(2f);

        // params
        {
            GridLayout.LayoutParams trueParams = new GridLayout.LayoutParams();
            trueParams.columnSpec = GridLayout.spec(0, 1, GridLayout.FILL, 1f);
            trueParams.rowSpec = GridLayout.spec(0, 1, GridLayout.CENTER, 1f);
            trueParams.width = 0;
            trueParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN);
            trueButton.setLayoutParams(trueParams);
            GridLayout.LayoutParams falseParams = new GridLayout.LayoutParams();
            falseParams.columnSpec = GridLayout.spec(1, 1, GridLayout.FILL, 1f);
            falseParams.rowSpec = GridLayout.spec(0, 1, GridLayout.CENTER, 1f);
            falseParams.width = 0;
            falseParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN);
            falseButton.setLayoutParams(falseParams);
        }

        gridLayout.addView(trueButton);
        gridLayout.addView(falseButton);

        // text
        {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.CENTER_IN_PARENT);

            TextView trueText = new TextView(context);
            trueText.setText(R.string.true_button_text);
            trueText.setGravity(Gravity.CENTER);
            trueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            trueText.setTextColor(Color.BLACK);
            trueText.setLayoutParams(params);
            trueButton.addView(trueText);
            TextView falseText = new TextView(context);
            falseText.setText(R.string.false_button_text);
            falseText.setGravity(Gravity.CENTER);
            falseText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            falseText.setTextColor(Color.BLACK);
            falseText.setLayoutParams(params);
            falseButton.addView(falseText);
        }


        if(!readingMode)
        {
            // restoring answer
            if(question.answer.userAnswer != null)
            {
                trueButton.setSelected(question.answer.userAnswer);
                falseButton.setSelected(!question.answer.userAnswer);
            }

            // listeners
            trueButton.setOnClickListener(
                (View view) ->
                {
                    if(question.answer.userAnswer == null || question.answer.userAnswer == false)
                    {
                        trueButton.setSelected(true);
                        falseButton.setSelected(false);
                        question.answer.userAnswer = true;
                    }
                    else
                    {
                        trueButton.setSelected(false);
                        question.answer.userAnswer = null;
                    }
                }
            );
            falseButton.setOnClickListener(
                (View view) ->
                {
                    if(question.answer.userAnswer == null || question.answer.userAnswer == true)
                    {
                        falseButton.setSelected(true);
                        trueButton.setSelected(false);
                        question.answer.userAnswer = false;
                    }
                    else
                    {
                        falseButton.setSelected(false);
                        question.answer.userAnswer = null;
                    }

                }
            );
        }
        else
        {
            trueButton.setEnabled(false);
            falseButton.setEnabled(false);

            if(question.answer.userAnswer != null)
            {
                trueButton.setSelected(question.answer.userAnswer);
                falseButton.setSelected(!question.answer.userAnswer);
            }
            trueButton.setCorrect(question.answer.correctAnswer);
            falseButton.setCorrect(!question.answer.correctAnswer);
        }

        /*
        View item = inflater.inflate(R.layout.item_truth_option, gridLayout, false);
        CheckBox truthOption = item.findViewById(R.id.truth_option);

        // 1. question number
        ((TextView) item.findViewById(R.id.truth_option_item_number))
            .setText("(" + (char) ('a' + question.number - 1) + ")");

        // 2. statement
        ((TextView) item.findViewById(R.id.truth_option))
            .setText(question.getStatement());


        // 3. restoring the answer if any
        if(question.answer.userAnswer != null)
            truthOption.setChecked(question.answer.userAnswer);

        truthOption.setEnabled(!readingMode);

        // 4. on check listener
        truthOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                question.answer.userAnswer = isChecked;
            }
        });

        gridLayout.addView(item);
         */
        trueButton.invalidate();
    }

    @Override
    public void visit(CheckBoxQuestion question)
    {
        gridLayout.setColumnCount(question.answers.length);
        gridLayout.setRowCount(1);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);
        for(int i = 0; i < question.answers.length; i++)
        {
            final QuestionCheckBox checkBox = new QuestionCheckBox(context);
            checkBox.setBackgroundResource(R.drawable.background_check_box);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.setMargins(CB_MARGIN, CB_MARGIN, CB_MARGIN, CB_MARGIN);
            params.columnSpec = GridLayout.spec(i, 1, GridLayout.FILL , 1f);
            params.rowSpec = GridLayout.spec(0, 1, GridLayout.CENTER, 1f);
            checkBox.setLayoutParams(params);

            // 1. question number
            //((TextView) item.findViewById(R.id.check_box_item_number))
            //    .setText("(" + (char) ('a' + i) + ")");

            // restoring the answer if any
            if (question.answers[i].userAnswer != null)
            {
                checkBox.setSelected(true);
                checkBox.setChecked(question.answers[i].userAnswer);
            }

            if(!readingMode)
            {
                // on click listener
                final int index = i;
                checkBox.setOnClickListener(
                    (View view) ->
                    {
                        checkBox.setSelected(true);
                        if (question.answers[index].userAnswer == null)
                        {
                            Log.d("checking check box", "null");
                            question.answers[index].userAnswer = true;
                            checkBox.setChecked(true);
                        }
                        else
                        {
                            question.answers[index].userAnswer = !question.answers[index].userAnswer;
                            checkBox.setChecked(question.answers[index].userAnswer);
                        }
                    }
                );
                checkBox.setOnLongClickListener(
                    (View view) ->
                    {
                        checkBox.setSelected(false);
                        question.answers[index].userAnswer = null;
                        return true;
                    }
                );
            }
            else
            {
                checkBox.setEnabled(false);
                checkBox.setSelected(true);
                if(question.answers[i].userAnswer == null)
                {
                    checkBox.setChecked(question.answers[i].correctAnswer);
                }
                else
                {
                    checkBox.setCorrect(question.answers[i].correct());
                }
            }
            gridLayout.addView(checkBox);
        }
    }

    @Override
    public void visit(IntervalInputQuestion question)
    {
        Score score = question.score();
        if(score.parts().length != 1 ||
            score.parts()[0].measures().length != 1 ||
            score.parts()[0].measures()[0].notes().length != 3)
        {
            throw new IllegalStateException("Invalid score from question.");
        }
        View panel = inflater.inflate(R.layout.item_note_input, gridLayout, false);
        ScoreView scoreView = panel.findViewById(R.id.note_input_score_view);
        scoreView.setFixedRatio(3f / 2f);
        scoreView.setScore(score);
        scoreView.setBackgroundResource(R.drawable.border);
        scoreView.invalidate();
        if(readingMode)
        {
            Measure measure = score.parts()[0].measures()[0];
            if(measure.notes()[1].equals(measure.notes()[2]))
            {
                measure.notes()[1].setColor(Color.GREEN);
                measure.notes()[2].setPrintObject(false);
            }
            else
            {
                measure.notes()[1].setColor(Color.RED);
                measure.notes()[2].setPrintObject(true);
                measure.notes()[2].setColor(Color.GREEN);
            }
        }
        else
        {
            PanelOnTouchListener listener = new PanelOnTouchListener(
                scoreView,
                question
            );
            panel.setOnTouchListener(listener);
        }
        ((TextView) panel.findViewById(R.id.required_interval_text_view))
            .setText(question.requiredInterval());
        gridLayout.addView(panel);
    }

    private ImageView retrieveImage(String title)
    {
        ImageView imageView = new ImageView(context);
        Bitmap bitmap = BitmapFactory.decodeFile(
            context.getFilesDir() + "/images/" + title + ".png"
        );
        if(bitmap == null)
        {
            bitmap = BitmapFactory.decodeFile(
                context.getFilesDir() + "/images/image_not_found.png"
            );
        };
        imageView.setImageBitmap(bitmap);
        //imageView.setImageResource(getResourceID(description.content, "drawable", context));
        imageView.setAdjustViewBounds(true);
        return imageView;
    }
}
