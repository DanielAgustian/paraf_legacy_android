package com.example.parafdigitalyokesen.util;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.parafdigitalyokesen.model.SignatureDetailModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class UtilFile {
    Context context;
    public UtilFile(Context context) {
        this.context = context;
    }

    public void downloadFile(Bitmap bitmap, String type, List<String> typeShare, String title) {
        if(type.equals(typeShare.get(0))){
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + title + ".png");
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch ( FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            try {
                fOut.flush();
                fOut.close();
                MediaStore.Images.Media.insertImage(context.getContentResolver(),f.getAbsolutePath(),f.getName(),f.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else  if(type.equals(typeShare.get(1))){
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + title+ ".jpg");
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch ( FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            try {
                fOut.flush();
                fOut.close();
                MediaStore.Images.Media.insertImage(context.getContentResolver(),f.getAbsolutePath(),f.getName(),f.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals(typeShare.get(2))){
            downloadPDF(bitmap, title);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void downloadFileAPI29(Bitmap bitmap, String type, List<String> typeShare, String title) {
        Log.d("FILETYPE", type);

        if(type.equals(typeShare.get(0))){
            ContentValues contentValues = new ContentValues();

            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, title +".png");
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.Downloads.IS_PENDING, true);
            Uri uri = null;
            uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            Uri itemUri = context.getContentResolver().insert(uri, contentValues);

            if (itemUri != null) {
                try {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(itemUri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false);

                    context.getContentResolver().update(itemUri, contentValues, null, null);
                    Toast.makeText(context, "Successfully Download Image: "+ itemUri.getPath(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else  if(type.equals(typeShare.get(1))){
            ContentValues contentValues = new ContentValues();

            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, title+".jpg");
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.Downloads.IS_PENDING, true);
            Uri uri = null;
            uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            Uri itemUri = context.getContentResolver().insert(uri, contentValues);

            if (itemUri != null) {
                try {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(itemUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(itemUri, contentValues, null, null);
                    Toast.makeText(context, "Successfully Download Image: "+ itemUri.getPath(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else if (type.equals(typeShare.get(2))){
            downloadPDF(bitmap,  title);
        }
    }
    void downloadPDF(Bitmap bitmap, String title){
        String stringFilePath = Environment.getExternalStorageDirectory().getPath() + "/Download/"+title + ".pdf";
        File file = new File(stringFilePath);
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));


        page.getCanvas().drawBitmap(bitmap,0,0 , paint);
        pdfDocument.finishPage(page);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        }
        catch (Exception e){
            e.printStackTrace();

        }
        Uri.parse(new File("/sdcard/cats.jpg").toString());
        pdfDocument.close();
        Toast.makeText(context, "Successfully Download PDF: "+ file.getPath(),
                Toast.LENGTH_LONG).show();
    }

    public void downloadFileURL(String link){
        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(link);
        String fileName = link.substring( link.lastIndexOf('/')+1, link.length() );
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
        downloadmanager.enqueue(request);
        Util util = new Util();
        util.toastMisc(context,"Downloaded with title "+ fileName);
    }

}
