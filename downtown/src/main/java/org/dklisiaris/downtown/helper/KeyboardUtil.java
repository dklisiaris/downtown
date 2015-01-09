package org.dklisiaris.downtown.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil
{
public static void hideKeyboard(Activity activity)
    {
        try
        {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
         }
        catch (Exception e)
        {
            // Ignore exceptions if any
                Log.e("KeyBoardUtil", e.toString(), e);
        }
    }
}