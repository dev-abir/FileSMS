package org.ralph.filesms;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.regex.Pattern;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

	public static final String LOG_DEBUG = "Debug";
	public static final String LOG_TEST = "Test";
	public static final String LOG_INFO = "Info";
	public static final String LOG_ERROR = "Error";

	private static final int REQUEST_OPEN_READ_FILE = 3;
	private static final int PERMISSION_REQUEST_READ_INTERNAL_STORAGE = 4;

	public static final String PATH_EXTRA_NAME = "Extra_from_MAIN_containing_path";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button buttonChooseFile = findViewById(R.id.button_choose_file);
		buttonChooseFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startMessagingActivity();
			}
		});

	}

	@AfterPermissionGranted(PERMISSION_REQUEST_READ_INTERNAL_STORAGE)
	private void startMessagingActivity() {

		if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
			new MaterialFilePicker()
					.withActivity(this)
					.withRequestCode(REQUEST_OPEN_READ_FILE)
					.withFilter(Pattern.compile(".+\\.csv")) // Filtering files and directories by file name using regexp
					.withHiddenFiles(true) // Show hidden files and folders
					.start();
		} else {
			EasyPermissions.requestPermissions(this, "Read file permission is needed to select a \'.csv\' file",
					PERMISSION_REQUEST_READ_INTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_OPEN_READ_FILE && resultCode == RESULT_OK) {
			String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
			Log.d(LOG_DEBUG, filePath);

			Intent intentStartMessagingActivity = new Intent(this, MessagingActivity.class);
			intentStartMessagingActivity.putExtra(MainActivity.PATH_EXTRA_NAME, filePath);
			startActivity(intentStartMessagingActivity);
		}
		//TODO : Show the user an example of a good csv file.
	}


}