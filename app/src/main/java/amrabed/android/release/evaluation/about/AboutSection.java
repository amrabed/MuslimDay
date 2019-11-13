package amrabed.android.release.evaluation.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import amrabed.android.release.evaluation.R;

public class AboutSection extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final TextView view = (TextView) inflater.inflate(R.layout.about, container, false);
        view.setText(R.string.about_content1);
        return view;
    }
}
