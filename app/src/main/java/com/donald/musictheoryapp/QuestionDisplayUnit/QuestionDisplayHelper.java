package com.donald.musictheoryapp.QuestionDisplayUnit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.ArrayList;

import com.donald.musictheoryapp.BuildConfig;
import com.donald.musictheoryapp.Question.CheckBoxQuestion;
import com.donald.musictheoryapp.Question.Description;
import com.donald.musictheoryapp.Question.MultipleChoiceQuestion;
import com.donald.musictheoryapp.Question.Question;
import com.donald.musictheoryapp.Question.TextInputQuestion;
import com.donald.musictheoryapp.Question.TruthQuestion;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.Screen.Screen;

public class QuestionDisplayHelper implements Question.QuestionVisitor
{
    private static class EditTextInputSaver implements Runnable
    {
        private final Question m_Question;
        private final EditText m_EditText;
        private final int m_AnswerIndex;

        EditTextInputSaver(Question question, EditText editText, int answerIndex)
        { m_Question = question; m_EditText = editText; m_AnswerIndex = answerIndex; }

        @Override
        public void run() { m_Question.setAnswer(m_AnswerIndex, m_EditText.getText().toString()); }
    }
    private final TextView m_SectionView;
    private final TextView m_NumberView;
    private final LinearLayout m_Content;
    private final ArrayList<View> m_QuestionDescriptionViews;

    private Question m_CurrentQuestion;

    private final TextView m_TitleView;
    private final ImageView m_ImageView;
    private final TextView m_DescriptionView;
    private final LinearLayout m_List;
    private final Context m_Context;
    private final LayoutInflater m_Inflater;
    private boolean m_ReadingMode;


    public QuestionDisplayHelper(Screen screen)
    {
        final View view = screen.getView();
        m_Context = screen.getContext();
        m_Inflater = screen.getLayoutInflater();

        if (BuildConfig.DEBUG && !view.getTag().toString().equals("question_layout"))
            throw new AssertionError("View passed into question display unit loader is not a question screen layout.");

        m_SectionView = view.findViewById(R.id.question_section);
        m_NumberView = view.findViewById(R.id.question_number);
        m_Content = view.findViewById(R.id.question_content);
        m_QuestionDescriptionViews = new ArrayList<>();

        m_CurrentQuestion = null;

        m_TitleView = view.findViewById(R.id.question_title);
        m_ImageView = view.findViewById(R.id.question_image);
        m_DescriptionView = view.findViewById(R.id.question_description);
        m_List = view.findViewById(R.id.question_answer_item_list);
        m_ReadingMode = false;
    }

    public void setReadingMode(boolean readingMode)
    {
        m_ReadingMode = readingMode;
    }

    public void displayQuestion(Question question)
    {
        // 1. Setting up the question section and number
        StringBuilder numberStringBuilder = new StringBuilder();
        m_SectionView.setText(1 + "  " + "IMPLEMENT SECTION NAME");
        numberStringBuilder.append(question.getGroup().getNumber());

        if(question.getGroup().getQuestions().length > 1)
        {
            int questionSubNumber = question.getNumber() % 26;
            numberStringBuilder.append((char) (questionSubNumber  - 1 + 'a'));
        }

        numberStringBuilder.append(')');
        m_NumberView.setText(numberStringBuilder.toString());

        // adding the descriptions


        /*
        // 2. Setting up the question title
        m_TitleView.setText(unit.getTitle());

        // 3. Setting up the question image
        if(unit.getImage() != null)
        {
            m_ImageView.setVisibility(View.VISIBLE);
            m_ImageView.setImageResource(getResourceID(unit.getImage(), "drawable", m_Context));
        }
        else m_ImageView.setVisibility(View.GONE);

        // 4. Setting up the question description
        m_DescriptionView.setText(unit.getDescription());

         */
        // handling group descriptions
        if(m_CurrentQuestion == null || m_CurrentQuestion.getGroup() != question.getGroup())
        {
            m_Content.removeAllViews();
            Description[] descriptions = question.getGroup().getDescriptions();
            for(Description description : descriptions)
                addDescription(description, false);
        }
        else for(View view : m_QuestionDescriptionViews) m_Content.removeView(view);

        // handling question descriptions
        Description[] descriptions = question.getDescriptions();
        for(Description description : descriptions)
            addDescription(description, true);

        m_List.removeAllViews();
        // handling input panel
        question.acceptVisitor(this);

        m_CurrentQuestion = question;
    }

    private void addDescription(Description description, boolean isQuestionDescription)
    {
        switch(description.type)
        {
            case Description.TEXT_TYPE:
            {
                TextView textView = new TextView(m_Context);
                textView.setText(description.content);
                m_Content.addView(textView);
                if(isQuestionDescription) m_QuestionDescriptionViews.add(textView);
                break;
            }
            case Description.IMAGE_TYPE:
            {
                ImageView imageView = new ImageView(m_Context);
                imageView.setImageResource(getResourceID(description.content, "drawable", m_Context));
                imageView.setAdjustViewBounds(true);
                m_Content.addView(imageView);
                if(isQuestionDescription) m_QuestionDescriptionViews.add(imageView);
                break;
            }
        }
    }

    @Override
    public void visit(MultipleChoiceQuestion question)
    {
        String[] options = question.getOptions();
        int optionNumber = options.length;

        AppCompatRadioButton[] radioButtons = new AppCompatRadioButton[optionNumber];

        // Populating the list
        for (int i = 0; i < optionNumber; i++)
        {
            // 1. inflate view preset
            View item = m_Inflater.inflate(R.layout.item_multiple_choice_option, m_List, false);

            // 2. setting up the number
            ((TextView) item.findViewById(R.id.multiple_choice_item_number)).setText("(" + (i + 1) + ")");

            // 3. setting up the content
            FrameLayout frame = item.findViewById(R.id.multiple_choice_item_content);
            // first arg: item type (image / text)
            // second arg: item content (image name / text description)
            // TODO: USE REUSABLE XML ATTRIBUTE SET (MAYBE NOT LOL)
            if (question.getOptionType() == MultipleChoiceQuestion.OptionType.Text)
            {
                TextView content = new TextView(m_Context);
                content.setText(question.getOptions()[i]);
                content.setGravity(Gravity.CENTER);
                content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                frame.addView(content);
            }
            else
            {
                ImageView content = new ImageView(m_Context);
                content.setImageResource(getResourceID(question.getOptions()[i], "drawable", m_Context));
                content.setAdjustViewBounds(true);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                content.setLayoutParams(params);
                frame.addView(content);
            }

            // 4. getting a reference of the radio button for later set-up
            AppCompatRadioButton button = item.findViewById(R.id.multiple_choice_item_button);
            radioButtons[i] = button;

            // 5. disabling the buttons if showing results
            if(m_ReadingMode) button.setEnabled(false);

            m_List.addView(item);
        }

        if(m_ReadingMode)
        {
            // 1. highlighting the user selected button
            if(question.getAnswer() != null)
            {
                int color;
                if (question.isCorrect(0)) color = Color.GREEN;
                else color = Color.RED;

                AppCompatRadioButton selectedButton = radioButtons[Integer.parseInt(question.getAnswer()) - 1];
                selectedButton.setChecked(true);
                selectedButton.jumpDrawablesToCurrentState();

                ColorStateList selectedButtonStateList = new ColorStateList(
                    new int[][]{
                        new int[]{android.R.attr.state_checked},
                        //new int[] { -android.R.attr.state_checked }
                    },
                    new int[]{
                        color
                    }
                );
                selectedButton.setButtonTintList(selectedButtonStateList);
            }

            // 2. highlighting the correct button if the user is wrong
            // TODO: MESSAGE HIGHLIGHTED IN RED SAYING NO OPTIONS WERE CHOSEN
            if(!question.isCorrect(0))
            {
                ColorStateList correctButtonStateList = new ColorStateList(
                    new int[][] {
                        new int[] { -android.R.attr.state_checked },
                        //new int[] { -android.R.attr.state_checked }
                    },
                    new int[] {
                        Color.GREEN
                    }
                );
                radioButtons[Integer.parseInt(question.getCorrectAnswer()) - 1].setButtonTintList(correctButtonStateList);
            }
        }
        // Restoring answer if any
        else if (question.getAnswer() != null)
        {
            AppCompatRadioButton selectedButton = radioButtons[Integer.parseInt(question.getAnswer()) - 1];
            selectedButton.setChecked(true);
            selectedButton.jumpDrawablesToCurrentState();
        }

        //Setting up onClick listeners
        for (int i = 0; i < optionNumber; i++)
        {
            final int index = i;
            radioButtons[i].setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //Recording the answer
                    question.setAnswer(0, String.valueOf(index + 1));
                    //Unchecking all other buttons
                    for (int j = 0; j < optionNumber; j++)
                    {
                        if (index != j)
                        {
                            radioButtons[j].setChecked(false);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void visit(TextInputQuestion question)
    {
        int numberOfItems = question.getNumberOfAnswers();
        for (int i = 0; i < numberOfItems; i++)
        {
            // 1. populating the list
            View item = m_Inflater.inflate(R.layout.item_text_entry, m_List, false);
            ((TextView) item.findViewById(R.id.text_entry_item_number))
                .setText("(" + (char) ('a' + i) + ")");
            EditText editText = item.findViewById(R.id.text_entry_item_text_entry);

            // changing the keyboard layout as appropriate
            if (question.getInputType() == TextInputQuestion.InputType.Number)
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            m_List.addView(item);

            // 2. restoring answer if any
            if (question.getAnswer() != null)
            {
                editText.setText(question.getAnswer(i));
            }

            if (m_ReadingMode)
            {
                editText.setEnabled(false);
                if (question.isCorrect(i))
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

        View item = m_Inflater.inflate(R.layout.item_truth_option, m_List, false);
        CheckBox truthOption = item.findViewById(R.id.truth_option);

        // 1. question number
        ((TextView) item.findViewById(R.id.truth_option_item_number))
            .setText("(" + (char) ('a' + question.getNumber() - 1) + ")");

        // 2. restoring the answer if any
        if(question.getAnswer() != null)
            truthOption.setChecked(question.getAnswer().equals(TruthQuestion.TRUE_ANSWER));

        truthOption.setEnabled(!m_ReadingMode);

        // 3. on check listener
        truthOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked) question.setAnswer(0, TruthQuestion.TRUE_ANSWER);
                else question.setAnswer(0, TruthQuestion.FALSE_ANSWER);
            }
        });

        m_List.addView(item);
    }

    @Override
    public void visit(CheckBoxQuestion question)
    {
        int numberOfItems = question.getNumberOfAnswers();
        for(int i = 0; i < numberOfItems; i++)
        {
            View item = m_Inflater.inflate(R.layout.item_check_box, m_List, false);
            CheckBox checkBox = item.findViewById(R.id.check_box);

            // 1. question number
            ((TextView) item.findViewById(R.id.check_box_item_number))
                .setText("(" + (char) ('a' + i) + ")");

            // 2. restoring the answer if any
            if (question.getAnswer() != null)
                checkBox.setChecked(question.getAnswer(i).equals(CheckBoxQuestion.CHECK_ANSWER));

            checkBox.setEnabled(!m_ReadingMode);

            // 3. on check listener
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (isChecked) question.setAnswer(0, CheckBoxQuestion.CHECK_ANSWER);
                    else question.setAnswer(0, CheckBoxQuestion.CROSS_ANSWER);
                }
            });

            m_List.addView(item);
        }
    }

    private static int getResourceID (final String resName, final String resType, final Context ctx)
    {
        final int ResourceID = ctx.getResources().getIdentifier(resName, resType, ctx.getApplicationInfo().packageName);

        if (ResourceID == 0)
        {
            throw new IllegalArgumentException("No resource string found with name " + resName);
        }
        else
        {
            return ResourceID;
        }
    }
}
