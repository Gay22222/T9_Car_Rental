package com.uit.carrental.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.uit.carrental.Model.Message;
import com.uit.carrental.R;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getContent());

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
                holder.messageContainer.getLayoutParams();
        if (message.isUser()) {
            params.startToEnd = ConstraintLayout.LayoutParams.UNSET;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            params.setMarginEnd(8);
            holder.avatar.setVisibility(View.GONE);
            holder.messageContainer.setBackgroundResource(R.drawable.message_bubble_background_user);
        } else {
            params.startToEnd = holder.avatar.getId();
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            params.setMarginStart(8);
            holder.avatar.setVisibility(View.VISIBLE);
            holder.messageContainer.setBackgroundResource(R.drawable.message_bubble_background);
        }
        holder.messageContainer.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        LinearLayout messageContainer;
        TextView messageText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            messageContainer = itemView.findViewById(R.id.message_container);
            messageText = itemView.findViewById(R.id.message_text);
        }
    }
}