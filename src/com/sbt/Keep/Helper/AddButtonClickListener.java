package com.sbt.Keep.Helper;

import java.util.UUID;

import com.sbt.Keep.ExpenseAdapter;
import com.sbt.Keep.R;
import com.sbt.Keep.Data.Expense;

import android.app.Activity;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class AddButtonClickListener implements OnClickListener {	
	private ExpenseAdapter historyAdapter;
	private Activity activity;

	@Override
	public void onClick(View v) {
		// get text
		final EditText priceText = (EditText) getActivity().findViewById(R.id.priceText);

		final Editable text = priceText.getText();

		if (text.length() > 0) {
			// add to list
			Expense expense = new Expense();
			expense.setId(UUID.randomUUID().toString());
			expense.setAmount(Double.parseDouble(text.toString()));			
			expense.setItem(((android.widget.Button)v).getText().toString());
			
			this.historyAdapter.add(expense);
			this.historyAdapter.notifyDataSetChanged();

			priceText.setText("");

			final ListView historyView = (ListView) getActivity().findViewById(R.id.historyView);

			final int newPosition = getHistoryAdapter().getCount() - 1;

			historyView.setSelection(newPosition);

			SpreadsheetHelper helper = new SpreadsheetHelper(activity);
			helper.startSync(expense);
		}
	}

	public ExpenseAdapter getHistoryAdapter() {
		return historyAdapter;
	}

	public void setHistoryAdapter(ExpenseAdapter historyAdapter) {
		this.historyAdapter = historyAdapter;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

}
