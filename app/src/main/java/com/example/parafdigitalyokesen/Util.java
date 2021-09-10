package com.example.parafdigitalyokesen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.RequestBody;

public class Util {



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
        DateFormat df = new SimpleDateFormat("hh:mm");
        return df.format(date);
    }
    public void toastError(Context context, String apiName, Throwable throwable){
        Toast.makeText(context, "ERROR IN "+apiName+":" + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}
