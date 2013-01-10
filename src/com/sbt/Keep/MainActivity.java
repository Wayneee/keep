package com.sbt.Keep;

import java.util.ArrayList;
import java.util.List;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.sbt.Keep.Data.Expense;
import com.sbt.Keep.Helper.DbHelper;
import com.sbt.Keep.Helper.SpreadsheetHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	protected static final int REQUEST_CREATE_SPREADSHEET = 1;
	protected static final int REQUEST_LOAD_SPREADSHEET = 0;
	protected static final String SPREADSHEET_AUTHTYPE = "oauth2:https://spreadsheets.google.com/feeds/";
	private ExpenseAdapter historyAdapter;
	private ArrayList<Integer> syncedId = new ArrayList<Integer>();

	protected boolean isSynced(final int position) {
		// return syncedId.contains(position);
		// get by db.

		return false;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupUI();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		MenuItem clearHistoryMenu = menu.findItem(R.id.clear_history);
		clearHistoryMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
//				DbHelper dbHelper = new DbHelper(item.getActionView().getContext());
//				dbHelper.db().query(arg0).store(expenses);
//				dbHelper.db().commit();
//				dbHelper.db().close();
				
				
				return false;
			}
			
		});
		
		return true;
	}

	private void setupUI() {
		// init history list
		final ListView historyView = (ListView) findViewById(R.id.historyView);

		historyAdapter = new ExpenseAdapter(this, android.R.layout.simple_list_item_1);

		// load db
//		DbHelper dbHelper = new DbHelper(this);
//		List<Expense> query = dbHelper.db().queryByExample(Expense.class);
//		for (Expense exp : query) {
//			historyAdapter.add(exp);
//		}
//
//		dbHelper.db().close();

		historyAdapter.registerDataSetObserver(new DataSetObserver() {

			@Override
			public void onChanged() {
				// TODO Auto-generated method stub
				super.onChanged();

				syncData();
			}

		});

		historyView.setAdapter(historyAdapter);

		// setup button
		final Button addButton = (Button) findViewById(R.id.addButton);

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// get text
				final EditText priceText = (EditText) findViewById(R.id.priceText);

				final Editable text = priceText.getText();

				if (text.length() > 0) {
					// add to list
					historyAdapter.add(text.toString());
					historyAdapter.notifyDataSetChanged();

					priceText.setText("");

					final ListView historyView = (ListView) findViewById(R.id.historyView);

					final int newPosition = historyAdapter.getCount() - 1;

					historyView.setSelection(newPosition);
				}
			}
		});

		final EditText priceText = (EditText) findViewById(R.id.priceText);

		priceText.setImeOptions(EditorInfo.IME_ACTION_DONE);

		priceText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(final View v, final int keyCode, final KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					final Button addButton = (Button) findViewById(R.id.addButton);
					addButton.performClick();

					return true;
				}

				return false;
			}
		});

		priceText.requestFocus();

		this.setTitle("");
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected void syncData() {
		final int count = historyAdapter.getCount();

		final ArrayList<String> amounts = new ArrayList<String>(count);

		ArrayList<Expense> expenses = new ArrayList<Expense>();

		int start = 0;
		if (syncedId.size() > 0) {
			start = syncedId.get(syncedId.size() - 1) + 1;
		}

		for (int i = start; i < count; i++) {
			amounts.add(historyAdapter.getItem(i));

			syncedId.add(i);

			expenses.add(new Expense(Double.parseDouble(historyAdapter.getItem(i))));
		}

		// save to database
//		DbHelper dbHelper = new DbHelper(this);
//		dbHelper.db().store(expenses);
//		dbHelper.db().commit();
//		dbHelper.db().close();

		SpreadsheetHelper helper = new SpreadsheetHelper(this);
		helper.startSync(amounts);

		// get upload status
	}

}
