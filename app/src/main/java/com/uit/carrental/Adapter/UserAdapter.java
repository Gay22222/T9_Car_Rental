package com.uit.carrental.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final OnUserClickListener clickListener;
    private final OnDeleteClickListener deleteListener;

    public interface OnUserClickListener {
        void onUserClick(int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public UserAdapter(List<User> userList, OnUserClickListener clickListener, OnDeleteClickListener deleteListener) {
        this.userList = userList;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Bind data
        holder.textViewUsername.setText(user.getUsername() != null && !user.getUsername().isEmpty() ? user.getUsername() : "Unknown User");
        String verificationStatus = user.getVerificationStatus() != null ? user.getVerificationStatus() : "Not Set";
        String status = user.getStatus() != null ? user.getStatus() : "Not Set";
        holder.textViewDescription.setText(String.format("Role: %s | Verification: %s | Status: %s",
                user.getCurrentRole() != null ? user.getCurrentRole() : "Not Set", verificationStatus, status));

        // Load avatar
        Glide.with(holder.itemView.getContext())
                .load(user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty() ? user.getAvatarUrl() : R.drawable.ic_person)
                .placeholder(R.drawable.ic_person)
                .into(holder.imageViewAvatar);

        // Click listener for item
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUserClick(position);
            }
        });

        // Click listener for delete button
        holder.deleteButton.setOnClickListener(v -> {
            if (holder.itemView.getContext() != null) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có muốn xóa " + (user.getUsername() != null && !user.getUsername().isEmpty() ? user.getUsername() : "người dùng này") + "?")
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Xác nhận", (dialog, which) -> {
                            if (deleteListener != null) {
                                deleteListener.onDeleteClick(position);
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateData(List<User> newData) {
        this.userList.clear();
        if (newData != null) {
            this.userList.addAll(newData);
        }
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAvatar, deleteButton;
        TextView textViewUsername, textViewDescription;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAvatar = itemView.findViewById(R.id.user_avatar);
            textViewUsername = itemView.findViewById(R.id.user_name);
            textViewDescription = itemView.findViewById(R.id.user_description);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}
