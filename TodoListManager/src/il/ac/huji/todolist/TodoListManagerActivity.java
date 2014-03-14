package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TodoListManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		m_items = new ArrayList<Pair<String,Date>>();
		m_adapter = new TodoListArrayAdapter(getApplicationContext(), R.layout.todo_list_row, m_items);
		
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
			startActivityForResult(intent, ADD_ITEM_REQ_CODE);
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
			m_items.remove(info.position);
			m_adapter.notifyDataSetChanged();
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
		// Sanity check - the request code should be 0
		if (reqCode != ADD_ITEM_REQ_CODE) {
			return;
		}
		
		switch (resCode) {
		case RESULT_OK:
			final String title = data.getStringExtra("title");
			final Date dueDate = (Date)data.getSerializableExtra("dueDate");
			m_items.add(new Pair<String, Date>(title, dueDate));
			m_adapter.notifyDataSetChanged();
			break;
		case RESULT_CANCELED:
			break;
		}
	}
	
	private TodoListArrayAdapter m_adapter;
	private List<Pair<String, Date>> m_items;
	
	private final static int ADD_ITEM_REQ_CODE = 0;
}