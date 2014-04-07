package il.ac.huji.todolist;

import java.util.Date;

public class TodoItem {
	public TodoItem(String title, Date dueDate) {
		m_title = title;
		m_dueDate = dueDate;
	}
	
	public String getTitle() {
		return m_title;
	}
	
	public Date getDueDate() {
		return m_dueDate;
	}
	
	private String m_title;
	private Date m_dueDate;
}
