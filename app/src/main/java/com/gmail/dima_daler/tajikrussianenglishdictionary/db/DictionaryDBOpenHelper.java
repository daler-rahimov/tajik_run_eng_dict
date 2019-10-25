package com.gmail.dima_daler.tajikrussianenglishdictionary.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DictionaryDBOpenHelper extends SQLiteOpenHelper {
	
	private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
	//destination path (location) of our database on device
	private static String DATABASE_PATH = ""; 
	private static String DATABASE_NAME ="taj_rus";// Database name
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase db; 
	private final Context myContext;
	
	// table names
	public static final String TABLE_ENG_RUS = "eng_rus";
	public static final String TABLE_RUS_ENG = "rus_eng";
	public static final String TABLE_ENG_TAJ = "eng_taj";
	public static final String TABLE_TAJ_ENG = "taj_eng";
	public static final String TABLE_RUS_TAJ = "rus_taj";
	public static final String TABLE_TAJ_RUS = "taj_rus";	

	
	//column names
	public static final String COLUMN_ENG_RUS_ID = "eng_rus_id";
	public static final String COLUMN_RUS_ENG_ID = "rus_eng_id";
	public static final String COLUMN_ENG_TAJ_ID = "eng_taj_id";
	public static final String COLUMN_TAJ_ENG_ID = "taj_eng_id";
	public static final String COLUMN_RUS_TAJ_ID = "rus_taj_id";
	public static final String COLUMN_TAJ_RUS_ID = "taj_rus_id";
	
	public static final String COLUMN_WORD = "word";
	public static final String COLUMN_DESCRIPTION = "description";
	
	
	public DictionaryDBOpenHelper(Context context) 
	{
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);// 1? its Database Version
	    if(android.os.Build.VERSION.SDK_INT >= 17){
	       DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";         
	    }
	    else
	    {
	    	DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
	    }
	    this.myContext = context;
	}   

	public void createDataBase() throws IOException
	{
	    //If database not exists copy it from the assets

	    boolean mDataBaseExist = checkDataBase();
	    if(!mDataBaseExist)
	    {
	        this.getReadableDatabase();
	        this.close();
	        try 
	        {
	            //Copy the database from assests
	            copyDataBase();
	            Log.e(TAG, "createDatabase database created");
	        } 
	        catch (IOException mIOException) 
	        {
	            throw new Error("ErrorCopyingDataBase");
	        }
	    }
	}
	    //Check that the database exists here: /data/data/your package/databases/Da Name
	    private boolean checkDataBase()
	    {
	        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
	        Log.v("dbFile", dbFile + "   "+ dbFile.exists());
	        return dbFile.exists();
	    }

	    //Copy the database from assets
	    private void copyDataBase() throws IOException
	    {
	        InputStream mInput = myContext.getAssets().open(DATABASE_NAME);
	        String outFileName = DATABASE_PATH + DATABASE_NAME;
	        OutputStream mOutput = new FileOutputStream(outFileName);
	        byte[] mBuffer = new byte[1024];
	        int mLength;
	        while ((mLength = mInput.read(mBuffer))>0)
	        {
	            mOutput.write(mBuffer, 0, mLength);
	        }
	        mOutput.flush();
	        mOutput.close();
	        mInput.close();
	    }

	    //Open the database, so we can query it
	    public boolean openDataBase() 
	    {
	    	try{
	        String mPath = DATABASE_PATH + DATABASE_NAME;
	        Log.v("mPath", mPath);
	        db = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
	        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	        }catch(SQLException e){
	        	Log.v(TAG,"Ecciption: " + e.getMessage());
	        }
	    	return db != null;
	    }

	    @Override
	    public synchronized void close() 
	    {
	        if(db != null)
	        	db.close();
	        super.close();
	    }

		@Override
		public void onCreate(SQLiteDatabase db) {
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}

}
