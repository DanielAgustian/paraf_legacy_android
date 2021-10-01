package com.yokesen.parafdigitalyokesen.util;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;


public class DynamicLinkUtil {
    Context context;
    public DynamicLinkUtil(Context context) {
        this.context = context;
    }

    public String dynamicLinkParaf(String type, int id){

        String idSign = String.valueOf(id);

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.teken.yokesen.com/dashboard/"+type+"/detail/"+idSign))
                .setDomainUriPrefix("https://tekenyokesen.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        return dynamicLinkUri.toString();
    }

    public void handlingDynamicLink(){

    }
}
