package com.yokesen.parafdigitalyokesen.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.yokesen.parafdigitalyokesen.BuildConfig;
import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.model.InviteSignersModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.RequestBody;

public class Util {

    public String milisNow(){
        Date date = new Date();
        long timeMilli = date.getTime();
        return String.valueOf(timeMilli);
    }

    public int dpToPx(int data, Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, data, context.getResources().getDisplayMetrics());
    }

    public void shareData(Uri uri, String mime, Context context){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType(mime);
        context.startActivity(Intent.createChooser(shareIntent, "Send Your File"));
    }


    public void shareLink(Context context, String link){
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }


    public ArrayList<String> getDataEmails(ListView lvSigners, ArrayList<InviteSignersModel> list){
        ArrayList<String> emails = new ArrayList<>();
        for (int i =0; i< list.size(); i++){
            View viewListItem = lvSigners.getChildAt(i);
            EditText editText = viewListItem.findViewById(R.id.etDialogItemInvEmail);
            String string = editText.getText().toString();
            emails.add(string);
        }
        return emails;
    }

    public RequestBody requestBodyString( String value){
        RequestBody body =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, value);
        return body;
    }

    public PictureDrawable makeQRCOde(String qr){
        SVG svg = null;
        try {
            svg = SVG.getFromString(qr);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
        PictureDrawable pd = new PictureDrawable(svg.renderToPicture());

        return pd;
    }
    public Bitmap makeBitmap(PictureDrawable pd){
        Bitmap bitmap = Bitmap.createBitmap(pd.getIntrinsicWidth(), pd.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if(pd != null){
            pd.draw(canvas);
        }
        return bitmap;
    }

    public String CalendarToDateString(Calendar calendar){
        Date date= calendar.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public String CalendarToTimeString(Calendar calendar){
        Date date = calendar.getTime();
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(date);
    }
    public void toastError(Context context, String apiName, Throwable throwable){
        Toast.makeText(context, "ERROR IN "+apiName+":" + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    public void toastMisc(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public void changeColorEditText(EditText editText, boolean validator, Context context){
        Resources res = context.getResources();
        if(validator){
            editText.setTextColor(res.getColor(R.color.colorError));
            ColorStateList colorStateList = ColorStateList.valueOf(res.getColor(R.color.colorError));
            ViewCompat.setBackgroundTintList(editText, colorStateList);
        } else{
            editText.setTextColor(res.getColor(R.color.colorGrayText));
            ColorStateList colorStateList = ColorStateList.valueOf(res.getColor(R.color.colorLightGrayText));
            ViewCompat.setBackgroundTintList(editText, colorStateList);
        }
    }
}
