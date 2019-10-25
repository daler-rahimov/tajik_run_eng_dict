package com.gmail.dima_daler.tajikrussianenglishdictionary.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gmail.dima_daler.tajikrussianenglishdictionary.words.Word;

public class DictionaryDataSource {

	protected static final String TAG = "DictionaryDataSource";

	private final Context mContext;
	private SQLiteDatabase mDb;
	private DictionaryDBOpenHelper mDbHelper;

	private int word_id;
	private String word;
	private String description;

	public DictionaryDataSource(Context context) {
		this.mContext = context;
		mDbHelper = new DictionaryDBOpenHelper(mContext);
	}

	public DictionaryDataSource createDatabase() {
		try {
			try {
				mDbHelper.createDataBase();
			} catch (IOException mIOException) {
				Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
				throw new Error("UnableToCreateDatabase");
			}
		} catch (SQLException e) {
			Log.v(TAG, e.getMessage());
		}
		return this;
	}

	public DictionaryDataSource open() {
		try {
			mDbHelper.openDataBase();
			mDbHelper.close();
			mDb = mDbHelper.getReadableDatabase();
		} catch (SQLException mSQLException) {
			Log.e(TAG, "open >>" + mSQLException.toString());
		}
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public Cursor getTestData() {
		try {
			String sql = "SELECT * FROM taj_rus;";

			Cursor mCur = mDb.rawQuery(sql, null);
			return mCur;
		} catch (SQLException mSQLException) {
			Log.e(TAG, "getTestData >>" + mSQLException.toString());
			throw mSQLException;
		}
	}

	public List<Word> parseCoursorToList(Cursor cursor) {
		List<Word> listWords = new ArrayList<Word>();
		Word wordObj;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				word_id = cursor.getInt(0);
				word = cursor.getString(1);
				description = cursor.getString(2);
				wordObj = new Word(word_id, word, description);
				listWords.add(wordObj);
			}
		} else {
			listWords.add(new Word(0, " ", " "));
		}
		return listWords;
	}

	public String parseCursorToWord(Cursor cursor, int position) {
		// String word;
		if (cursor.getCount() > 0) {

		}
		return null;
	}

	/*
	 * Search for wideChar passed and return cursor
	 */
	public Cursor getDataWideChar(String wordPart, String tableName) {
		Cursor mCur;
		// if its less than one return empty cursor
		if (wordPart.length() < 1) {
			mCur = null;
			return mCur;
		}
		wordPart.toLowerCase(Locale.US);
		wordPart = checkForSpecialChars(wordPart);
		try {
			String sql;
			sql = " SELECT * ";
			sql += " FROM " + tableName;
			sql += " WHERE word GLOB '" + wordPart + "*'";
			Log.v("Sql", sql);

			mCur = mDb.rawQuery(sql, null);
			return mCur;
		} catch (SQLException mSQLException) {
			Log.e(TAG, "getTestData >>" + mSQLException.toString());
			throw mSQLException;
		}
	}

	/*
	 * Checks for special characters in the string and returns representing wide
	 * char added instead of that char: "[Ӣ,Й,И]" , "[ӣ,и]" , "[Е,Ё]" , "[ё,е]"
	 * , "[Ғ,Г]" , "[ғ,г]" , "[Қ,К]" , "[қ,к]" , "[Ӯ,У]" , "[ӯ,у]" , "[Ҳ,Х]" ,
	 * "[ҳ,х]" , "[Ҷ,Ч]" , "[ҷ,ч]"
	 */
	public String checkForSpecialChars(String word) {
		String withSpecialCharWord = "";
		for (int i = 0; i < word.length(); i++) {
            switch (word.charAt(i)) {
            case 'И':
                withSpecialCharWord += "[ӢЙИ]";
                break;
            case 'и':
                withSpecialCharWord += "[ӣйи]";
                break;
            case 'Е':
                withSpecialCharWord += "[ЕЁ]";
                break;
            case 'е':
                withSpecialCharWord += "[ёе]";
                break;
            case 'Г':
                withSpecialCharWord += "[ҒГ]";
                break;
            case 'г':
                withSpecialCharWord += "[ғг]";
                break;
            case 'К':
                withSpecialCharWord += "[ҚК]";
                break;
            case 'к':
                withSpecialCharWord += "[қк]";
                break;
            case 'У':
                withSpecialCharWord += "[ӯу]";
                break;
            case 'у':
                withSpecialCharWord += "[ӯу]";
                break;
            case 'Х':
                withSpecialCharWord += "[ҲХ]";
                break;
            case 'х':
                withSpecialCharWord += "[ҳх]";
                break;
            case 'Ч':
                withSpecialCharWord += "[ҶЧ]";
                break;
            case 'ч':
                withSpecialCharWord += "[ҷч]";
                break;
            default:
                withSpecialCharWord += word.charAt(i);
            }
		}
		return withSpecialCharWord;
	}
}
