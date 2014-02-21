package com.sbt.Keep;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sbt.Keep.Data.Expense;

public class ExpenseAdapter extends ArrayAdapter<Expense> {

	private ArrayList<Expense> expenses = new ArrayList<Expense>();

	public ArrayList<Expense> getExpenses() {
		return expenses;
	}

	public ExpenseAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}	
	
	@Override
	public void add(Expense object) {
		super.add(object);
		
		this.expenses.add(object);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getView(position, convertView, parent);

		return view;
	}

	public void setExpenses(ArrayList<Expense> expenses) {
		this.expenses = expenses;
	}

}
