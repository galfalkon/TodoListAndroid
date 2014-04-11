package il.ac.huji.todolist;

import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

public class TodoListManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Initialize DB
		DBHelper.initialize(getApplicationContext());
		
		setContentView(R.layout.activity_todo_list_manager);

		// Set adapter
		String[] from = new String[] {DBHelper.TodoTable.COL_TITLE, DBHelper.TodoTable.COL_DUE_DATE};
		int[] to = new int[] {R.id.txtTodoTitle, R.id.txtTodoDueDate};
		m_adapter = new TodoListCursorAdapter(getApplicationContext(), R.layout.todo_list_row, null, from, to, 0);
		new LoadTodoItemsFromDBAsyncTask().execute();
				
		// Set the list view for the tasks
		ListView lstTodoItems = (ListView)findViewById(R.id.lstTodoItems);
		lstTodoItems.setAdapter(m_adapter);
		registerForContextMenu(lstTodoItems);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list_manager, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menuItemAdd:
	    	// Start a dialog for prompting the user for the new item information
	    	Intent intent = new Intent(getApplicationContext(), AddNewTodoItemActivity.class);
			startActivityForResult(intent, REQ_CODE_ADD_ITEM);
	    	return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info)  {
		super.onCreateContextMenu(menu, v, info);
		getMenuInflater().inflate(R.menu.todo_list_context_menu, menu);
		
		AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo)info;
		final String title = ((TextView)adapterInfo.targetView.findViewById(R.id.txtTodoTitle)).getText().toString();
		menu.setHeaderTitle(title);
		if (title.toLowerCase(Locale.US).startsWith(getString(R.string.call_item_lowercase_prefix))) {
			menu.findItem(R.id.menuItemCall).setVisible(true);
		} else {
			menu.findItem(R.id.menuItemCall).setVisible(false);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menuItemDelete:
			// Delete the item from DB
			new DeleteTodoItemsAsyncTask().execute(info.id);
			
			return true;
		case R.id.menuItemCall:
			final String title = ((TextView)info.targetView.findViewById(R.id.txtTodoTitle)).getText().toString();
			Pattern pattern = Pattern.compile(getString(R.string.call_item_lowercase_prefix) + "(.*)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(title);
			if (matcher.matches()) {
		    	Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + matcher.group(1)));
				startActivity(dial);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data) {
		switch (reqCode) {
		case REQ_CODE_ADD_ITEM:
			switch (resCode) {
			case RESULT_OK:
				final String title = data.getStringExtra(AddNewTodoItemActivity.RESULT_KEY_TITLE);
				final Date dueDate = (Date)data.getSerializableExtra(AddNewTodoItemActivity.RESULT_KEY_DUE_DATE);
				TodoItem item = new TodoItem(title, dueDate);
				new AddTodoItemsAsyncTask().execute(item);
				
				break;
			case RESULT_CANCELED:
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DBHelper.helperInstance.close();
	}
	
	private class AddTodoItemsAsyncTask extends AsyncTask<TodoItem, Cursor, Void> {

		@Override
		protected Void doInBackground(TodoItem... params) {
			int chunkCounter = 0;
			for (TodoItem item : params) {
				chunkCounter++;
				DBHelper.TodoTable.insertItem(item);
				if (chunkCounter == ITEMS_CHUNKES_SIZE) {
					publishProgress(DBHelper.TodoTable.getCursorToAllRecords());
					chunkCounter = 0;
				}
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Cursor... values) {
			super.onProgressUpdate(values);
			m_adapter.changeCursor(values[0]);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			m_adapter.changeCursor(DBHelper.TodoTable.getCursorToAllRecords());
		}
		
		private static final int ITEMS_CHUNKES_SIZE = 5;
	}
	
	private class DeleteTodoItemsAsyncTask extends AsyncTask<Long, Cursor, Void> {

		@Override
		protected Void doInBackground(Long... params) {
			int chunkCounter = 0;
			for (Long itemId : params) {
				chunkCounter++;
				DBHelper.TodoTable.deleteItem(getApplicationContext(), itemId);
				if (chunkCounter == ITEMS_CHUNKES_SIZE) {
					publishProgress(DBHelper.TodoTable.getCursorToAllRecords());
					chunkCounter = 0;
				}
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Cursor... values) {
			super.onProgressUpdate(values);
			m_adapter.changeCursor(values[0]);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			m_adapter.changeCursor(DBHelper.TodoTable.getCursorToAllRecords());
		}
		
		private static final int ITEMS_CHUNKES_SIZE = 5;
	}
	
	private class LoadTodoItemsFromDBAsyncTask extends AsyncTask<Void, Cursor, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			long numOfRecords = DBHelper.TodoTable.getNumOfRecords();
			int limit = ITEMS_CHUNKES_SIZE;
			while (limit < numOfRecords) {
				publishProgress(DBHelper.TodoTable.getLimitedCursorToRecordes(limit));
				limit += ITEMS_CHUNKES_SIZE;
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Cursor... values) {
			super.onProgressUpdate(values);
			m_adapter.changeCursor(values[0]);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			m_adapter.changeCursor(DBHelper.TodoTable.getCursorToAllRecords());
		}
		
		private static final int ITEMS_CHUNKES_SIZE = 1; 
	}
	
	private TodoListCursorAdapter m_adapter;
	
	private final static int REQ_CODE_ADD_ITEM = 0;
}