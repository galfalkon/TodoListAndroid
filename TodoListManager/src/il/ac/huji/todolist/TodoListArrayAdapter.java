package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TodoListArrayAdapter extends ArrayAdapter<Pair<String, Date>> {
	
	public TodoListArrayAdapter(Context context, int resource, List<Pair<String, Date>> objects) {
		super(context, resource, objects);
		this.m_context = context;
		this.m_objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflator = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView;
		if (convertView == null) {
			v = inflator.inflate(R.layout.todo_list_row, null);
		}
		final Date dueDate = m_objects.get(position).second;
		
		TextView titleText = (TextView)v.findViewById(R.id.txtTodoTitle);
		TextView dueDateText = (TextView)v.findViewById(R.id.txtTodoDueDate);
		titleText.setText(m_objects.get(position).first);
		final SimpleDateFormat simeplDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		dueDateText.setText(simeplDateFormat.format(dueDate));
		
		// Set the text views colors
		if (dueDate.before(new Date()) && !DateUtils.isToday(dueDate.getTime())) {
			dueDateText.setTextColor(OVERDUE_ITEM_COLOR);
			titleText.setTextColor(OVERDUE_ITEM_COLOR);
		} else {
			dueDateText.setTextColor(NOT_OVERDUE_ITEM_COLOR);
			titleText.setTextColor(NOT_OVERDUE_ITEM_COLOR);
		}
		
		return v;
	}
	
	private Context m_context;
	private List<Pair<String, Date>> m_objects;
	
	private static final int OVERDUE_ITEM_COLOR = Color.RED;
	private static final int NOT_OVERDUE_ITEM_COLOR = Color.BLACK;
}