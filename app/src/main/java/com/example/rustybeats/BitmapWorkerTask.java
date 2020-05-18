package com.example.rustybeats;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

public class BitmapWorkerTask extends AsyncTask<File,Void, Bitmap> {

    WeakReference<ImageView> imageViewWeakReference;
    ItemHelper item;
    final static int TARGET_IMAGE_VIEW_WIDTH = 100;
    final static int TARGET_IMAGE_VIEW_HEIGHT = 100;

    public BitmapWorkerTask(ImageView imageView, ItemHelper item){
        imageViewWeakReference = new WeakReference<>(imageView);
        item = item;
    }

    @Override
    protected Bitmap doInBackground(File... files) {
        return decodeBitmapFromFile(files[0]);

    }

    protected void onPostExecute(Bitmap bitmap){
        ImageView viewImage = imageViewWeakReference.get();
        if(bitmap !=null && imageViewWeakReference !=null){
            if(viewImage != null){
                viewImage.setImageBitmap(bitmap);
            }
        }
        else {
            viewImage.setImageResource(R.drawable.red);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options bmOptions){
        final int photoWidth = bmOptions.outWidth;
        final int photoHeight = bmOptions.outHeight;
        int scaleFactor = 1;

        if(photoWidth > TARGET_IMAGE_VIEW_WIDTH || photoWidth > TARGET_IMAGE_VIEW_HEIGHT){
            final int halfPhotoWidth = photoWidth/2;
            final int halfPhotoHeight = photoHeight/2;
            while(halfPhotoWidth/scaleFactor > TARGET_IMAGE_VIEW_WIDTH || halfPhotoHeight/scaleFactor > TARGET_IMAGE_VIEW_HEIGHT){
                scaleFactor *= 2;
            }

        }
        return scaleFactor;
    }

    private Bitmap decodeBitmapFromFile(File imageFile){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions);
        bmOptions.inJustDecodeBounds = false;
//        return BitmapFactory.decodeFile(imageFile.getAbsolutePath(),bmOptions);


        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(imageFile.getPath());
            byte[] data = mmr.getEmbeddedPicture();
            if(data != null){
                return BitmapFactory.decodeByteArray(data, 0, data.length, bmOptions);
            }

        } catch (RuntimeException ex) {
            ex.printStackTrace();

            return null;
        }




        return null;
    }


}
