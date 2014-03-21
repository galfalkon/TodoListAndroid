package il.ac.huji.todolist;

import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class TodoListManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		m_adapter = getAdapter();
		
		// Set the list view for the tasks
		ListView lstTodoItems = (ListView)findViewById(R.id.lstTodoItems);
		lstTodoItems.setAdapter(m_adapter);
		registerForContextMenu(lstTodoItems);
		
		// Initialize parse
		Resources resources = getResources();
		Parse.initialize(getApplicationContext(), resources.getString(R.string.parse_application_id), resources.getString(R.string.parse_client_key));
		ParseUser.enableAutomaticUser();
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
			DBHelper.TodoTable.deleteItem(getApplicationContext(), info.id);
			m_adapter.changeCursor(getCursorToList());
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
				DBHelper.TodoTable.insertItem(getApplicationContext(), new Pair<String, Date>(title, dueDate));
				
				// Save object to backend
				ParseObject todoObject = new ParseObject(ParseConstants.CLASS_NAME);
				todoObject.put(ParseConstants.KEY_TITLE, title);
				todoObject.put(ParseConstants.KEY_DUE_DATE, dueDate);
				todoObject.saveInBackground();
				
				// Update cursor
				m_adapter.changeCursor(getCursorToList());
				break;
			case RESULT_CANCELED:
				break;
			}
		}
	}
	
	private TodoListCursorAdapter getAdapter() {
		Cursor c = getCursorToList();
		String[] from = new String[] {DBHelper.TodoTable.COL_TITLE, DBHelper.TodoTable.COL_DUE_DATE};
		int[] to = new int[] {R.id.txtTodoTitle, R.id.txtTodoDueDate};
		return new TodoListCursorAdapter(getApplicationContext(), R.layout.todo_list_row, c, from, to, 0);
	}
	
	private Cursor getCursorToList() {
		DBHelper helper = new DBHelper(getApplicationContext());
		SQLiteDatabase db = helper.getReadableDatabase();
		// TODO: This call demand at least API 16. Can we assume it?
		return db.query(false, DBHelper.TodoTable.TABLE_NAME, null, null, null, null, null, null, null, null);
	}
	
	private TodoListCursorAdapter m_adapter;
	
	private final static int REQ_CODE_ADD_ITEM = 0;
	private static class ParseConstants {
		public final static String CLASS_NAME = "todo"; 
		public final static String KEY_TITLE = "title";
		public final static String KEY_DUE_DATE = "due";
	}
}