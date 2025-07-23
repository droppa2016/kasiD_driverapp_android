package co.za.kasi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * created by {Andries Sebola} on {11/14/2022}.
 */
public class WebClientUtil extends WebViewClient {

    ReturnResponse returnResponse;
    private Context context;

    public WebClientUtil(Context context){
        this.context =context;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        Log.d("TAG_CHECKED", "getStatus: "+ "Page started");
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        Log.d("TAG_CHECKED", "getStatus: "+ "loading URL");
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        try{
            returnResponse = (ReturnResponse) context;
            returnResponse.response(false);
        } catch (Exception e){
            Log.e("TAG_CHECKED", "getStatus: "+ e.getMessage());
            Log.e("TAG_CHECKED", "getStatus: "+ "Failed");
        }
    }

    public interface ReturnResponse{
        void response(boolean checked);
    }

}
