package me.khrystal.jsbridge;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/11/28
 * update time:
 * email: 723526676@qq.com
 */

public class BridgeWebViewClient extends WebViewClient {

    private BridgeAdapter mAdapter;
    private WebViewClient mClient;

    public BridgeWebViewClient(BridgeAdapter adapter, WebViewClient client) {
        this.mAdapter = adapter;
        this.mClient = client;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (url.trim().equals(BridgeUtil.OVERRIDE_LOADED)) {
            if (BridgeAdapter.toLoadJs != null) {
                BridgeUtil.webViewLoadLocalJs(view, BridgeAdapter.toLoadJs);
            }
            return true;
        }else if (url.startsWith(BridgeUtil.RETURN_DATA)) {
            mAdapter.handlerReturnData(url);
            return true;
        } else if (url.startsWith(BridgeUtil.OVERRIDE_SCHEMA)) {
            mAdapter.flushMessageQueue(); // if response data == null return
            return true;
        } else {
            if (mClient != null)
                return mClient.shouldOverrideUrlLoading(view, url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (mClient != null)
            mClient.onPageStarted(view, url, favicon);
    }

    @Override
    public void onFormResubmission(WebView view, android.os.Message dontResend, android.os.Message resend) {
        super.onFormResubmission(view, dontResend, resend);
        if (mClient != null)
            mClient.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        if (mClient != null)
            mClient.onLoadResource(view, url);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onPageCommitVisible(WebView view, String url) {
        super.onPageCommitVisible(view, url);
        if (mClient != null)
            mClient.onPageCommitVisible(view, url);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        super.onReceivedClientCertRequest(view, request);
        if (mClient != null)
            mClient.onReceivedClientCertRequest(view, request);
    }

    /**
     * when onPageFinished load local WebViewJavascriptBridge.js file
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mClient != null)
            mClient.onPageFinished(view, url);
        //
        if (mAdapter.getStartupMessage() != null) {
            for (Message m : mAdapter.getStartupMessage()) {
                mAdapter.dispatchMessage(m);
            }
            mAdapter.setStartupMessage(null);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (mClient != null)
            mClient.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (mClient != null)
            mClient.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
        if (mClient != null)
            mClient.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        if (mClient != null)
            mClient.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        super.onReceivedLoginRequest(view, realm, account, args);
        if (mClient != null)
            mClient.onReceivedLoginRequest(view, realm, account, args);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        if (mClient != null)
            mClient.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        if (mClient != null)
            mClient.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        super.onUnhandledKeyEvent(view, event);
        if (mClient != null)
            mClient.onUnhandledKeyEvent(view, event);
    }

    /**
     * redirects 302 will handle in {@see BridgeWebViewClient#shouldOverrideUrlLoading}
     * @param view
     * @param cancelMsg
     * @param continueMsg
     */
    @Override
    public void onTooManyRedirects(WebView view, android.os.Message cancelMsg, android.os.Message continueMsg) {
        super.onTooManyRedirects(view, cancelMsg, continueMsg);
        if (mClient != null)
            mClient.onTooManyRedirects(view, cancelMsg, continueMsg);
    }
}
