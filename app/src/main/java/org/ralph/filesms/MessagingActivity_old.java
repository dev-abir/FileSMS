package org.ralph.filesms;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;

public class MessagingActivity_old extends AppCompatActivity {}/*

	/*private static final int PERMISSION_SEND_SMS = 45;
	private static final int PERMISSION_READ_PHONE_STATE = 49;
	private static final int PERMISSION_SEND_SMS_AND_READ_PHONE_STATE = 48;

	public static final int countryCodeForIndia = 91;

	private ListView listViewAllMessages;
	private Intent recievedIntent;

	public static String message;

	private String filePath;
	private int countryCode;

	private CustomAdapter customAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messaging);

		recievedIntent = getIntent();

		if (!recievedIntent.getStringExtra(MainActivity.PATH_EXTRA_NAME).equals(null)) {
			filePath = recievedIntent.getStringExtra(MainActivity.PATH_EXTRA_NAME);
			//message = recievedIntent.getStringExtra(ActivityGetMessageAndCountryCode.MESSAGE_EXTRA_NAME);
			//countryCode = recievedIntent.getIntExtra(ActivityGetMessageAndCountryCode.COUNTRY_CODE_EXTRA_NAME, countryCodeForIndia);    //Setting default country code to country code of India.
		}

		listViewAllMessages = findViewById(R.id.list_view_all_messages);
		//adapter = new ArrayAdapter<SMS>(this, android.R.layout.simple_list_item_1, new ArrayList<SMS>());
		customAdapter = new CustomAdapter(this, 123, new ArrayList<SMS>());   //(I don't know what to give in resource)
		listViewAllMessages.setAdapter(customAdapter);

		if (!isPermissionGranted_sendSMS()) {
			askPermission_sendSMS();
		}
		if (!isPermissionGranted_readPhoneState()) {
			askPermission_readPhoneState();
		}
		startMessaging(filePath, message, countryCode);
	}

	private boolean isPermissionGranted_readPhoneState() {
		return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
	}

	private void askPermission_readPhoneState() {
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_SEND_SMS);
	}

	private boolean isPermissionGranted_sendSMS() {
		return (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);
	}

	private void askPermission_sendSMS() {
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case PERMISSION_SEND_SMS:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "Send sms Permission granted!", Toast.LENGTH_LONG).show();        //Use getApplicationContext(), or this
					startMessaging(filePath, message, countryCode);
				} else {
					askPermissionWithDialog(PERMISSION_SEND_SMS, "Please give permission to send sms.");
				}
				break;
			case PERMISSION_READ_PHONE_STATE:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "Send sms Permission granted!", Toast.LENGTH_LONG).show();        //Use getApplicationContext(), or this
					startMessaging(filePath, message, countryCode);
				} else {
					askPermissionWithDialog(PERMISSION_READ_PHONE_STATE, "Read phone state permission is required for android devices, with android 6(Marshmellow) or more, to send messages.");
				}
				break;
		}
	}

	private void askPermissionWithDialog(final int permissionType, String dialogMessage) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Permission request");
		alertDialog.setMessage(dialogMessage);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case AlertDialog.BUTTON_NEUTRAL: {
						switch (permissionType) {
							case PERMISSION_SEND_SMS:
								askPermission_sendSMS();
								break;
							case PERMISSION_READ_PHONE_STATE:
								askPermission_readPhoneState();
								break;
						}
						break;
					}
				}
			}
		});
		alertDialog.show();
		//finish();
	}

	//TODO : Create a help button, to show some examples of the input file.


    /*
    private void checkValidityOfFile(String filePath) {
        /**
         * Examples :
         * <NUMBERS>
         *      1234567890
         * 0987654321
         * 5556661112</NUMBERS>
         * <NUMBER>5265841256</NUMBER>
         * <MESSAGE>Hello how are you?</MESSAGE>
         *
         *
         *
         * -------OR-------
         *
         *
         *
         * <NUMBER>1234567890</NUMBER>
         * <NUMBER>0987654321</NUMBER>
         * <MESSAGE>Hello how are you?</MESSAGE>
         * <NUMBER>1234567890</NUMBER>
         * <NUMBER>0987654321</NUMBER>
         * <NUMBER>5556669998</NUMBER>
         * <MESSAGE>What are you doing?</MESSAGE>
         *
         *
         *
         *

        File file = new File(Environment.getExternalStorageDirectory(), filePath);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            //TODO : Create this method.
            br.close(); //TODO : br.close(), and fr.close() could cause errors, while reading this file for the next time.
            fr.close();
        } catch (FileNotFoundException e) {
            Log.d(MainActivity.LOG_ERROR, "File not found Exception!");
            Toast.makeText(this, "File not found!", Toast.LENGTH_LONG);
        } catch (IOException e) {
            Log.d(MainActivity.LOG_ERROR, "IO Exception!");
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG);
        }
        //TODO : If file is not valid, show that the file is not valid, and return to MainActivity.
    }


	public static String getMessage() {
		return message;
	}

	@AfterPermissionGranted(PERMISSION_SEND_SMS_AND_READ_PHONE_STATE)
	private void startMessaging(String filePath, String message, int countryCode) {
		Toast.makeText(getApplicationContext(), filePath, Toast.LENGTH_LONG).show();   //Use getApplicationContext(), or this
		MessageFileReader fileReader = new MessageFileReader(filePath, countryCode);
		fileReader.initReader();
		Error errorObject;
		if ((errorObject = fileReader.getErrorObject()) != null) {
			Log.d(MainActivity.LOG_ERROR, errorObject.getErrorMessage());
			Toast.makeText(this, errorObject.getErrorMessage(), Toast.LENGTH_LONG).show();
		} else {
			Log.d(MainActivity.LOG_INFO, "The file seems to be okay.");
			Toast.makeText(this, "The file seems to be okay.", Toast.LENGTH_LONG).show();
			SmsManager smsManager = SmsManager.getDefault();
			while (true) {
				if (fileReader.isEndOfFile()) {
					break;
				} else if ((errorObject = fileReader.getErrorObject()) != null) {
					if (errorObject.getType() == Error.TYPE_VERY_LONG_NUMBER) {
						customAdapter.add(new SMS("", SMS.NO_STATUS, errorObject.getErrorMessage()));
						Log.d(MainActivity.LOG_ERROR, errorObject.getErrorMessage());
						Toast.makeText(this, errorObject.getErrorMessage(), Toast.LENGTH_LONG).show();
					} else {
						Log.d(MainActivity.LOG_ERROR, errorObject.getErrorMessage());
						Toast.makeText(this, errorObject.getErrorMessage(), Toast.LENGTH_LONG).show();
					}
					break;
				} else {
					//TODO : If the message is too long, then divide the message into parts.
					sendMessage(fileReader.getNextNumber());
					//TODO : Check isEndOfFile()
					//TODO : Use String message.
					//TODO : Here will go the messaging code...
					//TODO : Check error, send message, Check error, send message(Every time check errors).
					//TODO : Check error like numbers of 1256 digits!, and add them to "FAILED" tab.
				}
			}
		}

		fileReader.clearResources();
		//No need to declare the lines below, while using Scanner.
        /*if((errorObject = fileReader.getErrorObject()) != null) {
            Log.d(MainActivity.LOG_ERROR, errorObject.getErrorMessage());
            Toast.makeText(this, errorObject.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
	}

	private void sendMessage(String number) {
		try {
			//PendingIntent pendingIntent = new
			//TODO : Properly know about : "smsManager.sendTextMessage(...)" method.
			Log.d(MainActivity.LOG_TEST, "Sending message : " + number);
			//smsManager.sendTextMessage(fileReader.getNextNumber(), null, message, null, null);
			customAdapter.add(new SMS(number, SMS.MESSAGE_SENDING_SUCCESS, null));
		} catch (Exception e) {
			customAdapter.add(new SMS(number, SMS.MESSAGE_SENDING_FAILED, e.getMessage()));
			Log.d(MainActivity.LOG_ERROR, e.getMessage());
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	private class CustomAdapter extends ArrayAdapter<SMS> {

		Context context;
		List<SMS> smsList;

		public CustomAdapter(Context context, int resource, List<SMS> objects) {
			super(context, resource, objects);
			this.context = context;
			smsList = objects;
		}

		@NonNull
		@Override
		public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			final SMS sms = getItem(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.sms, parent, false);
			}
			TextView textViewNumber = convertView.findViewById(R.id.textView_number);
			TextView textViewStatus = convertView.findViewById(R.id.textView_status);
			Log.d(MainActivity.LOG_DEBUG, sms.getNumber());
			Log.d(MainActivity.LOG_DEBUG, Integer.toString(sms.getMessageSendingStatus()));
			textViewNumber.setText(sms.getNumber());
			textViewStatus.setText(Integer.toString(sms.getMessageSendingStatus()));
			textViewStatus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendMessage(smsList.get(position).getNumber());
				}
			});
			return convertView;
		}
	}
}
*/