package com.sbt.Keep.Helper;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.R;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.StringUtil;
import com.sbt.Keep.MainActivity;
import com.sbt.Keep.Data.Expense;

public class SpreadsheetHelper {
	private static final int REQUEST_LOAD_SPREADSHEET = 0;
	private static final String SPREADSHEET_AUTHTYPE = "oauth2:https://spreadsheets.google.com/feeds/";
	private static final String SPREADSHEET_FEED_URL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";
	private static final String SPREADSHEET_NAME = "日常開資";
	private static String ITEM_PAID_BY = "Android";
	private String authToken;
	private HashMap<String, String> ITEM_PAID_BY_MAP;

	private Activity context;

	public SpreadsheetHelper(Activity context) {
		this.context = context;

		ITEM_PAID_BY_MAP = new HashMap<String, String>();
		ITEM_PAID_BY_MAP.put("pursylaw@gmail.com", "多寶魚");
		ITEM_PAID_BY_MAP.put("tsangchungwing@gmail.com", "河童");

	}

	private SpreadsheetService getService() {
		GoogleAccountCredential googleCredential = GoogleAccountCredential
				.usingOAuth2(context, "https://spreadsheets.google.com/feeds",
						"https://docs.google.com/feeds");

		ITEM_PAID_BY = googleCredential.getAllAccounts()[0].name;

		googleCredential.setSelectedAccountName(ITEM_PAID_BY);
		Credential credential = new GoogleCredential.Builder()
				.setTransport(AndroidHttp.newCompatibleTransport())
				.setJsonFactory(new GsonFactory())
				.setRequestInitializer(googleCredential).build();

		credential.setAccessToken(authToken);

		SpreadsheetService spreadsheetService = new SpreadsheetService(
				"com.sbt.keep-v1");
		spreadsheetService.setOAuth2Credentials(credential);

		return spreadsheetService;

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_LOAD_SPREADSHEET:
				startSync((Expense)data.getSerializableExtra("expense"));
				break;
			}
		}
	}

	public static int notificationCount = 0;

	private void processData(final Expense expense)
			throws IOException, ServiceException {

		SpreadsheetQuery query = new SpreadsheetQuery(new URL(
				SPREADSHEET_FEED_URL));

		query.setTitleQuery(SPREADSHEET_NAME);
		query.setTitleExact(true);

		AsyncTask<SpreadsheetQuery, Integer, String> asyncTask = new AsyncTask<SpreadsheetQuery, Integer, String>() {

			protected void onPostExecute(String result) {
				if (result != null && result != "[]") {
					String notifyContext = SPREADSHEET_NAME + " Updated, "
							+ result;
					Toast.makeText(context, notifyContext, Toast.LENGTH_LONG)
							.show();

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context)
							.setSmallIcon(R.drawable.stat_sys_upload_done)
							.setContentTitle("$$$$")
							.setContentText(notifyContext);

					Intent resultIntent = new Intent(context,
							MainActivity.class);

					// The stack builder object will contain an artificial back
					// stack for the
					// started Activity.
					// This ensures that navigating backward from the Activity
					// leads out of
					// your application to the Home screen.
					TaskStackBuilder stackBuilder = TaskStackBuilder
							.create(context);
					// Adds the back stack for the Intent (but not the Intent
					// itself)
					// stackBuilder.addParentStack(MainActivity.class);
					// Adds the Intent that starts the Activity to the top of
					// the stack
					stackBuilder.addNextIntent(resultIntent);

					PendingIntent resultPendingIntent = stackBuilder
							.getPendingIntent(0,
									PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent(resultPendingIntent);
					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					// mId allows you to update the notification later on.
					mNotificationManager.notify(notificationCount++,
							mBuilder.build());
				}
			}

			@Override
			protected String doInBackground(SpreadsheetQuery... params) {
				SpreadsheetService service = getService();

				try {
					SpreadsheetFeed feed = service.query(params[0],
							SpreadsheetFeed.class);
					List<SpreadsheetEntry> files = feed.getEntries();

					if (files.size() > 0) {
						List<WorksheetEntry> worksheets = files.get(0)
								.getWorksheets();

						WorksheetEntry worksheetEntry = worksheets.get(0);

						ListFeed lists = service
								.getFeed(worksheetEntry.getListFeedUrl(),
										ListFeed.class);

						boolean isNew = false;
							ListEntry entry = null;

							// find a empty row for update.
							for (ListEntry e : lists.getEntries()) {
								CustomElementCollection customElements = e
										.getCustomElements();
								if (StringUtil
										.isEmptyOrWhitespace(customElements
												.getValue("date"))
										&& StringUtil
												.isEmptyOrWhitespace(customElements
														.getValue("amount"))
										&& StringUtil
												.isEmptyOrWhitespace(customElements
														.getValue("item"))
										&& StringUtil
												.isEmptyOrWhitespace(customElements
														.getValue("paidby"))) {
									entry = e;
									break;
								}
							}

							if (entry == null) {
								entry = new ListEntry();
								isNew = true;
							}

							// [date, year, month, weekday, type, item, amount,
							// paidby]
							Date postDate = new Date();
							Locale.setDefault(Locale.US);
							
							entry.getCustomElements().setValueLocal(
									"date",
									(String) DateFormat.format(
											"yyyy-MM-dd h:mm:ss", postDate));
							
							entry.getCustomElements().setValueLocal(
									"year",
									(String) DateFormat
											.format("yyyy", postDate));
							
							entry.getCustomElements().setValueLocal("month",
									(String) DateFormat.format("M", postDate));
							
							entry.getCustomElements()
							.setValueLocal(
									"weekday",
									(String) DateFormat.format("EEE",
											postDate));
							
							entry.getCustomElements().setValueLocal("type",
									"週不時");
							
							entry.getCustomElements().setValueLocal("amount",
									Double.toString(expense.getAmount()));
							
							entry.getCustomElements().setValueLocal(
									"item",
									expense.getItem());
							
							entry.getCustomElements().setValueLocal("paidby",
									ITEM_PAID_BY_MAP.get(ITEM_PAID_BY));

							if (isNew) {
								service.insert(worksheetEntry.getListFeedUrl(),
										entry);
							} else {
								entry.update();
							}

						return expense.toString();
					} else {
						// create spreadsheet
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ServiceException e) {
					e.printStackTrace();
				}

				return null;
			}
		};

		asyncTask.execute(query);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void startSync(final Expense expense) {
		// get auth
		AccountManager am = AccountManager.get(context);
		Account[] accounts = am
				.getAccountsByType(GoogleAccountManager.ACCOUNT_TYPE);
		am.getAuthToken(accounts[0], SPREADSHEET_AUTHTYPE, null, false,
				new AccountManagerCallback<Bundle>() {

					@Override
					public void run(AccountManagerFuture<Bundle> future) {
						try {
							Bundle result = future.getResult();
							authToken = result
									.getString(AccountManager.KEY_AUTHTOKEN);

							if (authToken == null) {
								Intent intent = (Intent) result
										.get(AccountManager.KEY_INTENT);
								//intent.putExtra("expense",  expense);

								context.startActivityForResult(intent,
										REQUEST_LOAD_SPREADSHEET);

							} else {
								processData(expense);
							}
						} catch (OperationCanceledException e) {
							e.printStackTrace();
						} catch (AuthenticatorException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ServiceException e) {
							e.printStackTrace();
						}

					}
				}, null);
	}
}
