package il.ac.huji.todolist;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AlternatingColorsArrayAdapter<T> extends ArrayAdapter<T> {
	
	public AlternatingColorsArrayAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView)super.getView(position, convertView, parent);
		view.setTextColor(ITEMS_COLORS[position % ITEMS_COLORS.length]);
		return view;
	}
	
	private static final int[] ITEMS_COLORS = {0xFFFF0000, 0xFF0000FF};
}