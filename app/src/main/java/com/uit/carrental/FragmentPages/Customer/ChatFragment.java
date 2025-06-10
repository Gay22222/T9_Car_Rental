package com.uit.carrental.FragmentPages.Customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uit.carrental.Adapter.MessageAdapter;
import com.uit.carrental.Model.Message;
import com.uit.carrental.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageView sendButton;
    private ImageView closeButton;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private LinearLayout draggableChatContainer;
    private float dX, dY;
    private int lastAction;
    private OkHttpClient client;
    private FirebaseFirestore db;
    private List<String> vehicleNames;

    private static final String HUGGINGFACE_API_KEY = "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";
    private static final String API_URL = "https://api-inference.huggingface.co/models/meta-llama/Llama-3.1-8B-Instruct";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize views
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);
        closeButton = view.findViewById(R.id.close_button);
        draggableChatContainer = view.findViewById(R.id.draggable_chat_container);

        // Initialize OkHttpClient and Firestore
        client = new OkHttpClient();
        db = FirebaseFirestore.getInstance();
        vehicleNames = new ArrayList<>();

        // Load vehicle names from Firestore
        loadVehicleNames();

        // Setup RecyclerView
        messages = new ArrayList<>();
        messages.add(new Message("Chào bạn! T9 Car Rental luôn sẵn sàng hỗ trợ.", false));
        messageAdapter = new MessageAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(messageAdapter);

        // Send button click
        sendButton.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                messages.add(new Message(text, true));
                messageAdapter.notifyItemInserted(messages.size() - 1);
                chatRecyclerView.scrollToPosition(messages.size() - 1);
                messageInput.setText("");
                sendMessageToHuggingFace(text);
            }
        });

        // Close button click
        closeButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Quay lại CustomerHomeFragment
        });

        // Draggable container
        draggableChatContainer.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    lastAction = MotionEvent.ACTION_DOWN;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    v.setY(event.getRawY() + dY);
                    v.setX(event.getRawX() + dX);
                    lastAction = MotionEvent.ACTION_MOVE;
                    return true;
                case MotionEvent.ACTION_UP:
                    if (lastAction == MotionEvent.ACTION_DOWN) {
                    }
                    return true;
                default:
                    return false;
            }
        });

        return view;
    }

    private void loadVehicleNames() {
        db.collection("Vehicles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vehicleNames.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String vehicleName = document.getString("vehicle_name");
                            if (vehicleName != null) {
                                vehicleNames.add(vehicleName);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi tải danh sách xe", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendMessageToHuggingFace(String userMessage) {
        try {
            // Định dạng danh sách xe
            String vehicleList = vehicleNames.isEmpty() ? "Không có xe nào hiện tại." : vehicleNames.stream().collect(Collectors.joining(", "));
            // Prompt chuẩn cho Llama-3.1
            String prompt = "<|begin_of_text|>Bạn là nhân viên tư vấn thuê xe trực tuyến của T9 Car Rental. Dựa trên yêu cầu của khách hàng và danh sách xe hiện có (" + vehicleList + "), hãy đề xuất loại xe phù hợp nhất và giải thích ngắn gọn lý do. Trả lời thân thiện, chuyên nghiệp, tối đa 100 từ:\n" + userMessage + "<|end_of_text|>";

            JSONObject requestBody = new JSONObject();
            requestBody.put("inputs", prompt);
            requestBody.put("parameters", new JSONObject()
                    .put("max_new_tokens", 100)
                    .put("temperature", 0.7)
                    .put("top_p", 0.9));

            RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + HUGGINGFACE_API_KEY)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        messages.add(new Message("Lỗi kết nối, vui lòng thử lại.", false));
                        messageAdapter.notifyItemInserted(messages.size() - 1);
                        chatRecyclerView.scrollToPosition(messages.size() - 1);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        String rawResponse = jsonArray.getJSONObject(0).getString("generated_text");
                        final String aiResponse = rawResponse.replace(prompt, "").trim();

                        getActivity().runOnUiThread(() -> {
                            messages.add(new Message(aiResponse, false));
                            messageAdapter.notifyItemInserted(messages.size() - 1);
                            chatRecyclerView.scrollToPosition(messages.size() - 1);
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Lỗi phân tích phản hồi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi tạo request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}