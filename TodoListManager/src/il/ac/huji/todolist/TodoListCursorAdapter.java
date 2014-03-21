package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TodoListCursorAdapter extends SimpleCursorAdapter {
	public TodoListCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		TextView titleView = (TextView)v.findViewById(R.id.txtTodoTitle);
		TextView dueDateView = (TextView)v.findViewById(R.id.txtTodoDueDate);
		
		Date dueDate = new Date(Long.valueOf(dueDateView.getText().toString()));
		final SimpleDateFormat simeplDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		dueDateView.setText(simeplDateFormat.format(dueDate));
		
		// Set the text views colors
		if (dueDate.before(new Date()) && !DateUtils.isToday(dueDate.getTime())) {
			dueDateView.setTextColor(OVERDUE_ITEM_COLOR);
			titleView.setTextColor(OVERDUE_ITEM_COLOR);
		} else {
			dueDateView.setTextColor(NOT_OVERDUE_ITEM_COLOR);
			titleView.setTextColor(NOT_OVERDUE_ITEM_COLOR);
		}
				
		return v;
	}
	
	private static final int OVERDUE_ITEM_COLOR = Color.RED;
	private static final int NOT_OVERDUE_ITEM_COLOR = Color.BLACK;
}
