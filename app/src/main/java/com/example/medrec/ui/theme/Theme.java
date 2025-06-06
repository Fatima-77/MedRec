package com.example.medrec.ui.theme;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Theme {
    public static void applyTheme(Activity activity) {
        // Set Status Bar Color to Purple
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.PURPLE);
        }

        // Optionally set background colors for activities, buttons etc manually
        // Example: activity.findViewById(android.R.id.content).setBackgroundColor(AppColors.GREY);
    }
}
