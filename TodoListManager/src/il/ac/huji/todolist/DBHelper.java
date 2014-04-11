package il.ac.huji.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
		db.execSQL("drop table if exists " + TodoTable.TABLE_NAME);
		onCreate(db);
	}
	
	public static void initialize(Context context) {
		helperInstance = new DBHelper(context);
	}
	
	public static DBHelper helperInstance;
	private static final String DB_NAME = "todo_db";
	private static final int DB_VERSION = 1;
	
	public static class TodoTable {
		public static final String TABLE_NAME = "todo";
		public static final String COL_ID = "_id";
		public static final String COL_TITLE = "title";
		public static final String COL_DUE_DATE = "due";
		
		public static void insertItem(TodoItem item) {
			ContentValues itemValues = new ContentValues();
			itemValues.put(COL_TITLE, item.getTitle());
			itemValues.put(COL_DUE_DATE, item.getDueDate().getTime());
			
			helperInstance.getWritableDatabase().insert(TABLE_NAME, null, itemValues);
		}
		
		public static void deleteItem(Context context, long id) {
			helperInstance.getWritableDatabase().delete(TABLE_NAME, COL_ID + " = ?", new String[] {String.valueOf(id)});
		}
		
		public static Cursor getCursorToAllRecords() {
			return helperInstance.getReadableDatabase().query(DBHelper.TodoTable.TABLE_NAME, null, null, null, null, null, null);
		}
		
		public static Cursor getLimitedCursorToRecordes(int limit) {
			return helperInstance.getReadableDatabase().query(DBHelper.TodoTable.TABLE_NAME, null, null, null, null, null, null, String.valueOf(limit));
		}
		
		public static long getNumOfRecords() {
			return DatabaseUtils.queryNumEntries(helperInstance.getReadableDatabase(), DBHelper.TodoTable.TABLE_NAME);
		}
	}
}
