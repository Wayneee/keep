package com.sbt.Keep.Helper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;

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

public class SpreadsheetHelper {
	private static final String ITEM_DESCRIPTION = "Add by android on ";
	private static final int REQUEST_LOAD_SPREADSHEET = 0;
	private static final String SPREADSHEET_AUTHTYPE = "oauth2:https://spreadsheets.google.com/feeds/";
	private static final String SPREADSHEET_FEED_URL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";
	private static final String SPREADSHEET_NAME = "日常開資";
	private static String ITEM_PAID_BY = "Android";
	private String authToken;

	private Activity context;

	public SpreadsheetHelper(Activity context) {
		this.context = context;
	}

	private SpreadsheetService getService() {
		GoogleAccountCredential googleCredential = GoogleAccountCredential.usingOAuth2(context, "https://spreadsheets.google.com/feeds",
				"https://docs.google.com/feeds");
		
		ITEM_PAID_BY = googleCredential.getAllAccounts()[0].name;
		
		googleCredential.setSelectedAccountName(ITEM_PAID_BY);
		Credential credential = new GoogleCredential.Builder().setTransport(AndroidHttp.newCompatibleTransport()).setJsonFactory(new GsonFactory())
				.setRequestInitializer(googleCredential).build();

		credential.setAccessToken(authToken);

		SpreadsheetService spreadsheetService = new SpreadsheetService("com.sbt.keep-v1");
		spreadsheetService.setOAuth2Credentials(credential);

		return spreadsheetService;

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_LOAD_SPREADSHEET:
				;
				startSync(data.getStringArrayListExtra("amounts"));
				break;
			}
		}
	}

	private void processData(final ArrayList<String> arrayList) throws IOException, ServiceException {

		SpreadsheetQuery query = new SpreadsheetQuery(new URL(SPREADSHEET_FEED_URL));

		query.setTitleQuery(SPREADSHEET_NAME);
		query.setTitleExact(true);

		AsyncTask<SpreadsheetQuery, Integer, String> asyncTask = new AsyncTask<SpreadsheetQuery, Integer, String>() {

			@Override
			protected String doInBackground(SpreadsheetQuery... params) {
				SpreadsheetService service = getService();

				try {
					SpreadsheetFeed feed = service.query(params[0], SpreadsheetFeed.class);
					List<SpreadsheetEntry> files = feed.getEntries();

					if (files.size() > 0) {
						List<WorksheetEntry> worksheets = files.get(0).getWorksheets();

						WorksheetEntry worksheetEntry = worksheets.get(0);

						ListFeed lists = service.getFeed(worksheetEntry.getListFeedUrl(), ListFeed.class);
						
						boolean isNew = false;
						for (String amount : arrayList) {

							ListEntry entry = null;

							// find a empty row for update.
							for (ListEntry e : lists.getEntries()) {
								CustomElementCollection customElements = e.getCustomElements();
								if (StringUtil.isEmptyOrWhitespace(customElements.getValue("date"))
										&& StringUtil.isEmptyOrWhitespace(customElements.getValue("amount"))
										&& StringUtil.isEmptyOrWhitespace(customElements.getValue("item"))
										&& StringUtil.isEmptyOrWhitespace(customElements.getValue("paidby"))) {
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
							
							entry.getCustomElements().setValueLocal("date", (String) DateFormat.format("yyyy-MM-dd h:mm:ss", postDate));
							entry.getCustomElements().setValueLocal("amount", amount);
							entry.getCustomElements().setValueLocal("item", ITEM_DESCRIPTION +(String) DateFormat.format("h:mm:ss", postDate) );
							entry.getCustomElements().setValueLocal("paidby", ITEM_PAID_BY);

							if (isNew) {
								service.insert(worksheetEntry.getListFeedUrl(), entry);
							} else {
								entry.update();
							}
						}
					} else {
						// create spreadsheet
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}
		};

		asyncTask.execute(query);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void startSync(final ArrayList<String> arrayList) {
		// get auth
		AccountManager am = AccountManager.get(context);
		Account[] accounts = am.getAccountsByType(GoogleAccountManager.ACCOUNT_TYPE);
		am.getAuthToken(accounts[0], SPREADSHEET_AUTHTYPE, null, false, new AccountManagerCallback<Bundle>() {

			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle result = future.getResult();
					authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

					if (authToken == null) {
						Intent intent = (Intent) result.get(AccountManager.KEY_INTENT);
						intent.putExtra("amounts", arrayList);

						context.startActivityForResult(intent, REQUEST_LOAD_SPREADSHEET);

					} else {
						processData(arrayList);
					}
				} catch (OperationCanceledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AuthenticatorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, null);
	}
}
