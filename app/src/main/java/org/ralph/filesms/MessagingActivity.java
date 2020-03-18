package org.ralph.filesms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MessagingActivity extends AppCompatActivity {

	private static final int PERMISSION_SEND_SMS_AND_READ_PHONE_STATE = 48;

	private static final int CSV_HEADER_TYPE_NAME_NUMBER_MESSAGE = 5;
	private static final int CSV_HEADER_TYPE_NUMBER_MESSAGE = 65;
	private static final int CSV_HEADER_TYPE_UNDEFINED = -1;
	private static final String SMS_SENT = "Sms_sent";
	private static final String ADAPTER_INDEX = "Adapter index";
	private static final int INTENT_SEND_MESSAGE_CODE = 5555;

	private CustomAdapter customAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messaging);

		String filePath = "";

		Intent recievedIntent = getIntent();

		if (!recievedIntent.getStringExtra(MainActivity.PATH_EXTRA_NAME).equals(null)) {
			filePath = recievedIntent.getStringExtra(MainActivity.PATH_EXTRA_NAME);
		} else {
			// TODO : throw new runtime excp(maybe)
		}

		ListView listViewAllMessages = findViewById(R.id.list_view_all_messages);
		customAdapter = new CustomAdapter(this, 123, new ArrayList<SMS>());   //(I don't know what to give in resource)
		listViewAllMessages.setAdapter(customAdapter);

		Log.d(MainActivity.LOG_DEBUG, filePath);

		startMessaging(filePath);
	}

	@AfterPermissionGranted(PERMISSION_SEND_SMS_AND_READ_PHONE_STATE)
	private void startMessaging(String filePath) {

		//TODO : Create a help button, to show some examples of the input file.


		String[] perms = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS};
		if (EasyPermissions.hasPermissions(this, perms)) {
			List<SMS> smsList = readCSV(filePath);
			customAdapter.addAll(smsList);
			sendMessages(smsList);
		} else {
			String permissionRationale = "Please give permission to send sms, and\n" +
					"Read phone state permission is required for android devices, with android 6(Marshmellow) or more, to send messages.";
			EasyPermissions.requestPermissions(this, permissionRationale, PERMISSION_SEND_SMS_AND_READ_PHONE_STATE, perms);
		}
		//TODO : If the message is too long, then divide the message into parts.
		//TODO : Check isEndOfFile()
		//TODO : Use String message.
		//TODO : Here will go the messaging code...
		//TODO : Check error, send message, Check error, send message(Every time check errors).
		//TODO : Check error like numbers of 1256 digits!, and add them to "FAILED" tab.
	}

	private List<SMS> readCSV(String filePath) {
		File file = new File(filePath);
		CsvReader csvReader = new CsvReader();
		csvReader.setContainsHeader(true);

		CsvContainer csvContainer;


		List<SMS> smsList = new ArrayList<>();

		int CSV_ROW_COUNT_LIMIT = 500;
		//TODO : Get no.of rows managable by android(do not test it on just your phone).
		//TODO : Show a warning...


		try {
			csvContainer = csvReader.read(file, StandardCharsets.UTF_8);
			if (csvContainer.getRowCount() > CSV_ROW_COUNT_LIMIT) {
				//TODO : Show a warning...
			} else {
				if (getCSVheaderType(csvContainer.getHeader()) == CSV_HEADER_TYPE_NAME_NUMBER_MESSAGE) {
					for (CsvRow eachRow : csvContainer.getRows()) {
						smsList.add(new SMS(eachRow.getField("Name"), eachRow.getField("Number"), eachRow.getField("Message")));
					}
				} else if (getCSVheaderType(csvContainer.getHeader()) == CSV_HEADER_TYPE_NUMBER_MESSAGE) {
					for (CsvRow eachRow : csvContainer.getRows()) {
						smsList.add(new SMS(eachRow.getField("Number"), eachRow.getField("Message")));
					}
				} else {
					Log.d(MainActivity.LOG_INFO, "Header type did not match : " + csvContainer.getHeader());
					// TODO : Show fatal error.
				}
			}
		} catch (IOException e) {
			Log.e(MainActivity.LOG_ERROR, "Error in reading csv");
			e.printStackTrace();
		}

		return smsList;


/*

TODO :
There is another way:

try {
			CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8);
			CsvRow row;
			if (csvParser.nextRow() != null) {
				getCSVheaderType(csvParser.getHeader()) == CSV_HEADER_TYPE_NAME_NUMBER_MESSAGE
			} else {

			}
			if () {
				while ((row = csvParser.nextRow()) != null) {
					smsList.add(new SMS(row.getField("Name"), row.getField("Number"), row.getField("Message")));
				}
			} else if (getCSVheaderType(csvParser.getHeader()) == CSV_HEADER_TYPE_NUMBER_MESSAGE) {
				while ((row = csvParser.nextRow()) != null) {
					smsList.add(new SMS(row.getField("Number"), row.getField("Message")));
				}
			} else {
				// TODO : Show fatal error.
			}

		} catch (Exception e) {
			Log.e(MainActivity.LOG_ERROR, "Error in reading csv");
			e.printStackTrace();
		}

		------somewhat like this...


		THis is actually reading each line and converting it to SMS object, and adding to smsList
		tO get The header type in this approach is complicated(little bit).. You need to call csvParser.nextRow() once
		before reading the header type...... may think about it...

 */

	}

	private void sendMessages(List<SMS> smsList) {


		Log.d(MainActivity.LOG_DEBUG, smsList.toString());

		int index = 0;

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				if (getResultCode() == Activity.RESULT_OK) {
					customAdapter.getItem(arg1.getIntExtra(ADAPTER_INDEX, 0)).setMessageSendingStatus(SMS.MESSAGE_SENDING_SUCCESS);
					customAdapter.notifyDataSetChanged();
				} else {
					customAdapter.getItem(arg1.getIntExtra(ADAPTER_INDEX, 0)).setMessageSendingStatus(SMS.MESSAGE_SENDING_FAILED);
					customAdapter.getItem(arg1.getIntExtra(ADAPTER_INDEX, 0)).setFailedReason(arg1.getDataString());
					customAdapter.notifyDataSetChanged();
				}
			}
		}, new IntentFilter(SMS_SENT));

		for (SMS eachSms : smsList) {
			sendSMS(eachSms, index);
			index += 1;
		}
	}

	private void sendSMS(SMS sms, int index) {
		SmsManager.getDefault().sendTextMessage(sms.getNumber(),
				null,
				sms.getMessage(),
				PendingIntent.getBroadcast(this,
						INTENT_SEND_MESSAGE_CODE,
						new Intent(SMS_SENT).putExtra(ADAPTER_INDEX, index),
						0),
				null);
	}

	private int getCSVheaderType(List<String> header) {

		//TODO : Show the user an example of a good csv file.

		if (header.size() == 3) {
			if (header.get(0).equals("Name") && header.get(1).equals("Number") && header.get(2).equals("Message")) return CSV_HEADER_TYPE_NAME_NUMBER_MESSAGE;
			else return CSV_HEADER_TYPE_UNDEFINED;
		} else if (header.size() == 2) {
			if ((header.get(0).equals("Number") && header.get(1).equals("Message"))) return CSV_HEADER_TYPE_NUMBER_MESSAGE;
			else return CSV_HEADER_TYPE_UNDEFINED;
		} else return CSV_HEADER_TYPE_UNDEFINED;
	}

	private class CustomAdapter extends ArrayAdapter<SMS> {

		Context context;
		List<SMS> smsList;

		public CustomAdapter(Context context, int resource, List<SMS> objects) {
			super(context, resource, objects);
			this.context = context;
			smsList = objects;
		}

		@Override
		public int getCount() {
			return smsList.size();
		}

		@NonNull
		@Override
		public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			final SMS sms = smsList.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.sms, parent, false);
			}
			TextView textViewNumber = convertView.findViewById(R.id.textView_number);
			TextView textViewStatus = convertView.findViewById(R.id.textView_status);
			Log.d(MainActivity.LOG_DEBUG, "getViev\t" + sms.toString());
			textViewNumber.setText(sms.getNumber());
			textViewStatus.setText(Integer.toString(sms.getMessageSendingStatus()));
			textViewStatus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendSMS(smsList.get(position), position);
				}
			});
			return convertView;
		}
	}
}
