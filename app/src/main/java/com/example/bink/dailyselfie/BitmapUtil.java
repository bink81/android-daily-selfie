package com.example.bink.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class BitmapUtil {
	public static Bitmap getPic(File file, int width, int height) {
		String uri = file.getAbsolutePath();
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(uri, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / width, photoH / height);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		return BitmapFactory.decodeFile(uri, bmOptions);
	}
}
