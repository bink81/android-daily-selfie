package com.example.bink.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	
	private static final long ALARM_REPEAT = 2 * 60 * 1000L;

	private ListView mListView;
	private ArrayAdapter<File> mAdapter;
	private ArrayList<File> listItems = new ArrayList<File>();
	AlarmManager mAlarmManager;
	PendingIntent mNotificationReceiverPendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		mListView = (ListView) findViewById(R.id.images);
		initFiles();
		mAdapter = new MySimpleArrayAdapter(getApplicationContext(), listItems);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Log.d(TAG, "pos:" + position);
				Intent i = new Intent(MainActivity.this, ViewActivity.class);
				File f = listItems.get(position);
				Uri uri = Uri.fromFile(f);
				i.setData(uri);
				startActivity(i);
			}
		});
		setSupportActionBar(toolbar);
		createAlarm();
	}

	private void createAlarm() {
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent mNotificationReceiverIntent = new Intent(this,
				AlarmReceiver.class);
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 234324243, mNotificationReceiverIntent, 0);
		mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + ALARM_REPEAT,
				ALARM_REPEAT,
				mNotificationReceiverPendingIntent);
	}

	private void initFiles() {
		File dir = new File(getImageDir());
		File files[] = dir.listFiles();
		Log.d(TAG, "Size: " + files.length);
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			listItems.add(file);
		}
	}

	static final int REQUEST_IMAGE_CAPTURE = 1;

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			File file = null;
			try {
				file = createImageFile();
			} catch (IOException ex) {
				Log.e(TAG, "file error", ex);
			}
			// Continue only if the File was successfully created
			if (file != null) {
				try {
					FileOutputStream fOut = new FileOutputStream(file);
					imageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
					fOut.flush();
					fOut.close();
					MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
					mAdapter.add(file);

					Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					Uri contentUri = Uri.fromFile(file);
					mediaScanIntent.setData(contentUri);
					sendBroadcast(mediaScanIntent);
				} catch (FileNotFoundException e) {
					Log.e(TAG, "file error", e);
				} catch (IOException e) {
					Log.e(TAG, "file error", e);
				}
			} else Toast.makeText(getApplicationContext(), "error in creating file",
					Toast.LENGTH_LONG).show();
		}
	}

	File mCurrentPhotoPath;

	private File createImageFile() throws IOException {
		// Create an image file name
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss");
		String currentDateandTime = sdf.format(Calendar.getInstance().getTime());
		String dir = getImageDir();
		File storageDir = new File(dir);
		Log.d(TAG, "path:" + storageDir.getAbsolutePath());
		if (!storageDir.exists()) {
			storageDir.mkdirs();
			Log.d(TAG, "canWrite:" + storageDir.canWrite()+".");
		}
		File image = new File(storageDir, currentDateandTime+ ".jpg");
		image.createNewFile();

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image;
		return image;
	}

	private String getImageDir() {
		return Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/selfie";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		dispatchTakePictureIntent();
		return super.onOptionsItemSelected(item);
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<File> {
		private final Context context;
		private final ArrayList<File> values;

		public MySimpleArrayAdapter(Context context, ArrayList<File> values) {
			super(context, -1, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.label);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
			File file = values.get(position);
			Bitmap bitmap = BitmapUtil.getPic(file, 50, 50);
			imageView.setImageBitmap(bitmap);
			textView.setText(file.getName().replace(".jpg", ""));
			return rowView;
		}
	}
}
