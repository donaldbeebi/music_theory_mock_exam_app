package com.donald.musictheoryapp.question

import android.content.Context
import com.donald.musictheoryapp.music.MusicXML.Score.Companion.fromXml
import android.view.LayoutInflater
import com.donald.musictheoryapp.question.Question.QuestionVisitor
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.Utils.ImageViewOnDoubleClickListener
import com.donald.musictheoryapp.music.scoreview.ScoreView
import org.xmlpull.v1.XmlPullParserException
import com.donald.musictheoryapp.Utils.Button.QuestionButton
import androidx.core.content.ContextCompat
import android.os.Looper
import android.text.TextWatcher
import android.text.Editable
import com.donald.musictheoryapp.Utils.Button.ColorButton
import com.donald.musictheoryapp.Utils.Button.QuestionCheckBox
import com.donald.musictheoryapp.Utils.PanelOnTouchListener
import android.graphics.Bitmap
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import kotlin.Throws
import com.donald.musictheoryapp.Utils.Button.ScoreButton
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import com.donald.musictheoryapp.BuildConfig
import com.donald.musictheoryapp.Utils.Button.ImageButton
import com.donald.musictheoryapp.Utils.ScoreViewOnDoubleClickListener
import com.donald.musictheoryapp.Utils.StringUtils
import java.io.IOException
import java.lang.AssertionError
import java.lang.StringBuilder
import java.util.ArrayList

class QuestionDisplayHelper(private val context: Context, view: View, private val inflater: LayoutInflater) : QuestionVisitor {

    private class EditTextInputSaver(private val question: TextInputQuestion, private val editText: EditText, private val answerIndex: Int) : Runnable {
        override fun run() {
            question.answers[answerIndex].userAnswer = editText.text.toString()
        }
    }

    private val sectionNumberView: TextView
    private val sectionNameView: TextView
    private val numberView: TextView
    private val content: LinearLayout
    private val panelHintView: TextView
    private val correctAnswerView: TextView
    private val questionDescriptionViews: ArrayList<View>
    private val inputGridLayout: GridLayout
    private val correctAnswerGridLayout: GridLayout
    private val correctAnswerTextView: TextView

    private var currentQuestion: Question?
    private var readingMode = false

    fun displayQuestion(exercise: Exercise, question: Question, readingMode: Boolean) {
        this.readingMode = readingMode
        // Setting up the question section and number
        val numberStringBuilder = StringBuilder()
        //if (currentQuestion == null || currentQuestion.group.section !== question.group.section) {
        // updating section
        val currentQuestion = currentQuestion
        val section = exercise.sectionOf(question)
        val group = exercise.groupOf(question)
        if (currentQuestion == null || currentQuestion !in section) {
            sectionNumberView.text = section.number.toString()
            sectionNameView.text = section.name
        }
        numberStringBuilder.append(context.resources.getString(R.string.question_string))
            .append(" ")
            .append(group.number)
        if (group.questions.size > 1) {
            val questionSubNumber = question.number % 26
            numberStringBuilder.append((questionSubNumber - 1 + 'a'.code).toChar())
        }
        numberView.text = numberStringBuilder.toString()

        // handling group descriptions
        if (currentQuestion == null || currentQuestion !in group) {
            content.removeAllViews()
            val descriptions = group.descriptions
            for (description in descriptions) addDescription(description, false)
        } else {
            for (view in questionDescriptionViews) {
                content.removeView(view)
            }
        }

        // handling question descriptions
        val descriptions = question.descriptions
        for (description in descriptions) {
            addDescription(description, true)
        }

        // handling panel hint
        if (question.inputHint != null) {
            panelHintView.visibility = View.VISIBLE
            panelHintView.text = question.inputHint
        } else {
            panelHintView.visibility = View.GONE
        }

        // handling input panel
        inputGridLayout.removeAllViews()
        correctAnswerGridLayout.removeAllViews()
        this.currentQuestion = question
        question.acceptVisitor(this)

        if (readingMode) {
            correctAnswerView.visibility = View.VISIBLE
            correctAnswerGridLayout.visibility = View.VISIBLE
        } else {
            correctAnswerView.visibility = View.GONE
            correctAnswerGridLayout.visibility = View.GONE
        }
    }

    private fun addDescription(description: Description, isQuestionDescription: Boolean) {
        when (description.type) {
            Description.Type.TEXT, Description.Type.TEXT_EMPHASIZE -> {
                val textView = if (description.type == Description.Type.TEXT) {
                    inflater.inflate(R.layout.description_text, content, false) as TextView
                } else {
                    inflater.inflate(R.layout.description_text_emphasize, content, false) as TextView
                }
                textView.text = description.content
                content.addView(textView)
                if (isQuestionDescription) questionDescriptionViews.add(textView)
            }
            Description.Type.IMAGE -> {
                val imageScrollView = inflater.inflate(
                    R.layout.part_image_description, content, false
                ) as HorizontalScrollView
                val imageView = imageScrollView.findViewById<ImageView>(R.id.part_description_image)
                imageView.setImageBitmap(retrieveBitmap(description.content))
                val newLayoutParams = imageView.layoutParams
                newLayoutParams.width = content.width
                imageView.layoutParams = newLayoutParams
                imageView.setOnClickListener(ImageViewOnDoubleClickListener(imageView))
                content.addView(imageScrollView)
                if (isQuestionDescription) questionDescriptionViews.add(imageScrollView)
            }
            Description.Type.SCORE -> {
                try {
                    val scoreScrollView = inflater.inflate(
                        R.layout.part_score_description, content, false
                    ) as HorizontalScrollView
                    val scoreFrame = scoreScrollView.findViewById<RelativeLayout>(
                        R.id.part_description_score_frame
                    )
                    val score = fromXml(description.content)
                    val scoreView = ScoreView(context)
                    val params = RelativeLayout.LayoutParams(
                        content.width,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                    scoreView.layoutParams = params
                    scoreView.setScore(score)
                    scoreView.setOnClickListener(ScoreViewOnDoubleClickListener(scoreView))
                    //scoreView.setFixedRatio(3);
                    scoreFrame.addView(scoreView)
                    scoreView.post { Log.d("QuestionDisplayHelper", "Width = " + scoreView.width + " Height = " + scoreView.height) }
                    content.addView(scoreScrollView)
                    if (isQuestionDescription) questionDescriptionViews.add(scoreScrollView)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: XmlPullParserException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun visit(question: MultipleChoiceQuestion) {
        val buttons = when (question.optionType) {
            MultipleChoiceQuestion.OptionType.IMAGE -> imageButtons(question)
            MultipleChoiceQuestion.OptionType.TEXT -> colorButtons(question)
            MultipleChoiceQuestion.OptionType.SCORE -> scoreButtons(question)
        }

        if (!readingMode) {
            // Setting up onClick listeners
            for (i in question.options.indices) {
                buttons[i].setOnClickListener {
                    if (buttons[i].isSelected) {
                        // if the user is clicking an already selected button, deselect it
                        question.answer.userAnswer = -1
                        buttons[i].isSelected = false
                    } else {
                        // if the user is clicking a non-selected button, select it
                        question.answer.userAnswer = i
                        buttons[i].isSelected = true
                        for (j in question.options.indices) {
                            if (i != j) buttons[j].isSelected = false
                        }
                    }
                }
            }
            // Restoring answer if any
            question.answer.userAnswer?.let {
                val selectedButton = buttons[it]
                selectedButton.isSelected = true
                selectedButton.jumpDrawablesToCurrentState()
            }
        } else {
            // reading mode
            for (button in buttons) {
                button.isEnabled = false
            }
            val userAnswer = question.answer.userAnswer
            if (userAnswer != null) {
                buttons[userAnswer].isSelected = true
                if (question.answer.correct) {
                    buttons[userAnswer].setStrokeColor(
                        ContextCompat.getColor(context, R.color.question_button_stroke_correct)
                    )
                    correctAnswerTextView.setText(R.string.question_correct_string)
                }
            }
            if (!question.answer.correct) {
                correctAnswerTextView.setText(R.string.question_correct_answer_string)
                correctAnswerGridLayout.columnCount = 1
                correctAnswerGridLayout.rowCount = 1
                val correctAnswerView = inflater.inflate(
                    R.layout.text_correct_answer, correctAnswerGridLayout, false
                ) as TextView
                correctAnswerGridLayout.addView(correctAnswerView)
                if (question.optionType == MultipleChoiceQuestion.OptionType.TEXT) {
                    correctAnswerView.text = question.options[question.answer.correctAnswer]
                } else {
                    correctAnswerView.text = (question.answer.correctAnswer + 1).toString()
                }
            }
        }
    }

    override fun visit(question: TextInputQuestion) {
        val numberOfItems = question.answers.size
        for (i in 0 until numberOfItems) {
            // 1. populating the list
            val item = inflater.inflate(R.layout.item_text_entry, inputGridLayout, false)
            val editText = item.findViewById<EditText>(R.id.text_entry_item_text_entry)

            // changing the keyboard layout as appropriate
            if (question.inputType == TextInputQuestion.InputType.Number) editText.inputType = InputType.TYPE_CLASS_NUMBER
            inputGridLayout.addView(item)

            // 2. restoring answer if any
            if (question.answers[i].userAnswer != null) {
                editText.setText(question.answers[i].userAnswer)
            }

            // 3. setting up edit text saver
            val handler = Handler(Looper.getMainLooper())
            val saver = EditTextInputSaver(question, editText, i)
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    handler.removeCallbacks(saver)
                    handler.postDelayed(saver, 500)
                }
            })
            if (readingMode) {
                editText.isEnabled = false
                if (question.points < question.maxPoints) {
                    correctAnswerTextView.setText(R.string.question_correct_answer_string)
                    correctAnswerGridLayout.columnCount = 1
                    correctAnswerGridLayout.rowCount = 1
                    val correctAnswer = inflater.inflate(
                        R.layout.text_correct_answer, correctAnswerGridLayout, false
                    ) as TextView
                    val builder = StringBuilder()
                    for (answerIndex in question.answers.indices) {
                        if (answerIndex == 0) {
                            builder.append(question.answers[answerIndex].correctAnswer)
                        } else {
                            builder.append(", ").append(question.answers[answerIndex].correctAnswer)
                        }
                    }
                    correctAnswer.text = builder.toString()
                    correctAnswerGridLayout.addView(correctAnswer)
                } else {
                    correctAnswerTextView.setText(R.string.question_correct_string)
                }
            }
        }
    }

    override fun visit(question: TruthQuestion) {
        inputGridLayout.columnCount = 2
        inputGridLayout.rowCount = 1
        val trueButton = ColorButton(context)
        trueButton.setColor(
            ContextCompat.getColor(context, R.color.green_button_default)
        )
        trueButton.setFixedRatio(2f)
        val falseButton = ColorButton(context)
        falseButton.setColor(
            ContextCompat.getColor(context, R.color.red_button_default)
        )
        falseButton.setFixedRatio(2f)

        // params
        run {
            val trueParams = GridLayout.LayoutParams()
            trueParams.columnSpec = GridLayout.spec(0, 1, GridLayout.FILL, 1f)
            trueParams.rowSpec = GridLayout.spec(0, 1, GridLayout.CENTER, 1f)
            trueParams.width = 0
            trueParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN)
            trueButton.layoutParams = trueParams
            val falseParams = GridLayout.LayoutParams()
            falseParams.columnSpec = GridLayout.spec(1, 1, GridLayout.FILL, 1f)
            falseParams.rowSpec = GridLayout.spec(0, 1, GridLayout.CENTER, 1f)
            falseParams.width = 0
            falseParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN)
            falseButton.layoutParams = falseParams
        }
        inputGridLayout.addView(trueButton)
        inputGridLayout.addView(falseButton)
        val trueText = inflater.inflate(R.layout.text_truth_question_button, trueButton, false) as TextView
        trueText.setText(R.string.true_button_text)
        trueButton.addView(trueText)
        val falseText = inflater.inflate(R.layout.text_truth_question_button, falseButton, false) as TextView
        falseText.setText(R.string.false_button_text)
        falseButton.addView(falseText)
        if (!readingMode) {
            // restoring answer
            val userAnswer = question.answer.userAnswer
            if (userAnswer != null) {
                trueButton.isSelected = userAnswer
                falseButton.isSelected = !userAnswer
            }

            // listeners
            trueButton.setOnClickListener {
                if (userAnswer == null || userAnswer == false) {
                    trueButton.isSelected = true
                    falseButton.isSelected = false
                    question.answer.userAnswer = true
                } else {
                    trueButton.isSelected = false
                    question.answer.userAnswer = null
                }
            }
            falseButton.setOnClickListener {
                if (userAnswer == null || userAnswer == true) {
                    falseButton.isSelected = true
                    trueButton.isSelected = false
                    question.answer.userAnswer = false
                } else {
                    falseButton.isSelected = false
                    question.answer.userAnswer = null
                }
            }
        } else {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            val userAnswer = question.answer.userAnswer
            if (userAnswer != null) {
                if (userAnswer == true) {
                    trueButton.isSelected = true
                    falseButton.isSelected = false
                    if (question.answer.correct) {
                        trueButton.setStrokeColor(ContextCompat.getColor(context, R.color.question_button_stroke_correct))
                    }
                } else {
                    trueButton.isSelected = false
                    falseButton.isSelected = true
                    if (question.answer.correct) {
                        falseButton.setStrokeColor(ContextCompat.getColor(context, R.color.question_button_stroke_correct))
                    }
                }
            }
            if (question.answer.correct) {
                correctAnswerTextView.setText(R.string.question_correct_string)
            } else {
                correctAnswerTextView.setText(R.string.question_correct_answer_string)
                correctAnswerGridLayout.columnCount = 1
                correctAnswerGridLayout.rowCount = 1
                val correctAnswer = inflater.inflate(
                    R.layout.text_correct_answer, correctAnswerGridLayout, false
                ) as TextView
                correctAnswer.setText(
                    if (question.answer.correctAnswer) R.string.true_button_text else R.string.false_button_text
                )
                correctAnswerGridLayout.addView(correctAnswer)
            }
        }
    }

    override fun visit(question: CheckBoxQuestion) {
        inputGridLayout.columnCount = question.answers.size
        inputGridLayout.rowCount = 1
        //inputGridLayout.setOrientation(GridLayout.HORIZONTAL);
        val checkBoxes = Array(question.answers.size) { i ->
            val checkBox = QuestionCheckBox(context)
            run {
                val params = GridLayout.LayoutParams()
                params.width = 0
                params.setMargins(CB_MARGIN, CB_MARGIN, CB_MARGIN, CB_MARGIN)
                params.columnSpec = GridLayout.spec(i, 1, GridLayout.FILL, 1f)
                params.rowSpec = GridLayout.spec(0, 1, GridLayout.CENTER, 1f)
                checkBox.layoutParams = params
            }

            // 1. question number
            //((TextView) item.findViewById(R.id.check_box_item_number))
            //    .setText("(" + (char) ('a' + i) + ")");

            // restoring the answer if any
            val userAnswer = question.answers[i].userAnswer
            if (userAnswer != null) {
                checkBox.isSelected = true
                checkBox.isChecked = userAnswer
            }
            inputGridLayout.addView(checkBox)
            checkBox
        }
        if (!readingMode) {
            for (i in checkBoxes.indices) {
                val checkBox = checkBoxes[i]
                // on click listener
                checkBox.setOnClickListener {
                    checkBox.isSelected = true
                    val userAnswer = question.answers[i].userAnswer
                    if (userAnswer == null) {
                        question.answers[i].userAnswer = true
                        checkBox.isChecked = true
                    } else {
                        question.answers[i].userAnswer = !userAnswer
                        checkBox.isChecked = !userAnswer
                    }
                }
                checkBox.setOnLongClickListener {
                    checkBox.isSelected = false
                    question.answers[i].userAnswer = null
                    true
                }
            }
        } else {
            for (i in checkBoxes.indices) {
                val checkBox = checkBoxes[i]
                checkBox.isEnabled = false
                checkBox.isSelected = question.answers[i].userAnswer != null
                if (question.answers[i].userAnswer == null) {
                    checkBox.isChecked = question.answers[i].correctAnswer
                } else {
                    checkBox.isCorrect = question.answers[i].correct
                }
            }
            Log.d("QuestionDisplayHelper", "${question.points} and ${question.maxPoints}")
            if (question.points < question.maxPoints) {
                correctAnswerTextView.setText(R.string.question_correct_answer_string)
                correctAnswerGridLayout.columnCount = question.answers.size
                correctAnswerGridLayout.rowCount = 1
                for (i in question.answers.indices) {
                    val correctCheckBox = QuestionCheckBox(context)
                    val params = GridLayout.LayoutParams()
                    params.width = 0
                    params.setMargins(CB_MARGIN, CB_MARGIN, CB_MARGIN, CB_MARGIN)
                    params.columnSpec = GridLayout.spec(i, 1, GridLayout.FILL, 1f)
                    params.rowSpec = GridLayout.spec(0, 1, GridLayout.CENTER, 1f)
                    correctCheckBox.layoutParams = params
                    //correctCheckBox.setEnabled(false);
                    correctCheckBox.isSelected = true
                    correctCheckBox.isChecked = question.answers[i].correctAnswer
                    correctAnswerGridLayout.addView(correctCheckBox)
                }
            } else {
                correctAnswerTextView.setText(R.string.question_correct_string)
            }
        }
    }

    override fun visit(question: IntervalInputQuestion) {
        val score = question.score.clone()
        check(!(score.parts().size != 1 || score.parts()[0].measures().size != 1 || score.parts()[0].measures()[0].notes().size != 1)) { "Invalid score from question. length: " + score.parts()[0].measures()[0].notes().size }
        val inputNote = question.answer.userAnswer ?: score.parts()[0].measures()[0].notes()[0].clone().also { question.answer.userAnswer = it }
        score.parts()[0].measures()[0].notes().add(inputNote)
        val panel = inflater.inflate(R.layout.item_note_input, inputGridLayout, false)
        val frame = panel.findViewById<FrameLayout>(R.id.note_input_score_frame)
        val scoreView = inflater.inflate(R.layout.score_input, frame, false) as ScoreView
        scoreView.setScore(score)
        if (readingMode) {
            panel.findViewById<View>(R.id.note_input_tip_text_view).visibility = View.GONE
            if (!question.answer.correct) {
                correctAnswerTextView.setText(R.string.question_correct_answer_string)
                val correctScore = question.score.clone()
                correctAnswerGridLayout.columnCount = 1
                correctAnswerGridLayout.rowCount = 1
                val correctScoreView = inflater.inflate(
                    R.layout.score_input, correctAnswerGridLayout, false
                ) as ScoreView
                correctScore.parts()[0].measures()[0].notes().add(question.answer.correctAnswer)
                correctScoreView.setScore(correctScore)
                correctAnswerGridLayout.addView(correctScoreView)
            } else {
                correctAnswerTextView.setText(R.string.question_correct_string)
            }
        } else {
            //scoreView.setFixedRatio(3f / 2f);
            scoreView.inputMode = true
            val listener = PanelOnTouchListener(scoreView, score)
            panel.setOnTouchListener(listener)
        }
        (panel.findViewById<View>(R.id.required_interval_text_view) as TextView).text = StringUtils.capitalize(question.requiredInterval)
        frame.addView(scoreView)
        inputGridLayout.addView(panel)
    }

    private fun imageButtons(question: MultipleChoiceQuestion): Array<QuestionButton> {//Array<ImageButton> {
        val column: Int
        val row: Int
        val buttonRatio: Float
        val bitmaps = Array(question.options.size) { index ->
            retrieveBitmap(question.options[index])
        }

        buttonRatio = bitmaps[0].width.toFloat() / bitmaps[0].height.toFloat()
        if (buttonRatio > 2f) {
            column = 1
            row = question.options.size
        } else if (question.options.size % 2 == 0 && buttonRatio > 0.7) {
            column = 2
            row = (question.options.size + 1) / column
        } else {
            column = question.options.size
            row = 1
        }

        inputGridLayout.columnCount = column
        inputGridLayout.rowCount = row

        // Populating the list
        return Array(bitmaps.size) { index ->
            // 1. inflate view preset
            val button = ImageButton(
                context,
                RoundedBitmapDrawableFactory.create(
                    context.resources,
                    bitmaps[index]
                )
            )
            if (readingMode) button.setNumber(index + 1)
            val columnSize: Int
            if (column == 2 && index == question.options.size - 1 && question.options.size % 2 == 1) {
                columnSize = 2
                button.setFixedRatio(buttonRatio * 2f)
            } else {
                columnSize = 1
                button.setFixedRatio(buttonRatio)
            }
            val buttonParams = GridLayout.LayoutParams()
            buttonParams.width = 0
            buttonParams.columnSpec = GridLayout.spec(index % column, columnSize, GridLayout.FILL, 1f)
            buttonParams.rowSpec = GridLayout.spec(index / column, 1, GridLayout.CENTER, 1f)
            buttonParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN)
            button.layoutParams = buttonParams
            inputGridLayout.addView(button)
            button
        }
    }

    private fun colorButtons(question: MultipleChoiceQuestion): Array<QuestionButton> {
        val column = 2
        val row = (question.options.size + 1) / column
        inputGridLayout.columnCount = column
        inputGridLayout.rowCount = row
        val ratio: Float = when (row) {
            2 -> 3f / 2f
            3 -> 2f / 1f
            else -> 4f / 3f
        }

        // Populating the list
        return Array(question.options.size) { index ->
            // 1. inflate view preset
            val button = ColorButton(context)
            button.setColor(
                ContextCompat.getColor(
                    context, BUTTON_COLORS[index % BUTTON_COLORS.size]
                )
            )
            val columnSize: Int
            if (index == question.options.size - 1 && question.options.size % 2 == 1) {
                columnSize = 2
                button.setFixedRatio(ratio * 2f)
            } else {
                columnSize = 1
                button.setFixedRatio(ratio)
            }
            val buttonParams = GridLayout.LayoutParams()
            buttonParams.width = 0
            buttonParams.columnSpec = GridLayout.spec(index % column, columnSize, GridLayout.FILL, 1f)
            buttonParams.rowSpec = GridLayout.spec(index / column, 1, GridLayout.CENTER, 1f)
            buttonParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN)
            button.layoutParams = buttonParams
            inputGridLayout.addView(button)

            // 2. setting up the content
            if (question.optionType == MultipleChoiceQuestion.OptionType.TEXT) {
                val textView = inflater.inflate(
                    R.layout.text_color_button, button, false
                ) as TextView
                textView.text = question.options[index]
                button.addView(textView)
            } else if (question.optionType == MultipleChoiceQuestion.OptionType.SCORE) {
                val scoreView = ScoreView(context)
                try {
                    scoreView.setScore(fromXml(question.options[index]))
                    val params = RelativeLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.addRule(RelativeLayout.CENTER_IN_PARENT)
                    scoreView.setFixedRatio(1.5f)
                    scoreView.layoutParams = params
                    button.addView(scoreView)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: XmlPullParserException) {
                    e.printStackTrace()
                }
            }
            button
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun scoreButtons(question: MultipleChoiceQuestion): Array<QuestionButton> {
        val column: Int
        val row: Int
        //if(question.options.length % 2 == 0)
        if (true) {
            column = 2
            row = (question.options.size + 1) / column
        } else {
            column = 1
            row = question.options.size
        }
        val ratio: Float = when (row) {
            1 -> 1f
            2 -> 3f / 2f
            else -> 3f / 1f
        }
        inputGridLayout.columnCount = column
        inputGridLayout.rowCount = row

        // Populating the list
        return Array(question.options.size) { index ->
            // 1. inflate view preset
            val button = ScoreButton(context, fromXml(question.options[index]))
            if (readingMode) button.setNumber(index + 1)
            val columnSize: Int
            if (index == question.options.size - 1 && question.options.size % 2 == 1) {
                columnSize = 1
                button.setFixedRatio(ratio)
            } else {
                columnSize = 1
                button.setFixedRatio(ratio)
            }
            val buttonParams = GridLayout.LayoutParams()
            buttonParams.width = 0
            buttonParams.columnSpec = GridLayout.spec(index % column, columnSize, GridLayout.FILL, 1f)
            buttonParams.rowSpec = GridLayout.spec(index / column, 1, GridLayout.CENTER, 1f)
            buttonParams.setMargins(BTN_MARGIN, BTN_MARGIN, BTN_MARGIN, BTN_MARGIN)
            button.layoutParams = buttonParams
            inputGridLayout.addView(button)
            button
        }
    }

    private fun retrieveImage(title: String): ImageView {
        val imageView = ImageView(context)
        var bitmap = BitmapFactory.decodeFile(
            context.filesDir.toString() + "/images/" + title + ".png"
        )
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile(context.filesDir.toString() + "/images/image_not_found.png")
        }
        imageView.setImageBitmap(bitmap)
        return imageView
    }

    private fun retrieveBitmap(title: String): Bitmap {
        return BitmapFactory.decodeFile(context.filesDir.toString() + "/images/" + title + ".png")
            ?: BitmapFactory.decodeFile(context.filesDir.toString() + "/images/image_not_found.png")
    }

    init {
        if (BuildConfig.DEBUG && view.tag.toString() != "question_layout") {
            throw AssertionError(
                "View passed into question display unit loader is not a question screen layout."
            )
        }
        sectionNumberView = view.findViewById(R.id.question_section_number)
        sectionNameView = view.findViewById(R.id.question_section_name)
        numberView = view.findViewById(R.id.question_number)
        content = view.findViewById(R.id.question_content)
        panelHintView = view.findViewById(R.id.question_input_hint_text_view)
        correctAnswerView = view.findViewById(R.id.question_correct_answer_text_view)
        questionDescriptionViews = ArrayList()
        currentQuestion = null
        inputGridLayout = view.findViewById(R.id.question_input_grid_layout)
        correctAnswerGridLayout = view.findViewById(R.id.question_correct_answer_grid_layout)
        correctAnswerTextView = view.findViewById(R.id.question_correct_answer_text_view)
    }

    companion object {

        private val BUTTON_COLORS = intArrayOf(
            R.color.red_button_default,
            R.color.blue_button_default,
            R.color.green_button_default,
            R.color.yellow_button_default
        )
        private const val BTN_MARGIN = 16
        private const val CB_MARGIN = 16

    }

}