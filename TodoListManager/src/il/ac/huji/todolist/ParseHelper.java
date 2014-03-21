package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.util.Pair;

public class ParseHelper {
	
	// Save the given object to back end
	public static void addItem(Pair<String, Date> itemPair) {
		final SimpleDateFormat simeplDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		ParseObject todoObject = new ParseObject(Constants.CLASS_NAME);
		todoObject.put(Constants.KEY_TITLE, itemPair.first);
		todoObject.put(Constants.KEY_DUE_DATE, simeplDateFormat.format(itemPair.second));
		todoObject.saveInBackground();
	}
	
	// Deletes the given item from the back end
	public static void deleteItem(Pair<String, String> deletedItemInfo) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Constants.CLASS_NAME).
				whereEqualTo(Constants.KEY_TITLE, deletedItemInfo.first).
				whereEqualTo(Constants.KEY_DUE_DATE, deletedItemInfo.second);
		
		query.getFirstInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e != null) {
					return;
				}
				object.deleteInBackground();
			}
		});
	}
	
	private static class Constants {
		public final static String CLASS_NAME = "todo"; 
		public final static String KEY_TITLE = "title";
		public final static String KEY_DUE_DATE = "due";
	}
	
}
