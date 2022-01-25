package com.donald.musictheoryapp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.Screen.FinishExerciseConfirmationDialog;
import com.donald.musictheoryapp.Utils.NumberTracker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.donald.musictheoryapp.Screen.ResultOverviewScreen;
import com.donald.musictheoryapp.Screen.ExerciseMenuScreen;
import com.donald.musictheoryapp.Screen.QuestionScreen;
import com.donald.musictheoryapp.Screen.Screen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
    implements BottomNavigationView.OnNavigationItemSelectedListener,
    ExerciseMenuScreen.OnStartExerciseListener,
    QuestionScreen.OnFinishExerciseListener,
    QuestionScreen.OnReturnToOverviewListener,
    FinishExerciseConfirmationDialog.OnConfirmDialogListener,
    ResultOverviewScreen.OnProceedToDetailListener
{
    public static final String URL = "http://161.81.107.94/";

    private ExerciseMenuScreen exerciseMenuScreen;
    private QuestionScreen questionScreen;
    private ResultOverviewScreen resultOverviewScreen;
    private Screen currentScreenForExerciseTab;
    private TextView exerciseMenuStatusTextView;

    private ViewGroup mainFrame;

    //private ExerciseGenerator m_ExerciseGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((BottomNavigationView) findViewById(R.id.bottom_navigation_view)).setOnNavigationItemSelectedListener(this);

        mainFrame = findViewById(R.id.main_frame_layout);

        exerciseMenuScreen = new ExerciseMenuScreen(this, getLayoutInflater().inflate(R.layout.screen_exercise_menu, null), this);
        exerciseMenuStatusTextView = exerciseMenuScreen.getView().findViewById(R.id.exercise_menu_status_text_view);
        questionScreen = new QuestionScreen(this, getLayoutInflater().inflate(R.layout.screen_question, null), this, this);
        resultOverviewScreen = new ResultOverviewScreen(this, getLayoutInflater().inflate(R.layout.screen_result_overview, null), this);

        currentScreenForExerciseTab = exerciseMenuScreen;
        mainFrame.addView(currentScreenForExerciseTab.getView());

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                if(currentScreenForExerciseTab == questionScreen)
                    questionScreen.onBackPressed();
            }
        });
    }

    /*
     * *********
     * CALLBACKS
     * *********
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.exercise_nav_button)
        {
            mainFrame.removeAllViews();
            mainFrame.addView(currentScreenForExerciseTab.getView());
        }

        else
        {
            mainFrame.removeAllViews();
            mainFrame.addView(resultOverviewScreen.getView());
        }

        return true;
    }

    @Override
    public void onStartExercise()
    {
        exerciseMenuStatusTextView.setText(R.string.contacting_server_status);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
            Request.Method.GET,
            URL + "exercise",
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    try
                    {
                        JSONObject object = new JSONObject(response);
                        JSONArray images = object.getJSONArray("images");
                        NumberTracker numberTracker = new NumberTracker(
                            images.length(),
                            (tracker) ->
                            {
                                exerciseMenuStatusTextView.setText(
                                    getString(
                                        R.string.image_download_status,
                                        tracker.count(),
                                        tracker.target()
                                    )
                                );
                            },
                            (tracker) ->
                            {
                                try
                                {
                                    onRetrieveQuestions(object);
                                }
                                catch(JSONException e)
                                {
                                    e.printStackTrace();
                                    exerciseMenuStatusTextView.setText(R.string.json_error_status);
                                }
                                catch(IOException | XmlPullParserException e)
                                {
                                    e.printStackTrace();
                                    exerciseMenuStatusTextView.setText(R.string.xml_error_status);
                                }
                            }
                        );
                        for(int i = 0; i < images.length(); i++)
                        {
                            downloadImage(images.getString(i), numberTracker);
                        }
                    }
                    catch(JSONException e)
                    {
                        exerciseMenuStatusTextView.setText(R.string.volley_error_status);
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.d("from string request", "error: " + error.getMessage());
                    exerciseMenuStatusTextView.setText(R.string.server_error_status);
                }
            });

        queue.add(request);
    }

    public void downloadImage(String title, NumberTracker tracker)
    {
        File dir = new File(getFilesDir(), "images");
        if(!dir.exists()) dir.mkdir();

        File destination = new File(dir, title + ".png");
        ImageRequest request = new ImageRequest(
            MainActivity.URL + "images/" + title,
            response -> {
                try
                {
                    destination.createNewFile();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    response.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    FileOutputStream fos = new FileOutputStream(destination);
                    fos.write(bos.toByteArray());
                    fos.flush();
                    fos.close();
                    tracker.increment();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            },
            2000,
            2000,
            ImageView.ScaleType.CENTER,
            Bitmap.Config.RGB_565,
            error -> Log.d("Volley error while fetch image " + title, error.toString())
        );
        Volley.newRequestQueue(this).add(request);
    }

    public void onImagesReady()
    {

    }

    public void onRetrieveQuestions(JSONObject object) throws JSONException, IOException, XmlPullParserException
    {
        mainFrame.removeAllViews();
        QuestionArray questions = QuestionArray.fromJSON(object);
        questionScreen.setQuestions(questions);
        //m_QuestionScreen.setQuestions(m_ExerciseGenerator.generateExercise());
        questionScreen.startExercise();
        questionScreen.startTimer();
        currentScreenForExerciseTab = questionScreen;
        mainFrame.addView(currentScreenForExerciseTab.getView());
        exerciseMenuStatusTextView.setText(R.string.exercise_menu_start_button_default_text);
    }

    @Override
    public void onFinishExercise()
    {
        FinishExerciseConfirmationDialog dialog = new FinishExerciseConfirmationDialog(this);
        dialog.show(getSupportFragmentManager(), "exercise_finish_confirmation_dialog");
    }

    @Override
    public void onConfirmDialog()
    {
        mainFrame.removeAllViews();
        resultOverviewScreen.setQuestions(questionScreen.getQuestions());
        currentScreenForExerciseTab = resultOverviewScreen;
        mainFrame.addView(currentScreenForExerciseTab.getView());
    }

    @Override
    public void onProceedToDetail(QuestionArray questions, int targetGroup)
    {
        mainFrame.removeAllViews();
        questionScreen.setQuestions(questions);
        questionScreen.displayQuestion(questions.questionIndexOf(questions.groupAt(targetGroup).getQuestion(0)));
        currentScreenForExerciseTab = questionScreen;
        mainFrame.addView(currentScreenForExerciseTab.getView());
    }

    @Override
    public void onReturnToOverview()
    {
        mainFrame.removeAllViews();
        currentScreenForExerciseTab = resultOverviewScreen;
        mainFrame.addView(currentScreenForExerciseTab.getView());
    }
}