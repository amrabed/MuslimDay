package amrabed.android.release.evaluation.guide;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RawRes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import amrabed.android.release.evaluation.R;

/**
 * Fragment to show details of guide entry
 */
public class DetailsFragment extends Fragment {
    private static final String ENTRY = "entry";
    private static final String TITLE = "title";

    private static final String TAG = DetailsFragment.class.getCanonicalName();

    public static DetailsFragment newInstance(@RawRes int entry, String title) {
        final DetailsFragment detailsFragment = new DetailsFragment();

        final Bundle args = new Bundle();
        args.putInt(ENTRY, entry);
        args.putString(TITLE, title);
        detailsFragment.setArguments(args);

        return detailsFragment;
    }

    /**
     * Read text from raw input file
     *
     * @param inputStream input file stream
     * @return Text content of file
     */
    private static String readText(InputStream inputStream) {

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return byteArrayOutputStream.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getArguments().getString(TITLE, null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.guide_entry, container, false);
        TextView text = view.findViewById(R.id.text);
        text.setText(readText(getResources().openRawResource(getArguments().getInt(ENTRY, 0))));
        return view;
    }
}