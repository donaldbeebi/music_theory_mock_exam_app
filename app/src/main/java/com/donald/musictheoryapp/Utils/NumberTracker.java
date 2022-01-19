package com.donald.musictheoryapp.Utils;

import android.util.Log;

public class NumberTracker
{
	public interface OnIncrementListener
	{
		void onIncrement(NumberTracker tracker);
	}

	public interface OnTargetListener
	{
		void onTarget(NumberTracker tracker);
	}

	private int count;
	private final int target;
	private final OnIncrementListener onIncrementListener;
	private final OnTargetListener onTargetListener;

	public NumberTracker(
		int target,
		OnIncrementListener onIncrementListener,
		OnTargetListener onTargetListener
		)
	{
		count = 0;
		if(target == 0) onTargetListener.onTarget(this);
		this.target = target;
		if(target < 0) throw new IllegalArgumentException();
		this.onIncrementListener = onIncrementListener;
		this.onTargetListener = onTargetListener;
	}

	public int count()
	{
		return count;
	}

	public int target()
	{
		return target;
	}

	public void increment()
	{
		count++;
		onIncrementListener.onIncrement(this);
		if(target == count) onTargetListener.onTarget(this);
		Log.d("inside number tracker", "hi");
	}
}
