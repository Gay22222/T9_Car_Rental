package com.uit.carrental.FragmentPages.Admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.uit.carrental.Adapter.UserAdapter;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminUserListFragment extends Fragment {

    private RecyclerView recyclerViewUsers;
    private TextView textViewUserCount, textViewUserStats;
    private LinearLayout buttonSearch, buttonPage1, buttonPage2, buttonPage3;
    private ImageView imageViewFilter, imageViewSearch, imageViewPrevPage, imageViewNextPage;
    private EditText editTextSearch;
    private UserAdapter userAdapter;
    private List<User> userList; // Danh sách tất cả người dùng từ Firestore
    private List<User> filteredUserList; // Danh sách sau khi lọc và tìm kiếm
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private int currentPage = 1;
    private int totalPages = 1;
    private static final int ITEMS_PER_PAGE = 5;
    private String currentQuery = "";
    private String currentVerificationStatus;
    private ListenerRegistration usersListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user_list, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        recyclerViewUsers = view.findViewById(R.id.user_list);
        textViewUserCount = view.findViewById(R.id.tv_user_count);
        textViewUserStats = view.findViewById(R.id.btn_user_stats);
        buttonSearch = view.findViewById(R.id.search_container);
        imageViewFilter = view.findViewById(R.id.filter_button);
        imageViewSearch = view.findViewById(R.id.search_button);
        editTextSearch = view.findViewById(R.id.search_input);
        buttonPage1 = view.findViewById(R.id.page_1_button);
        buttonPage2 = view.findViewById(R.id.page_2_button);
        buttonPage3 = view.findViewById(R.id.page_3_button);
        imageViewPrevPage = view.findViewById(R.id.prev_page_button);
        imageViewNextPage = view.findViewById(R.id.next_page_button);

        // Setup RecyclerView
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>();
        userAdapter = new UserAdapter(filteredUserList, position -> {
            UserStatsFragment fragment = new UserStatsFragment();
            Bundle args = new Bundle();
            args.putString("userId", filteredUserList.get(position).getUserId());
            fragment.setArguments(args);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout_admin, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }, position -> {
            User user = filteredUserList.get(position);
            if (user.getUserId() != null) {
                deleteUser(user.getUserId(), position);
            } else {
                Toast.makeText(getContext(), "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewUsers.setAdapter(userAdapter);

        // Check admin role
        if (auth.getCurrentUser() != null) {
            checkAdminRole(auth.getCurrentUser().getUid());
        } else {
            Toast.makeText(getContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }

        // Setup search
        setupSearch();

        // Setup filter
        setupFilter();

        // Setup pagination
        setupPagination();

        // Setup user stats button
        textViewUserStats.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserStatsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void checkAdminRole(String userId) {
        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user == null || !user.hasRole("admin")) {
                            Toast.makeText(getContext(), "Bạn không có quyền admin", Toast.LENGTH_SHORT).show();
                            requireActivity().finish();
                        } else {
                            loadUsersRealTime();
                        }
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        requireActivity().finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra vai trò: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                });
    }

    private void setupSearch() {
        buttonSearch.setOnClickListener(v -> updateFilteredList());
        imageViewSearch.setOnClickListener(v -> updateFilteredList());

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim().toLowerCase();
                currentPage = 1;
                updateFilteredList();
            }
        });
    }

    private void setupFilter() {
        imageViewFilter.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Lọc người dùng")
                    .setItems(new String[]{"Đã xác minh", "Chờ xác minh", "Tất cả"}, (dialog, which) -> {
                        currentVerificationStatus = switch (which) {
                            case 0 -> "verified";
                            case 1 -> "pending";
                            default -> null;
                        };
                        currentPage = 1;
                        updateFilteredList();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void setupPagination() {
        buttonPage1.setOnClickListener(v -> setPage(1));
        buttonPage2.setOnClickListener(v -> setPage(2));
        buttonPage3.setOnClickListener(v -> setPage(3));
        imageViewPrevPage.setOnClickListener(v -> setPage(currentPage - 1));
        imageViewNextPage.setOnClickListener(v -> setPage(currentPage + 1));
        updatePageButtons();
    }

    private void setPage(int page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        updateRecyclerView();
    }

    private void updatePageButtons() {
        buttonPage1.setBackgroundResource(currentPage == 1 ? R.drawable.cr15b003087 : R.drawable.s003087sw1cr15bffffff);
        buttonPage2.setBackgroundResource(currentPage == 2 ? R.drawable.cr15b003087 : R.drawable.s003087sw1cr15bffffff);
        buttonPage3.setBackgroundResource(currentPage == 3 ? R.drawable.cr15b003087 : R.drawable.s003087sw1cr15bffffff);
        imageViewPrevPage.setEnabled(currentPage > 1);
        imageViewNextPage.setEnabled(currentPage < totalPages);
        buttonPage1.setVisibility(totalPages >= 1 ? View.VISIBLE : View.GONE);
        buttonPage2.setVisibility(totalPages >= 2 ? View.VISIBLE : View.GONE);
        buttonPage3.setVisibility(totalPages >= 3 ? View.VISIBLE : View.GONE);
    }

    private void loadUsersRealTime() {
        // Lắng nghe thay đổi thời gian thực trên collection Users
        usersListener = db.collection("Users")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        loadMockData();
                        return;
                    }

                    if (querySnapshot != null) {
                        userList.clear();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            User user = document.toObject(User.class);
                            if (user != null && user.getUserId() != null) {
                                userList.add(user);
                            }
                        }
                        updateFilteredList();
                    }
                });
    }

    private void updateFilteredList() {
        filteredUserList.clear();

        // Lọc và tìm kiếm client-side
        for (User user : userList) {
            boolean matchesSearch = currentQuery.isEmpty() ||
                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(currentQuery)) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(currentQuery));
            boolean matchesFilter = currentVerificationStatus == null ||
                    (user.getVerificationStatus() != null && user.getVerificationStatus().equalsIgnoreCase(currentVerificationStatus));

            if (matchesSearch && matchesFilter) {
                filteredUserList.add(user);
            }
        }

        // Sắp xếp theo username client-side
        filteredUserList.sort((u1, u2) -> {
            String username1 = u1.getUsername() != null ? u1.getUsername().toLowerCase() : "";
            String username2 = u2.getUsername() != null ? u2.getUsername().toLowerCase() : "";
            return username1.compareTo(username2);
        });

        // Cập nhật tổng số trang
        totalPages = (int) Math.ceil((double) filteredUserList.size() / ITEMS_PER_PAGE);
        textViewUserCount.setText("Tổng số người dùng: " + filteredUserList.size());
        updatePageButtons();
        updateRecyclerView();
    }

    private void loadMockData() {
        userList.clear();
        filteredUserList.clear();
        String[] verificationStatuses = {"verified", "pending"};
        for (int i = 1; i <= 15; i++) {
            User mockUser = new User();
            mockUser.setUserId("mock_user_" + i);
            mockUser.setUsername("Người dùng " + i);
            mockUser.setEmail("user" + i + "@example.com");
            mockUser.setVerificationStatus(verificationStatuses[i % 2]);
            mockUser.setRoles(Map.of("customer", true));
            userList.add(mockUser);
        }
        updateFilteredList();
    }

    private void updateRecyclerView() {
        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredUserList.size());
        List<User> paginatedList = new ArrayList<>();
        if (start < filteredUserList.size()) {
            paginatedList.addAll(filteredUserList.subList(start, end));
        }
        userAdapter.updateData(paginatedList);
        recyclerViewUsers.scrollToPosition(0); // Cuộn lên đầu khi cập nhật
    }

    private void deleteUser(String userId, int position) {
        db.collection("Users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Không cần xóa thủ công vì listener sẽ tự động cập nhật userList
                    Toast.makeText(getContext(), "Đã xóa người dùng", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi xóa người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy listener để tránh rò rỉ bộ nhớ
        if (usersListener != null) {
            usersListener.remove();
        }
    }
}
