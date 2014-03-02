package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TodoListManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		m_items = new ArrayList<String>();
		m_adapter = new CustomArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, m_items);
		
		// Set the list view for the tasks
		ListView lstTodoItems = (ListView)findViewById(R.id.lstTodoItems);
		lstTodoItems.setAdapter(m_adapter);
		registerForContextMenu(lstTodoItems);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
	    	handleAddItem();
	    	return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info)  {
		super.onCreateContextMenu(menu, v, info);
		menu.setHeaderTitle(m_items.get(((AdapterContextMenuInfo)info).position));
		getMenuInflater().inflate(R.menu.todo_list_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menuItemDelete:
			m_items.remove(info.position);
			m_adapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void handleAddItem() {
		EditText edtNewItem = (EditText)findViewById(R.id.edtNewItem);
		
		// Verify input
		if (edtNewItem.getText().toString().isEmpty()) {
			Toast.makeText(getApplicationContext(), R.string.emptyInputToast, Toast.LENGTH_SHORT).show();
			return;
		} else {
			m_items.add(edtNewItem.getText().toString());
		}
		
		m_adapter.notifyDataSetChanged();
		edtNewItem.setText("");
	}
	
	private List<String> m_items;
	private CustomArrayAdapter m_adapter;
}