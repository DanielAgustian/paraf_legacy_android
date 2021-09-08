package com.example.parafdigitalyokesen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

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
        if(pd!=null){
            pd.draw(canvas);
        }
        return bitmap;
    }

}
