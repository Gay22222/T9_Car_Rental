package com.uit.carrental.FragmentPages.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.uit.carrental.R;

public class RequestSupplementDialogFragment extends DialogFragment {

    private static final String ARG_RECIPIENT_ID = "recipient_id";
    private static final String ARG_VEHICLE_ID = "vehicle_id";
    private static final String ARG_TYPE = "type";

    private String recipientId;
    private String vehicleId;
    private String type;
    private OnSubmitListener onSubmitListener;

    public interface OnSubmitListener {
        void onSubmit(String message);
    }

    public static RequestSupplementDialogFragment newInstance(String recipientId, String vehicleId, String type) {
        RequestSupplementDialogFragment fragment = new RequestSupplementDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPIENT_ID, recipientId);
        args.putString(ARG_VEHICLE_ID, vehicleId);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipientId = getArguments().getString(ARG_RECIPIENT_ID);
            vehicleId = getArguments().getString(ARG_VEHICLE_ID);
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_request_supplement, container, false);

        TextView tvTitle = view.findViewById(R.id.rk1bxbo5ts2e);
        EditText etMessage = view.findViewById(R.id.ro1uxzv0nk4);
        LinearLayout btnSubmit = view.findViewById(R.id.r7nlv9onk9j3);
        ImageView btnClose = view.findViewById(R.id.close_cicle);

        tvTitle.setText(type.equals("violation_warning") ? "Cảnh báo vi phạm" : "Yêu cầu bổ sung");
        etMessage.setHint(type.equals("violation_warning") ? "Nhập lý do vi phạm..." : "Nhập yêu cầu bổ sung...");

        btnSubmit.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty() && onSubmitListener != null) {
                onSubmitListener.onSubmit(message);
                dismiss();
            }
        });

        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    public void setOnSubmitListener(OnSubmitListener listener) {
        this.onSubmitListener = listener;
    }
}