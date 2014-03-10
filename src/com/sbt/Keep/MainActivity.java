package com.sbt.Keep;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sbt.Keep.Helper.AddButtonClickListener;

public class MainActivity extends Activity {
	protected static final int REQUEST_CREATE_SPREADSHEET = 1;
	protected static final int REQUEST_LOAD_SPREADSHEET = 0;
	protected static final String SPREADSHEET_AUTHTYPE = "oauth2:https://spreadsheets.google.com/feeds/";
	private ExpenseAdapter historyAdapter;

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
		clearHistoryMenu
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						return false;
					}

				});

		return true;
	}

	private void setupUI() {
		// init history list
		final ListView historyView = (ListView) findViewById(R.id.historyView);

		historyAdapter = new ExpenseAdapter(this,
				android.R.layout.simple_list_item_1);

		historyView.setAdapter(historyAdapter);

		// setup button
		final Button addButton1 = (Button) findViewById(R.id.addButton1);
		final Button addButton2 = (Button) findViewById(R.id.addButton2);
		final Button addButton3 = (Button) findViewById(R.id.addButton3);
		final Button addButton4 = (Button) findViewById(R.id.addButton4);
		final Button addButton5 = (Button) findViewById(R.id.addButton5);
		final Button addButton6 = (Button) findViewById(R.id.addButton6);
		final Button addButton7 = (Button) findViewById(R.id.addButton7);
		final Button addButton8 = (Button) findViewById(R.id.addButton8);
		final Button addButton9 = (Button) findViewById(R.id.addButton9);
		final Button addButton10 = (Button) findViewById(R.id.addButton10);
		final Button addButton11 = (Button) findViewById(R.id.addButton11);
		final Button addButton12 = (Button) findViewById(R.id.addButton12);

		AddButtonClickListener addButtonClickListener = new AddButtonClickListener();
		addButtonClickListener.setHistoryAdapter(historyAdapter);
		addButtonClickListener.setActivity(this);

		addButton1.setOnClickListener(addButtonClickListener);
		addButton2.setOnClickListener(addButtonClickListener);
		addButton3.setOnClickListener(addButtonClickListener);
		addButton4.setOnClickListener(addButtonClickListener);
		addButton5.setOnClickListener(addButtonClickListener);
		addButton6.setOnClickListener(addButtonClickListener);
		addButton7.setOnClickListener(addButtonClickListener);
		addButton8.setOnClickListener(addButtonClickListener);
		addButton9.setOnClickListener(addButtonClickListener);
		addButton10.setOnClickListener(addButtonClickListener);
		addButton11.setOnClickListener(addButtonClickListener);
		addButton12.setOnClickListener(addButtonClickListener);

		final EditText priceText = (EditText) findViewById(R.id.priceText);

		priceText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		priceText.setImeActionLabel(addButton4.getText(),
				EditorInfo.IME_ACTION_DONE);
		priceText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					final Button addButton = (Button) ((Activity) v
							.getContext()).findViewById(R.id.addButton4);
					addButton.performClick();

					return true;
				}

				return false;
			}

		});
		priceText.requestFocus();

		this.setTitle("");
	}
}
