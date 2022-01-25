package com.donald.musictheoryapp.Question;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.donald.musictheoryapp.MainActivity;
import com.donald.musictheoryapp.QuestionArray.QuestionArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCache implements Question.QuestionVisitor
{
	public interface OnImageCachedListener
	{
		void onImageCached();
	}

	private final Context context;
	private final File directory;
	private final OnImageCachedListener listener;
	private int target = 0;

	public ImageCache(Context context, File directory, OnImageCachedListener listener)
	{
		this.context = context;
		this.directory = directory;
		this.listener = listener;
	}

	public void cacheImages(QuestionArray array)
	{
		for(int i = 0; i < array.groupCount(); i++)
		{
			for(Description description : array.groupAt(i).descriptions)
			{
				if(description.type == Description.IMAGE_TYPE)
				{
					downloadImage(description.content);
				}
			}
		}
		for(int i = 0; i < array.questionCount(); i++)
		{
			array.questionAt(i).acceptVisitor(this);
		}
	}

	@Override
	public void visit(MultipleChoiceQuestion question)
	{
		for(Description description : question.descriptions)
		{
			if(description.type == Description.IMAGE_TYPE)
			{
				downloadImage(description.content);
			}
		}
		for(String option : question.options)
		{
			downloadImage(option);
		}
	}

	@Override
	public void visit(TextInputQuestion question)
	{
		for(Description description : question.descriptions)
		{
			if(description.type == Description.IMAGE_TYPE)
			{
				downloadImage(description.content);
			}
		}
	}

	@Override
	public void visit(TruthQuestion question)
	{
		for(Description description : question.descriptions)
		{
			if(description.type == Description.IMAGE_TYPE)
			{
				downloadImage(description.content);
			}
		}
	}

	@Override
	public void visit(CheckBoxQuestion question)
	{
		for(Description description : question.descriptions)
		{
			if(description.type == Description.IMAGE_TYPE)
			{
				downloadImage(description.content);
			}
		}
	}

	@Override
	public void visit(IntervalInputQuestion question)
	{
		for(Description description : question.descriptions)
		{
			if(description.type == Description.IMAGE_TYPE)
			{
				downloadImage(description.content);
			}
		}
	}

	private void downloadImage(String title)
	{
		target++;
		File destination = new File(directory, title + ".png");
		if(!destination.exists())
		{
			ImageRequest request = new ImageRequest(
				MainActivity.URL + "images/" + title,
				response ->
				{
					try
					{
						destination.createNewFile();
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						response.compress(Bitmap.CompressFormat.PNG, 100, bos);
						FileOutputStream fos = new FileOutputStream(destination);
						fos.write(bos.toByteArray());
						fos.flush();
						fos.close();
					}
					catch (IOException e)
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
			Volley.newRequestQueue(context).add(request);
		}
	}
}
