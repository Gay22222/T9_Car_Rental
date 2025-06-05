package com.uit.carrental.FragmentPages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.uit.carrental.R;

public class SplashFragment extends Fragment {

    private static final String ARG_LAYOUT = "layout";
    private static final String ARG_POSITION = "position";
    private OnNextButtonClickListener listener;

    public interface OnNextButtonClickListener {
        void onNextButtonClicked(int position);
    }

    public static SplashFragment newInstance(int layoutResId, int position) {
        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layoutResId);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof OnNextButtonClickListener) {
            listener = (OnNextButtonClickListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutResId = getArguments() != null ? getArguments().getInt(ARG_LAYOUT) : R.layout.sflash_1;
        View view = inflater.inflate(layoutResId, container, false);

        Button nextButton = view.findViewById(R.id.next_button);
        int position = getArguments() != null ? getArguments().getInt(ARG_POSITION) : 0;
        nextButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNextButtonClicked(position);
            }
        });

        return view;
    }
}