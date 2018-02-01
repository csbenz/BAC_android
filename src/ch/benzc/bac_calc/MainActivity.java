package ch.benzc.bac_calc;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import ch.benzc.bac_calc.TimePickerFragment.TimePickedListener;

public class MainActivity extends FragmentActivity implements TimePickedListener, OnItemSelectedListener{
	private static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREFS_TIME = "selected_time";
	private EditText quantity;
	private Button setTime;
	private Spinner spinnerUnit;
	private Spinner spinnerABV;
	private ListView drinkList;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setViews();
		Toast.makeText(MainActivity.this, "onCreate", Toast.LENGTH_SHORT).show();
	}

	protected void onResume(){
		super.onResume();

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		setTime.setText(settings.getString("saved_selectedTime",
				(String) DateFormat.format("HH:mm", Calendar.getInstance())));
		quantity.setText(settings.getString("saved_quantity", ""));
		Toast.makeText(MainActivity.this, "onResume", Toast.LENGTH_SHORT).show();
	}

	protected void onPause() {
		super.onPause();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor ed = settings.edit();
		ed.putString("saved_selectedTime", (String) setTime.getText());
		ed.putString("saved_quantity", quantity.getText().toString());

		ed.commit();

		Toast.makeText(MainActivity.this, "onPause", Toast.LENGTH_SHORT).show();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		
		getSharedPreferences(PREFS_NAME, 0).edit().clear().commit();
		getSharedPreferences("selected_time", 0).edit().clear().commit();
		
		Toast.makeText(MainActivity.this, "onDestroy", Toast.LENGTH_SHORT).show();
	}

	private void setViews(){
		quantity = (EditText) findViewById(R.id.quantity);
		spinnerUnit = (Spinner) findViewById(R.id.unit);
		spinnerABV = (Spinner) findViewById(R.id.abv);
		drinkList = (ListView) findViewById(R.id.drinkList);

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.units, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerUnit.setAdapter(adapter);

		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
				R.array.abv, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerABV.setAdapter(adapter2);
		spinnerABV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// An item was selected. You can retrieve the selected item using
				// parent.getItemAtPosition(pos)
				if(pos == 3){
					Toast.makeText(MainActivity.this, "onItemSelected", Toast.LENGTH_SHORT).show();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});


		setTime = (Button) findViewById(R.id.timePicker);
		//TODO Set actual time
		setTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("time", setTime.getText().toString());
				//Show the time picker dialog
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.setArguments(bundle);
				newFragment.show(getSupportFragmentManager(), "timePicker");
				//TODO ici envoyer Cal (setTime.getText)
			}
		});

		// Use the current time as the default time displayed in button
		String timeStringed = (String) DateFormat.format("HH:mm", Calendar.getInstance());
		setTime.setText(timeStringed);

	}

	public void onTimePicked(Calendar time)
	{
		String timeStringed = (String) DateFormat.format("HH:mm", time);
		setTime.setText(timeStringed);

		//TODO Ici on sauve l'heure selectionne dans un fichier preferences
		SharedPreferences settings = getSharedPreferences(PREFS_TIME, 0);
		SharedPreferences.Editor ed = settings.edit();
		ed.putInt("sel_hour", time.get(Calendar.HOUR_OF_DAY));
		ed.putInt("sel_minute", time.get(Calendar.MINUTE));
		ed.commit();
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)
		if(pos == 3){
			Toast.makeText(MainActivity.this, "onItemSelected Other", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//Close keyboard after EditText
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (v instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
			if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) { 

				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
			}
		}
		return ret;
	}
}
