package com.drayagerecorder;

import android.content.ContentUris;
import android.content.Context;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DataProvider extends ContentProvider{

//%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&
//Static Data
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	 
	public static final String PROVIDER_NAME = 
     "com.drayagerecorder.DataProvider.kml";
	 
	 public static final Uri CONTENT_URI = 
     Uri.parse("content://"+ PROVIDER_NAME + "/KML");
	 
   public static final String _ID = "_id";
   public static final String TITLE = "title";
   
   private static final int KML = 1;
   private static final int KML_ID = 2;
   
   private static final UriMatcher uriMatcher;
   static{
      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
      uriMatcher.addURI(PROVIDER_NAME, "kml", KML);
      uriMatcher.addURI(PROVIDER_NAME, "kml/#", KML_ID);      
   }
   
//%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&
//For Database Use
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
   
   private SQLiteDatabase db;
   private static final String DATABASE_NAME = "KML";
   private static final String DATABASE_TABLE = "titles";
   private static final int DATABASE_VERSION = 1;
   private static final String DATABASE_CREATE =
         "create table " + DATABASE_TABLE + 
         " (_id integer primary key autoincrement, "
         + "title integer not null);";

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&
//Database Management Helper Method
//%&%&%&%&%&%&%%%%&%&%&%&%&%&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&   
   
   private static class DatabaseHelper extends SQLiteOpenHelper 
   {
      DatabaseHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&
//Create The Database
//%&%&%&%&%&%&%%%%&%&%&%&%&%&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	
  @Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);		
	}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&
//Upgrading The Database
//%&%&%&%&%&%&%%%%&%&%&%&%&%&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	
  @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("Content provider database", 
        "Upgrading database from version " + 
        oldVersion + " to " + newVersion + 
        ", which will destroy all old data");
   db.execSQL("DROP TABLE IF EXISTS titles");
   onCreate(db);		
	}
}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&
//Deleting The Database
//%&%&%&%&%&%&%%%%&%&%&%&%&%&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	
   @Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
  	 // arg0 = uri 
     // arg1 = selection
     // arg2 = selectionArgs
  	 int count=0;
     switch (uriMatcher.match(uri)){
        case KML:
           count = db.delete(
          		 DATABASE_TABLE,
               selection, 
               selectionArgs);
       case KML_ID:
           String id = uri.getPathSegments().get(1);
            count = db.delete(
                DATABASE_TABLE,                        
                _ID + " = " + id + 
                (!TextUtils.isEmpty(selection) ? " AND (" + 
                uri + ')' : ""), 
                selectionArgs);
             break;
       default: throw new IllegalArgumentException(
           "Unknown URI " + uri);    
     }
     getContext().getContentResolver().notifyChange(uri, null);
     return count;    
	}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&
//Allow Records To be Inserted Into The Provider
//%&%&%&%&%&%&%%%%&%&%&%&%&%&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	
   @Override
	public Uri insert(Uri uri, ContentValues values) {
  	//---add a new KML---
     long rowID = db.insert(
        DATABASE_TABLE, "", values);
   //---if added successfully, notify change to registered observers--- 
     if (rowID>0)
     {
        Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(_uri, null);    
        return _uri;                
     }        
     throw new SQLException("Failed to insert row into " + uri);
  }   

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&
//Open A Connection To The Database
//%&%&%&%&%&%&%%%%&%&%&%&%&%&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&		
	
   @Override
  public boolean onCreate() {
     Context context = getContext();
     DatabaseHelper dbHelper = new DatabaseHelper(context);
     db = dbHelper.getWritableDatabase();
     return (db == null)? false:true;
  }

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&
//Allow Clients To Query The Database
//%&%&%&%&%&%&%%%%&%&%&%&%&%&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	   
	
   @Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
  	  
  	 SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
     sqlBuilder.setTables(DATABASE_TABLE);
     
     if (uriMatcher.match(uri) == KML_ID)
       //---if getting a particular book---
       sqlBuilder.appendWhere(
          _ID + " = " + uri.getPathSegments().get(1)); 
     
     if (sortOrder==null || sortOrder=="")
       sortOrder = TITLE;
     
     Cursor c = sqlBuilder.query(
         db, 
         projection, 
         selection, 
         selectionArgs, 
         null, 
         null, 
         sortOrder);
     
   //---register to watch a content URI for changes---
     c.setNotificationUri(getContext().getContentResolver(), uri);
     return c;
	}
 
 //%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&
 //Update a record
 //%&%&%&%&%&%&%%%%&%&%&%&%&%&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)){
		case KML:
			return "vnd.android.cursor.dir/vnd.drayagerecorder.DataProvider.kml";
		case KML_ID:                
      return "vnd.android.cursor.item/vnd.drayagerecorder.DataProvider.kml";
   default:
  	 throw new IllegalArgumentException("Unsupported URI: " + uri);        
		}
	}
}	
	
