package com.gmail.dima_daler.tajikrussianenglishdictionary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.gmail.dima_daler.tajikrussianenglishdictionary.utils.UIHelper;

public class TraslateActivity extends Activity {
	
	//string super global for shared preferences for entire app   
	public static String FAVOR_PREF = "favorite_pref";
	
	String wordForTraslate; //string coming form main activity 
	String traslation; // for now getting from TextView later from database
	CheckBox checkBoxSave;
//	TextView textVTraslation;
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traslate);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		wordForTraslate = intent.getStringExtra("wordForTraslate");
		traslation = intent.getStringExtra("traslation");		

		UIHelper.setTextForTextView(this, R.id.textView1, wordForTraslate);
		UIHelper.setTextForTextView(this, R.id.textVTraslation, traslation);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
