package sample.android.veritrans.co.id.sampleandroidapi.view;

import android.content.Context;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by Anis on 11/14/2014.
 */
public class CustomWebView extends WebView {
    public CustomWebView(Context context)
    {
        super(context);
    }

    // Note this!
    @Override
    public boolean onCheckIsTextEditor()
    {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                if (!hasFocus())
                    requestFocus();
                break;
        }

        return super.onTouchEvent(ev);
    }


}
