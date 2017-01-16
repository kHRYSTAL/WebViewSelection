package me.khrystal.webviewcustomselectiondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltipUtils;
import me.khrystal.selectionlib.SelectionWebView;
import me.khrystal.selectionlib.textselection.TextSelectionSupport;

public class MainActivity extends AppCompatActivity {

    private SelectionWebView mWebView;
    private TextSelectionSupport mTextSelectionSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mWebView = (SelectionWebView) findViewById(R.id.webView);
        mTextSelectionSupport = TextSelectionSupport.support(this, mWebView);
        mTextSelectionSupport.setSelectionListener(new TextSelectionSupport.SelectionListener() {

            @Override
            public void startSelection() {
                Toast.makeText(MainActivity.this, "startSelection", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void selectionChanged(String text, View anchorStartView, View anchorEndView) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void endSelection() {
                Toast.makeText(MainActivity.this, "endSelection", Toast.LENGTH_SHORT).show();
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                mTextSelectionSupport.onScaleChanged(oldScale, newScale);
            }
        });

        mWebView.loadUrl("file:///android_asset/huxiu.html");
    }
}
