package il.ac.huji.todolist;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String> {
	
	public CustomArrayAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView)super.getView(position, convertView, parent);
		view.setTextColor(ITEMS_COLORS[position % ITEMS_COLORS.length]);
		view.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
		return view;
	}
	
	private static final int[] ITEMS_COLORS = {Color.RED, Color.BLUE};
	private static final int PADDING_LEFT = 0;
	private static final int PADDING_TOP = 10;
	private static final int PADDING_RIGHT = 0;
	private static final int PADDING_BOTTOM = 10;
}