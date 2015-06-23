package com.example.schreiber.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExternalDbOpenHelper extends SQLiteOpenHelper {

	public static String DB_PATH;
	public static String DB_NAME1;
	public SQLiteDatabase database;
	public final Context context;

	public static final String DB_NAME = "schreiber.sqllite3";
	public static final String TABLE_NAME = "table1";
	public static final String SLOVAK = "SLOVAK";
	public static final String GERMAN = "GERMAN";
	public static final String TOPIC = "TOPIC";
	public static final String KNOWN = "KNOWN";
	public static final String ID = "id";

	public SQLiteDatabase getDb() {
		return database;
	}

	public ExternalDbOpenHelper(Context context, String databaseName) {
		super(context, databaseName, null, 1);
		this.context = context;
		DB_PATH = context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator + DB_NAME1;
		DB_NAME1 = databaseName;
		openDataBase();
	}

	public void createDataBase() {
		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getWritableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				Log.e(this.getClass().toString(), "Copying error");
				throw new Error("Error copying database!");
			}
		} else {
			Log.i(this.getClass().toString(), "Database already exists");
		}
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {

//			String path = DB_PATH + DB_NAME1;
			String path = "/data/data/com.example.schreiber/databases/" + DB_NAME1;

			File file = new File(path);
//			if (file.exists()) {
//				Log.e("Deletujem ", file.getPath());
//				file.delete();
//			}

			checkDb = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
			Log.i("Path je", path);
		} catch (SQLException e) {
			Log.e(this.getClass().toString(), "Error while checking db");
		}
		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null;
	}

	private void copyDataBase() throws IOException {
		InputStream externalDbStream = context.getAssets().open(DB_NAME1);

//		String outFileName = DB_PATH + DB_NAME1;

		String outFileName =  "/data/data/com.example.schreiber/databases/" + DB_NAME1;
		
		
		OutputStream localDbStream = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = externalDbStream.read(buffer)) > 0) {
			localDbStream.write(buffer, 0, bytesRead);
		}
		localDbStream.close();
		externalDbStream.close();

	}

	public SQLiteDatabase openDataBase() throws SQLException {
//		String path = DB_PATH + DB_NAME1;
		String path =  "/data/data/com.example.schreiber/databases/" + DB_NAME1;
		if (database == null) {
			createDataBase();
			database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}
		return database;
	}

	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
	}

	public int updateKnown(int id, int newKnown) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KNOWN, newKnown);
		String[] whereArgs = { Integer.toString(id) };
		int count = database.update(TABLE_NAME, contentValues, ID + " =?", whereArgs);
		return count;
	}
	
	public int resetKnown(int value){
		ContentValues contentValues = new ContentValues();
		contentValues.put(KNOWN, value);
		int count = database.update(TABLE_NAME, contentValues, null, null);
		return count;
	}

	public String getAllString() {
		String[] columns = { ID, SLOVAK, GERMAN, TOPIC, KNOWN };
		Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
		StringBuffer bufffer = new StringBuffer();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String slovak = cursor.getString(1);
			String german = cursor.getString(2);
			String topic = cursor.getString(3);
			int known = cursor.getInt(4);
			bufffer.append(id + " " + slovak + " " + german + " " + topic + " " + known + "\n");
		}
		return bufffer.toString();
	}
	/**
	 * 
	 * @return list of all Slovak phrases
	 */
	public List<String> getAllSlovakList() {
		String[] columns = { ID, SLOVAK, GERMAN, TOPIC, KNOWN };
		Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
		List<String> bufffer = new ArrayList<String>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String slovak = cursor.getString(1);
			bufffer.add(slovak);
		}
		return bufffer;
	}
	
	public HashMap<Integer, String> getAllMap() {
		String[] columns = { ID, SLOVAK, GERMAN, TOPIC, KNOWN };
		Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
		HashMap<Integer, String> mapa = new HashMap<Integer, String>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String slovak = cursor.getString(1);
			mapa.put(id, slovak);
		}
		return mapa;
	}
	
	public List<Sentence> getAllSentence() {
		String[] columns = { ID, SLOVAK, GERMAN, TOPIC, KNOWN };
		Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
		List<Sentence> bufffer = new ArrayList<Sentence>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String slovak = cursor.getString(1);
			String german = cursor.getString(2);
			String topic = cursor.getString(3);
			int known = cursor.getInt(4);
			Sentence sentence = new Sentence(id, slovak, german, topic, known);
			bufffer.add(sentence);
		}
		return bufffer;
	}
	
	public List<Sentence> getAllSentence(String topicName) {
		String[] columns = { ID, SLOVAK, GERMAN, TOPIC, KNOWN };
		Cursor cursor = database.query(TABLE_NAME, columns, TOPIC + " =?", new String[] {topicName}, null, null, null);
		List<Sentence> bufffer = new ArrayList<Sentence>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String slovak = cursor.getString(1);
			String german = cursor.getString(2);
			String topic = cursor.getString(3);
			int known = cursor.getInt(4);
			Sentence sentence = new Sentence(id, slovak, german, topic, known);
			bufffer.add(sentence);
		}
		return bufffer;
	}
	
	
	
	
}
