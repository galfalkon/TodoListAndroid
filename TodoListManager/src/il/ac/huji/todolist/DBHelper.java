package il.ac.huji.todolist;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

public class DBHelper extends SQLiteOpenHelper {
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
		
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(String.format("create table %s (" +
				"%s integer primary key autoincrement, " +
				"%s text," +
				"%s long);", 
				TodoTable.TABLE_NAME, TodoTable.COL_ID, TodoTable.COL_TITLE, TodoTable.COL_DUE_DATE));
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	private static final String DB_NAME = "todo_db";
	private static final int DB_VERSION = 1;
	
	public static class TodoTable {
		public static final String TABLE_NAME = "todo_table";
		public static final String COL_ID = "_id";
		public static final String COL_TITLE = "title";
		public static final String COL_DUE_DATE = "due";
		
		public static void insertItem(Context context, Pair<String, Date> item) {
			DBHelper helper = new DBHelper(context);
			SQLiteDatabase db = helper.getWritableDatabase();
			ContentValues itemValues = new ContentValues();
			itemValues.put(COL_TITLE, item.first);
			// TODO Check if that  was he meant with long
			itemValues.put(COL_DUE_DATE, item.second.getTime());
			
			db.insert(TABLE_NAME, null, itemValues);
		}
		
		public static void deleteItem(Context context, long id) {
			DBHelper helper = new DBHelper(context);
			SQLiteDatabase db = helper.getWritableDatabase();
			db.delete(TABLE_NAME, COL_ID + " = ?", new String[] {String.valueOf(id)});
		}
	}
}
