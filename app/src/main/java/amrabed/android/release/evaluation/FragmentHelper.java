package amrabed.android.release.evaluation;

import android.app.Activity;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentHelper {

    private FragmentHelper() {}

    public static void loadFragment(Fragment fragment, FragmentManager fragmentManager) {
        if(fragmentManager != null) {
            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.content, fragment).commit();
        }
    }

    public static void setTitle(@StringRes int title, Activity activity) {
        if(activity != null) {
            activity.setTitle(title);
        }
    }

    public static void setTitle(String title, Activity activity) {
        if(activity != null) {
            activity.setTitle(title);
        }
    }
}