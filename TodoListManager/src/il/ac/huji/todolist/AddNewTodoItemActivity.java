package il.ac.huji.todolist;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewTodoItemActivity extends Activity {
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_add_new_todo_item);
		
		Button cancelButton = (Button)findViewById(R.id.btnCancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent result = new Intent();
				setResult(RESULT_CANCELED, result);
				finish();
			}
		});
		
		findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String title = ((EditText)findViewById(R.id.edtNewItem)).getText().toString();
				if (title.isEmpty()) {
					Toast.makeText(AddNewTodoItemActivity.this, R.string.emptyInputToast, Toast.LENGTH_SHORT).show();
					return;
				}
				
				// Get the data
				final DatePicker datePicker = (DatePicker)findViewById(R.id.datePicker);

				Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
				Date dueDate = calendar.getTime();
				// Put the values in an Intent instance and send it back to the caller
				Intent result = new Intent();
				result.putExtra("title", title);
				result.putExtra("dueDate", dueDate);
				setResult(RESULT_OK, result);
				finish();
			}
		});
	}
}
