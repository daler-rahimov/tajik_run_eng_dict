package com.gmail.dima_daler.tajikrussianenglishdictionary;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.gmail.dima_daler.tajikrussianenglishdictionary.db.DictionaryDBOpenHelper;
import com.gmail.dima_daler.tajikrussianenglishdictionary.db.DictionaryDataSource;
import com.gmail.dima_daler.tajikrussianenglishdictionary.words.Word;


public class MainActivity extends ListActivity {
	List<Word> listWords;
	DictionaryDataSource mDbHelper;/*data source class object 
	  		used to open data base and get data from it */
	ArrayAdapter<Word> adapter;
	Cursor cursor;/* Cursor for data base */

	private boolean isSearchOpened;
	private String mSearchQuery = "";// holds current text in the search bar
	private Drawable mIconOpenSearch; /**/
	private Drawable mIconCloseSearch; /**/
	private EditText mSearchEt; /* Edit Text view for searches*/
	private ImageView imageViewInsideTraslate;
	private MenuItem mSearchAction;/* menu item object */

	private String engStr ;
	private String rusStr ;
	private String tajStr ;
	public String tableName = "taj_rus";// table name for query execution
	public String quaryLanguage_1;
	public String quaryLanguage_2;	
	private List<String> spinnerLanguages_1 = new ArrayList<String>();
	private List<String> spinnerLanguages_2 = new ArrayList<String>();
	private String selectedLanguage_1 , selectedLanguage_2;
	private Spinner spinner1;
	private Spinner spinner2;
	private ArrayAdapter<String> adapterForSecondSpinner ;
	private ArrayAdapter<String> adapterForFirstSpinner ;



	//	private final String LIST_WORDS = "listWords";
	//	private final String MDB_HELPER = "mDbHelper";
	//	private final String IS_SEARCH_OPENED = "isSearchOpened";
	//	private final String WORD_PART = "wordPart";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDbHelper = new DictionaryDataSource(this);

		// Languages Spinner population
		engStr = getResources().getString(R.string.english_language);
		rusStr = getResources().getString(R.string.russian_language);
		tajStr = getResources().getString(R.string.tajik_language);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);

		spinnerLanguages_1.add(tajStr);
		spinnerLanguages_1.add(engStr);
		selectedLanguage_1 = spinnerLanguages_1.get(0);
		ArrayAdapter<String> adapterForFirstSpinner = new ArrayAdapter<String>
		(this,  R.layout.spinner_language_textview,
				spinnerLanguages_1);
		spinnerLanguages_2.add(rusStr);
		spinnerLanguages_2.add(engStr);
		selectedLanguage_2 = spinnerLanguages_2.get(0); 
		ArrayAdapter<String> adapterForSecondSpinner =new ArrayAdapter<String>
		(this, R.layout.spinner_language_textview,
				spinnerLanguages_2);

		adapterForFirstSpinner.setDropDownViewResource(R.layout.spinner_drop_down_textview);
		adapterForSecondSpinner.setDropDownViewResource(R.layout.spinner_drop_down_textview);
		spinner1.setAdapter(adapterForFirstSpinner);
		spinner2.setAdapter(adapterForSecondSpinner);
		MyOnItemSelectedListener moisl = new MyOnItemSelectedListener();
		spinner1.setOnItemSelectedListener(moisl);
		spinner2.setOnItemSelectedListener(moisl);


		//        if(savedInstanceState == null){
		/////**************///////
		mDbHelper.createDatabase();      
		refrashListVeiw(); // method which set the adapter and more ...
		//        }else {
		// Getting the icons. for menu bar
		mIconOpenSearch = getResources()
				.getDrawable(R.drawable.ic_action_action_search);
		mIconCloseSearch = getResources()
				.getDrawable(R.drawable.ic_action_action_close);
		mDbHelper.close();
	}


	//	@Override
	//	protected void onSaveInstanceState(Bundle outState) {
	//	    super.onSaveInstanceState(outState);
	//	    
	//	    outState.putParcelableArrayList(LIST_WORDS, (ArrayList<Word>) listWords);
	//	}

	@Override
	/*
	 * Inflate the menu; this adds items to the action bar if it is present.
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	/*
	 * Getting search action menu from . 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		mSearchAction = menu.findItem(R.id.action_search);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	/*
	 * Checking weather search bar is open or close and doing the opposite 
	 * @see also android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_search) {
			if (isSearchOpened) {
				closeSearchBar();
			} else {
				openSearchBar(mSearchQuery);
			}
			return true;
		}else if (id == R.id.menu_about){
			Intent intent = null;
			intent = new Intent(MainActivity.this, AboutActivity.class);
				startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	/*
	 * Setting the custom view on action bar, 
	 * setting up edit text view, 
	 * changing search icon. 
	 */
	private void openSearchBar(String queryText) {

		// Set custom view on action bar.
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.search_bar);

		// Set the icon click inside edit text to erase the text 
		imageViewInsideTraslate  = (ImageView) findViewById(R.id.imageViewInsideTraslate);
		imageViewInsideTraslate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSearchEt.setText("");
				refrashListVeiw();
				mSearchQuery = "";
				imageViewInsideTraslate.setVisibility(View.INVISIBLE);
			}
		});
		// Search edit text field setup.
		mSearchEt = (EditText) actionBar.getCustomView()
				.findViewById(R.id.editTextSearch);
		mSearchEt.addTextChangedListener((TextWatcher) new SearchWatcher());
		mSearchEt.setText(queryText);
		mSearchEt.requestFocus();
		
		// to show keypad on focused view 
		this.getApplicationContext();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);

		// Change search icon accordingly.
		mSearchAction.setIcon(mIconCloseSearch);
		isSearchOpened = true;
		
	}
	/*
	 * Closing the search display and changing the icon
	 */
	private void closeSearchBar() {

		// Remove custom view.
		getActionBar().setDisplayShowCustomEnabled(false);

		// Change search icon accordingly.
		mSearchAction.setIcon(mIconOpenSearch);
		isSearchOpened = false;
	}



	@Override
	/*
	 * TraslateActivity getting opened and data passed to it to show its meaning 
	 * @see also android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent intent = new Intent(this, TraslateActivity.class);
		intent.putExtra("wordForTraslate", listWords.get(position).toString());
		intent.putExtra("traslation", listWords.get(position).getDescription());
		startActivity(intent);

	}

	@Override
	/*
	 * @see android.app.Activity#onResume()
	 * Also opening the database
	 */
	protected void onResume() {
		super.onResume();
		mDbHelper.open();
	}

	@Override
	/* 
	 * @see android.app.Activity#onPause()
	 * Also closing the database
	 */
	protected void onPause() {
		super.onPause();
		mDbHelper.close();
	}
	/*
	 * swaps two spinner and updates the ListView
	 * 
	 */
	public void swapIconClick(View v){
		String swap1, swap2;
		swap1 = spinnerLanguages_1.get(0);
		swap2 = spinnerLanguages_1.get(1);
		spinnerLanguages_1.clear();
		spinnerLanguages_1.add(spinnerLanguages_2.get(0));
		spinnerLanguages_1.add(spinnerLanguages_2.get(1));
		spinnerLanguages_2.clear();
		spinnerLanguages_2.add(swap1);
		spinnerLanguages_2.add(swap2);		
		
		adapterForFirstSpinner = new ArrayAdapter<String>
		(MainActivity.this,  R.layout.spinner_language_textview,
				spinnerLanguages_1);
		adapterForFirstSpinner.setDropDownViewResource(R.layout.spinner_drop_down_textview);
		adapterForFirstSpinner.notifyDataSetChanged();
		spinner1.setAdapter(adapterForFirstSpinner);	
		
		adapterForSecondSpinner = new ArrayAdapter<String>
		(MainActivity.this,  R.layout.spinner_language_textview,
				spinnerLanguages_2);
		adapterForSecondSpinner.setDropDownViewResource(R.layout.spinner_drop_down_textview);
		adapterForSecondSpinner.notifyDataSetChanged();
		spinner2.setAdapter(adapterForSecondSpinner);	
		refrashListVeiw();
	}
	/*
	 * This method executes a query based on mSearchQuery and tableName 
	 * parses the cursor to list and sets the adapter list
	 * checks what languages are selected and determines what is tableName value
	 */
	public void refrashListVeiw(){
		mDbHelper.open();
		selectedLanguage_1 = spinnerLanguages_1.get(0);
		selectedLanguage_2 = spinnerLanguages_2.get(0);
		if(selectedLanguage_1.equals(engStr) && selectedLanguage_2.equals(rusStr)){
			tableName = DictionaryDBOpenHelper.TABLE_ENG_RUS;
		}
		else if(selectedLanguage_1.equals(engStr) && selectedLanguage_2.equals(tajStr)){
			tableName = DictionaryDBOpenHelper.TABLE_ENG_TAJ;
		}			
		else if(selectedLanguage_1.equals(rusStr) && selectedLanguage_2.equals(engStr)){
			tableName = DictionaryDBOpenHelper.TABLE_RUS_ENG;
		}			
		else if(selectedLanguage_1.equals(rusStr) && selectedLanguage_2.equals(tajStr)){
			tableName = DictionaryDBOpenHelper.TABLE_RUS_TAJ;
		}			
		else if(selectedLanguage_1.equals(tajStr) && selectedLanguage_2.equals(rusStr)){
			tableName = DictionaryDBOpenHelper.TABLE_TAJ_RUS;
		}			
		else if(selectedLanguage_1.equals(tajStr) && selectedLanguage_2.equals(engStr)){
			tableName = DictionaryDBOpenHelper.TABLE_TAJ_ENG;
		}			
		
		
		cursor = mDbHelper.getDataWideChar(mSearchQuery, tableName );	
		listWords = mDbHelper.parseCoursorToList(cursor);	
		
		adapter = new ArrayAdapter<Word>(
				MainActivity.this, android.R.layout.simple_list_item_1, listWords);
		setListAdapter(adapter);
	}

	/**
	 * This class handles element selection on spinners
	 * and switches elements so user can not pick same element on 
	 * both spinners. Assign language selected as well (more code than expected) 
	 */
	private class MyOnItemSelectedListener
			implements AdapterView.OnItemSelectedListener{

		String swap1, swap2;
		Spinner s;
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			s = (Spinner) parent;
			if (s.getId() == spinner1.getId()){
				if(spinner1.getSelectedItem().toString().equals(spinner2.getItemAtPosition(1).toString())){
					spinnerLanguages_2.remove(spinnerLanguages_2.indexOf(spinner1.getSelectedItem().toString()));
					spinnerLanguages_2.add(selectedLanguage_1);
					selectedLanguage_1 = spinner1.getSelectedItem().toString();
					adapterForSecondSpinner =new ArrayAdapter<String>(MainActivity.this, 
							R.layout.spinner_language_textview, spinnerLanguages_2);
					adapterForSecondSpinner.setDropDownViewResource(R.layout.spinner_drop_down_textview);
					adapterForSecondSpinner.notifyDataSetChanged();
					spinner2.setAdapter(adapterForSecondSpinner);

					swap1 = spinnerLanguages_1.get(0);
					swap2 = spinnerLanguages_1.get(1);
					spinnerLanguages_1.clear();
					spinnerLanguages_1.add(swap2);
					spinnerLanguages_1.add(swap1);
					adapterForFirstSpinner = new ArrayAdapter<String>
					(MainActivity.this,  R.layout.spinner_language_textview,
							spinnerLanguages_1);
					adapterForFirstSpinner.setDropDownViewResource(R.layout.spinner_drop_down_textview);
					adapterForFirstSpinner.notifyDataSetChanged();
					spinner1.setAdapter(adapterForFirstSpinner);	
					refrashListVeiw();
				}

			}
			if (s.getId() == spinner2.getId()){
				if(spinner2.getSelectedItem().toString().equals(spinner1.getItemAtPosition(1).toString())){
					spinnerLanguages_1.remove(spinnerLanguages_1.indexOf(spinner2.getSelectedItem().toString()));
					spinnerLanguages_1.add(selectedLanguage_2);
					selectedLanguage_2 = spinner2.getSelectedItem().toString();
					adapterForFirstSpinner =new ArrayAdapter<String>(MainActivity.this, 
							R.layout.spinner_language_textview, spinnerLanguages_1);
					adapterForFirstSpinner.setDropDownViewResource(R.layout.spinner_drop_down_textview);
					adapterForFirstSpinner.notifyDataSetChanged();
					spinner1.setAdapter(adapterForFirstSpinner);

					swap1 = spinnerLanguages_2.get(0);
					swap2 = spinnerLanguages_2.get(1);
					spinnerLanguages_2.clear();
					spinnerLanguages_2.add(swap2);
					spinnerLanguages_2.add(swap1);
					adapterForSecondSpinner = new ArrayAdapter<String>
					(MainActivity.this,  R.layout.spinner_language_textview,
							spinnerLanguages_2);
					adapterForSecondSpinner.setDropDownViewResource(R.layout.spinner_drop_down_textview);
					adapterForSecondSpinner.notifyDataSetChanged();
					spinner2.setAdapter(adapterForSecondSpinner);	
					refrashListVeiw();
				}
			}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * Responsible for handling changes in search edit text.
	 */
	private class SearchWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {
		}

		@Override
		public void onTextChanged(CharSequence c, int i, int i2, int i3) {
		}
		
		@Override
		/*
		 * If text changed in EditText view it calls refrashListView 
		 * make cancel imageView visible which erases editText views text
		 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
		 */
		public void afterTextChanged(Editable editable) {
			mSearchQuery = mSearchEt.getText().toString();
			if( mSearchQuery.length() > 0){
				imageViewInsideTraslate.setVisibility(View.VISIBLE);
			}
			refrashListVeiw();
		}

	}
}
