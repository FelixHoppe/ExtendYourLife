package felix_h.de.increaseyourlifetime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class AppRater {
    private final static int DAYS_UNTIL_PROMPT = 3;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 3;//Min number of launches
    private static long launch_counter = 0;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);
        AppRater.set_launch_counter(launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    private static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.app_rater_layout, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setTitle(mContext.getString(R.string.like_my_app));
        dialog.show();


        Button b1 = mView.findViewById(R.id.b1);
        b1.setOnClickListener(v -> {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
            if (editor != null) {
                editor.putBoolean("dontshowagain", true);
                editor.commit();
            }
            dialog.dismiss();
        });

        Button b2 = mView.findViewById(R.id.b2);
        b2.setOnClickListener(v -> {
            if (editor != null) {
                editor.putBoolean("dontshowagain", true);
                editor.commit();
            }
            dialog.dismiss();
        });

        Button b3 = mView.findViewById(R.id.b3);
        b3.setOnClickListener(v -> dialog.dismiss());
    }

    private static void set_launch_counter(long _launch_counter) {
        AppRater.launch_counter = _launch_counter;
    }

    public static long get_launch_counter(){
        return AppRater.launch_counter;
    }
}
