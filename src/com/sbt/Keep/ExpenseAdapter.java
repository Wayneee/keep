package com.sbt.Keep;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sbt.Keep.Data.Expense;

public class ExpenseAdapter extends ArrayAdapter<String> {

	private DecimalFormat decimalFormat = new DecimalFormat("0.##");
	private ArrayList<Expense> expenses = new ArrayList<Expense>();

	public ArrayList<Expense> getExpenses() {
		return expenses;
	}

	public ExpenseAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public void add(Expense exp) {
		expenses.add(exp);

		super.add(decimalFormat.format(exp.getAmount()));
	}

	@Override
	public void add(String amount) {
		Expense exp = new Expense(Double.parseDouble(amount));
		add(exp);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getView(position, convertView, parent);

//		if (position > 0 && expenses.size() < position - 1) {
//			switch (expenses.get(position-1).getStatus()) {
//			case Completed:
//				view.setTextColor(0xFF14700F);
//				break;
//			case Pending:
//			case Queue:
//			default:
//				view.setTextColor(Color.BLACK);
//				break;
//			}
//		}

		return view;
	}

}
