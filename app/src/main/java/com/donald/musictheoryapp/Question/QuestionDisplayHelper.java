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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.IOException;
import java.util.ArrayList;

import com.donald.musictheoryapp.BuildConfig;
import com.donald.musictheoryapp.Music.MusicXML.Measure;
import com.donald.musictheoryapp.Music.MusicXML.Score;
import com.donald.musictheoryapp.Music.ScoreView.ScoreView;
import com.donald.musictheoryapp.PanelOnTouchListener;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.Screen.Screen;
import com.donald.musictheoryapp.Utils.Button.ColorButton;
import com.donald.musictheoryapp.Utils.Button.ImageButton;
import com.donald.musictheoryapp.Utils.Button.QuestionButton;
import com.donald.musictheoryapp.Utils.Button.QuestionCheckBox;
import com.donald.musictheoryapp.Utils.Button.ScoreButton;
import com.donald.musictheoryapp.Utils.StringUtils;

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

    private static final int[] BUTTON_COLORS = new int[]
        {
            R.color.red_button_default,
            R.color.blue_button_default,
            R.color.green_button_default,
            R.color.yellow_button_default
        };

    private static final int BTN_MARGIN = 16;
    private static final int CB_MARGIN = 16;

    private final TextView sectionNumberView;
    private final TextView sectionNameView;
    private final TextView numberView;
    private final LinearLayout content;
    private final TextView panelHintView;
    private final TextView correctAnswerView;
    private final ArrayList<View> questionDescriptionViews;

    private Question currentQuestion;

    private final GridLayout inputGridLayout;
    private final GridLayout correctAnswerGridLayout;
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

        sectionNumberView = view.findViewById(R.id.question_section_number);
        sectionNameView = view.findViewById(R.id.question_section_name);
        numberView = view.findViewById(R.id.question_number);
        content = view.findViewById(R.id.question_content);
        panelHintView = view.findViewById(R.id.question_input_hint_text_view);
        correctAnswerView = view.findViewById(R.id.question_correct_answer_text_view);
        questionDescriptionViews = new ArrayList<>();

        currentQuestion = null;

        inputGridLayout = view.findViewById(R.id.question_input_grid_layout);
        correctAnswerGridLayout = view.findViewById(R.id.question_correct_answer_grid_layout);

        setReadingMode(false);
    }

    public void setReadingMode(boolean readingMode)
    {
        this.readingMode = readingMode;
    }

    public void displayQuestion(Question question)
    {
        // Setting up the question section and number
        StringBuilder numberStringBuilder = new StringBuilder();
        if(currentQuestion == null || currentQuestion.group.section != question.group.section)
        {
            sectionNumberView.setText(String.valueOf(question.group.section.number));
            sectionNameView.setText(question.group.section.name);
        }
        numberStringBuilder.append(context.getResources().getString(R.string.question_string))
            .append(" ")
            .append(question.group.number);
        if(question.group.questions.length > 1)
        {
            int questionSubNumber = question.number % 26;
            numberStringBuilder.append((char) (questionSubNumber - 1 + 'a'));
        }
        numberView.setText(numberStringBuilder.toString());

        // handling group descriptions
        if(currentQuestion == null || currentQuestion.group != question.group)
        {
            content.removeAllViews();
            Description[] descriptions = question.group.descriptions;
            for(Description description : descriptions)
                addDescription(description, false);
        }
        else
        {
            for(View view : questionDescriptionViews)
            {
                content.removeView(view);
            }
        }


        // handling question descriptions
        Description[] descriptions = question.descriptions;
        for(Description description : descriptions)
        {
            addDescription(description, true);
        }

        // handling panel hint
        if(question.inputHint != null)
        {
            panelHintView.setVisibility(View.VISIBLE);
            panelHintView.setText(question.inputHint);
        } else
        {
            panelHintView.setVisibility(View.GONE);
        }

        // handling input panel
        inputGridLayout.removeAllViews();
        correctAnswerGridLayout.removeAllViews();
        currentQuestion = question;
        question.acceptVisitor(this);

        if(readingMode && question.points() < question.maxPoints())
        {
            correctAnswerView.setVisibility(View.VISIBLE);
            correctAnswerGridLayout.setVisibility(View.VISIBLE);
        } else
        {
            correctAnswerView.setVisibility(View.GONE);
            correctAnswerGridLayout.setVisibility(View.GONE);
        }
    }


    
    private void addDescription(Description description, boolean isQuestionDescription)
    {
        switch(description.type)
        {
            case Description.TEXT_TYPE:
            case Description.TEXT_EMPHASIZE_TYPE:
            {
                TextView textView =
                    description.type == Description.TEXT_TYPE ?
                        (TextView) inflater.inflate(R.layout.description_text, content, false) :
                        (TextView) inflater.inflate(R.layout.description_text_emphasize, content, false);
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
        if(question.optionType == MultipleChoiceQuestion.OptionType.IMAGE)
        {
            multipleChoiceSetUpImageButtons(question, buttons);
        }
        else if(question.optionType == MultipleChoiceQuestion.OptionType.TEXT)
        {
            multipleChoiceSetUpColorButtons(question, buttons);
        }
        else
        {
            try
            {
                multipleChoiceSetUpScoreButtons(question, buttons);
            } catch(IOException | XmlPullParserException e)
            {
                e.printStackTrace();
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
                QuestionButton selectedButton = buttons[question.answer.userAnswer];
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
                buttons[question.answer.userAnswer].setStrokeColor(
                    ContextCompat.getColor(
                        context,
                        question.answer.correct() ?
                            R.color.question_button_stroke_correct :
                            R.color.question_button_stroke_default
                    )
                );
            }
            if(!question.answer.correct())
            {
                correctAnswerGridLayout.setColumnCount(1);
                correctAnswerGridLayout.setRowCount(1);
                TextView correctAnswerTextView = (TextView) inflater.inflate(
                    R.layout.text_correct_answer, correctAnswerGridLayout, false
                );
                correctAnswerGridLayout.addView(correctAnswerTextView);
                if(question.optionType == MultipleChoiceQuestion.OptionType.TEXT)
                {
                    correctAnswerTextView.setText(
                        question.options[question.answer.correctAnswer]
                    );
                }
                else
                {
                    correctAnswerTextView.setText(
                        String.valueOf(question.answer.correctAnswer + 1)
                    );
                }

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
            View item = inflater.inflate(R.layout.item_text_entry, inputGridLayout, false);
            EditText editText = item.findViewById(R.id.text_entry_item_text_entry);

            // changing the keyboard layout as appropriate
            if (question.inputType == TextInputQuestion.InputType.Number)
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            inputGridLayout.addView(item);

            // 2. restoring answer if any
            if(question.answers[i].userAnswer != null)
            {
                editText.setText(question.answers[i].userAnswer);
            }

            if(readingMode)
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

            if(question.points() < question.maxPoints())
            {
                correctAnswerGridLayout.setColumnCount(1);
                correctAnswerGridLayout.setRowCount(1);
                TextView correctAnswer = (TextView) inflater.inflate(
                    R.layout.text_correct_answer, correctAnswerGridLayout, false
                );
                StringBuilder builder = new StringBuilder();
                for(int answerIndex = 0; answerIndex < question.answers.length; answerIndex++)
                {
                    if(answerIndex == 0)
                    {
                        builder.append(question.answers[answerIndex].correctAnswer);
                    }
                    else
                    {
                        builder.append(", ").append(question.answers[answerIndex].correctAnswer);
                    }
                }
                correctAnswer.setText(builder.toString());
                correctAnswerGridLayout.addView(correctAnswer);
            }
        }
    }



    @Override
    public void visit(TruthQuestion question)
    {
        inputGridLayout.setColumnCount(2);
        inputGridLayout.setRowCount(1);

        ColorButton trueButton = new ColorButton(context);
        trueButton.setColor(
            ContextCompat.getColor(context, R.color.green_button_default)
        );
        trueButton.setFixedRatio(2f);
        ColorButton falseButton = new ColorButton(context);
        falseButton.setColor(
            ContextCompat.getColor(context, R.color.red_button_default)
        );
        falseButton.setFixedRatio(2f);

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

        inputGridLayout.addView(trueButton);
        inputGridLayout.addView(falseButton);

        TextView trueText =
            (TextView) inflater.inflate(R.layout.text_truth_question_button, trueButton, false);
        trueText.setText(R.string.true_button_text);
        trueButton.addView(trueText);
        TextView falseText =
            (TextView) inflater.inflate(R.layout.text_truth_question_button, falseButton, false);
        falseText.setText(R.string.false_button_text);
        falseButton.addView(falseText);

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

            correctAnswerGridLayout.setColumnCount(1);
            correctAnswerGridLayout.setRowCount(1);
            TextView correctAnswer = (TextView) inflater.inflate(
                R.layout.text_correct_answer, correctAnswerGridLayout, false
            );
            correctAnswer.setText(
                question.answer.correctAnswer ?
                R.string.true_button_text :
                R.string.false_button_text
            );
            correctAnswerGridLayout.addView(correctAnswer);
        }
    }



    @Override
    public void visit(CheckBoxQuestion question)
    {
        QuestionCheckBox[] checkBoxes = new QuestionCheckBox[question.answers.length];
        inputGridLayout.setColumnCount(question.answers.length);
        inputGridLayout.setRowCount(1);
        //inputGridLayout.setOrientation(GridLayout.HORIZONTAL);
        for(int i = 0; i < question.answers.length; i++)
        {
            final QuestionCheckBox checkBox = new QuestionCheckBox(context);
            checkBoxes[i] = checkBox;
            {
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.setMargins(CB_MARGIN, CB_MARGIN, CB_MARGIN, CB_MARGIN);
                params.columnSpec = GridLayout.spec(i, 1, GridLayout.FILL, 1f);
                params.rowSpec = GridLayout.spec(0, 1, GridLayout.CENTER, 1f);
                checkBox.setLayoutParams(params);
            }

            // 1. question number
            //((TextView) item.findViewById(R.id.check_box_item_number))
            //    .setText("(" + (char) ('a' + i) + ")");

            // restoring the answer if any
            if (question.answers[i].userAnswer != null)
            {
                checkBox.setSelected(true);
                checkBox.setChecked(question.answers[i].userAnswer);
            }

            inputGridLayout.addView(checkBox);
        }

        if(!readingMode)
        {
            for(int i = 0; i < checkBoxes.length; i++)
            {
                QuestionCheckBox checkBox = checkBoxes[i];
                // on click listener
                final int index = i;
                checkBox.setOnClickListener(
                    (View view) ->
                    {
                        checkBox.setSelected(true);
                        if(question.answers[index].userAnswer == null)
                        {
                            Log.d("checking check box", "null");
                            question.answers[index].userAnswer = true;
                            checkBox.setChecked(true);
                        } else
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
        }
        else
        {
            for(int i = 0; i < checkBoxes.length; i++)
            {
                QuestionCheckBox checkBox = checkBoxes[i];
                checkBox.setEnabled(false);
                checkBox.setSelected(question.answers[i].userAnswer != null);
                if(question.answers[i].userAnswer == null)
                {
                    checkBox.setChecked(question.answers[i].correctAnswer);
                }
                else
                {
                    checkBox.setCorrect(question.answers[i].correct());
                }
            }

            correctAnswerGridLayout.setColumnCount(question.answers.length);
            correctAnswerGridLayout.setRowCount(1);
            for(int i = 0; i < question.answers.length; i++)
            {
                final QuestionCheckBox correctCheckBox = new QuestionCheckBox(context);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.setMargins(CB_MARGIN, CB_MARGIN, CB_MARGIN, CB_MARGIN);
                params.columnSpec = GridLayout.spec(i, 1, GridLayout.FILL , 1f);
                params.rowSpec = GridLayout.spec(0, 1, GridLayout.CENTER, 1f);
                correctCheckBox.setLayoutParams(params);
                //correctCheckBox.setEnabled(false);
                correctCheckBox.setSelected(true);
                correctCheckBox.setChecked(question.answers[i].correctAnswer);
                correctAnswerGridLayout.addView(correctCheckBox);
            }
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
        View panel = inflater.inflate(R.layout.item_note_input, inputGridLayout, false);
        FrameLayout frame = panel.findViewById(R.id.note_input_score_frame);
        ScoreView scoreView = (ScoreView) inflater.inflate(R.layout.score_input, frame, false);
        frame.addView(scoreView);
        scoreView.setScore(score);
        if(readingMode)
        {
            ((TextView) panel.findViewById(R.id.note_input_tip_text_view)).setVisibility(View.GONE);
            if(!question.answer.correct())
            {
                Score correctScore = question.score().clone();
                Measure correctMeasure = correctScore.parts()[0].measures()[0];
                correctAnswerGridLayout.setColumnCount(1);
                correctAnswerGridLayout.setRowCount(1);
                ScoreView correctScoreView = (ScoreView) inflater.inflate(
                    R.layout.score_input, correctAnswerGridLayout, false
                );
                correctMeasure.notes()[1].setPrintObject(false);
                correctMeasure.notes()[2].setPrintObject(true);
                correctScoreView.setScore(correctScore);
                correctAnswerGridLayout.addView(correctScoreView);
            }
        }
        else
        {
            scoreView.setFixedRatio(3f / 2f);
            PanelOnTouchListener listener = new PanelOnTouchListener(
                scoreView,
                question
            );
            panel.setOnTouchListener(listener);
        }
        ((TextView) panel.findViewById(R.id.required_interval_text_view))
            .setText(StringUtils.capitalize(question.requiredInterval()));

        inputGridLayout.addView(panel);
    }



    private void multipleChoiceSetUpImageButtons(MultipleChoiceQuestion question, QuestionButton[] buttons)
    {
        int column;
        int row;
        float buttonRatio;
        Bitmap[] bitmaps = new Bitmap[question.options.length];
        for(int i = 0; i < bitmaps.length; i++)
        {
            bitmaps[i] = retrieveBitmap(question.options[i]);
        }

        buttonRatio = //Math.round(
            ((float) bitmaps[0].getWidth() / (float) bitmaps[0].getHeight());
        //);
        if(buttonRatio > 2f)
        {
            column = 1;
            row = question.options.length;
        }
        else if(question.options.length % 2 == 0 && buttonRatio > 0.7)
        {
            column = 2;
            row = (question.options.length + 1) / column;
        }
        else
        {
            column = question.options.length;
            row = 1;
        }


        //buttonRatio = Math.max(buttonRatio, 4f / 3f);
        //final int row = (question.options.length + 1) / column;
        inputGridLayout.setColumnCount(column);
        inputGridLayout.setRowCount(row);

        // Populating the list
        for (int i = 0; i < question.options.length; i++)
        {
            // 1. inflate view preset
            ImageButton button = new ImageButton(
                context,
                RoundedBitmapDrawableFactory.create(
                    context.getResources(),
                    bitmaps[i]
                )
            );
            if(readingMode) button.setNumber(i + 1);
            int columnSize;
            if(column == 2 && i == question.options.length - 1 &&
                question.options.length % 2 == 1)
            {
                columnSize = 2;
                button.setFixedRatio(buttonRatio * 2f);
            }
            else
            {
                columnSize = 1;
                button.setFixedRatio(buttonRatio);
            }
            GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
            buttonParams.width = 0;
            buttonParams.columnSpec = GridLayout.spec(i % column, columnSize, GridLayout.FILL, 1f);
            buttonParams.rowSpec = GridLayout.spec(i / column, 1, GridLayout.CENTER, 1f);
            buttonParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN);
            button.setLayoutParams(buttonParams);
            inputGridLayout.addView(button);

            // 2. setting up the content
            /*
            ImageView imageView = images[i];
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageView.setLayoutParams(params);
            button.addView(imageView);

             */
            buttons[i] = button;
        }
    }



    private void multipleChoiceSetUpColorButtons(
        MultipleChoiceQuestion question,
        QuestionButton[] buttons)
    {
        final int column = 2;
        final int row = (question.options.length + 1) / column;
        inputGridLayout.setColumnCount(column);
        inputGridLayout.setRowCount(row);
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
                ratio = 4f / 3f;
                break;
        }

        // Populating the list
        for (int i = 0; i < question.options.length; i++)
        {
            // 1. inflate view preset
            ColorButton button = new ColorButton(context);
            button.setColor(ContextCompat.getColor(
                context, BUTTON_COLORS[i % BUTTON_COLORS.length]
            ));
            int columnSize;
            if(i == question.options.length - 1 && question.options.length % 2 == 1)
            {
                columnSize = 2;
                button.setFixedRatio(ratio * 2f);
            }
            else
            {
                columnSize = 1;
                button.setFixedRatio(ratio);
            }
            GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
            buttonParams.width = 0;
            buttonParams.columnSpec = GridLayout.spec(i % column, columnSize, GridLayout.FILL, 1f);
            buttonParams.rowSpec = GridLayout.spec(i / column, 1, GridLayout.CENTER, 1f);
            buttonParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN);
            button.setLayoutParams(buttonParams);
            inputGridLayout.addView(button);

            // 2. setting up the content
            if(question.optionType == MultipleChoiceQuestion.OptionType.TEXT)
            {
                TextView textView = (TextView) inflater.inflate(
                    R.layout.text_color_button, button, false
                );
                textView.setText(question.options[i]);
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
    }


    private void multipleChoiceSetUpScoreButtons(
        MultipleChoiceQuestion question,
        QuestionButton[] buttons) throws IOException, XmlPullParserException
    {
        int column;
        int row;
        //if(question.options.length % 2 == 0)
        if(true)
        {
            column = 2;
            row = (question.options.length + 1) / column;
        }
        else
        {
            column = 1;
            row = question.options.length;
        }
        float ratio;
        switch(row)
        {
            case 1:
                ratio = 1f;
                break;
            case 2:
                ratio = 3f / 2f;
                break;
            default:
                ratio = 3f / 1f;
                break;
        }
        inputGridLayout.setColumnCount(column);
        inputGridLayout.setRowCount(row);

        // Populating the list
        for (int i = 0; i < question.options.length; i++)
        {
            // 1. inflate view preset
            ScoreButton button = new ScoreButton(context, Score.fromXML(question.options[i]));
            if(readingMode) button.setNumber(i + 1);
            int columnSize;
            if(i == question.options.length - 1 && question.options.length % 2 == 1)
            {
                columnSize = 1;
                button.setFixedRatio(ratio);
            }
            else
            {
                columnSize = 1;
                button.setFixedRatio(ratio);
            }
            GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
            buttonParams.width = 0;
            buttonParams.columnSpec = GridLayout.spec(i % column, columnSize, GridLayout.FILL, 1f);
            buttonParams.rowSpec = GridLayout.spec(i / column, 1, GridLayout.CENTER, 1f);
            buttonParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN);
            button.setLayoutParams(buttonParams);
            inputGridLayout.addView(button);
            buttons[i] = button;
        }
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

    private Bitmap retrieveBitmap(String title)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(
            context.getFilesDir() + "/images/" + title + ".png"
        );
        if(bitmap == null)
        {
            // not working
            bitmap = BitmapFactory.decodeFile(
                context.getFilesDir() + "/images/image_not_found.png"
            );
        };
        return bitmap;
    }
}
