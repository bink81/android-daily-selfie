package com.example.bink.dailyselfie;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ViewActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_image);

		Uri uri = this.getIntent().getData();
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;;
		Bitmap b = BitmapUtil.getPic(new File(uri.getPath()), width, height);
		ImageView view = (ImageView) findViewById(R.id.imageView1);
		view.setImageBitmap(b);
		TextView t = (TextView) findViewById(R.id.title);
		List<String> s = uri.getPathSegments();
		String title = s.get(s.size()-1);
		t.setText(title);
	}
}
