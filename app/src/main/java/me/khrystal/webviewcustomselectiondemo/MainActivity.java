package me.khrystal.webviewcustomselectiondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
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
                SimpleTooltip builder = new SimpleTooltip.Builder(MainActivity.this)
                        .anchorView(anchorStartView)
                        .text(R.string.copy)
                        .showArrow(true)
                        .animated(true)
                        .margin(0.0f)
                        .gravity(Gravity.TOP)
                        .dismissOnOutsideTouch(true)
                        .dismissOnInsideTouch(false)
                        .build();
                builder.show();
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
