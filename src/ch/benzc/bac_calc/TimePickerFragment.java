package ch.benzc.bac_calc;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	private static final String PREFS_TIME = "selected_time";
	private TimePickedListener mListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {		
		SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_TIME, 0);
		Calendar c = Calendar.getInstance();
		int hour = settings.getInt("sel_hour", c.get(Calendar.HOUR_OF_DAY));
		int minute = settings.getInt("sel_minute", c.get(Calendar.MINUTE));
		Log.i("Tag2", Integer.toString(settings.getInt("sel_hour", 888))+ " "+Integer.toString(settings.getInt("sel_minute", 888)));
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		// when the fragment is initially shown (i.e. attached to the activity), cast the activity to the callback interface type
		super.onAttach(activity);
		try
		{
			mListener = (TimePickedListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement " + TimePickedListener.class.getName());
		}
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Do something with the time chosen by the user

		// when the time is selected, send it to the activity via its callback interface method
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		
		mListener.onTimePicked(c);
	}

	public static interface TimePickedListener
	{
		public void onTimePicked(Calendar time);
	}
}