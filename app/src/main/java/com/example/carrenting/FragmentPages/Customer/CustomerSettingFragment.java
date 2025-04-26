package com.example.carrenting.FragmentPages.Customer;
import com.example.carrenting.Service.UserAuthentication.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.carrenting.ActivityPages.OwnerMainActivity;
import com.example.carrenting.R;
import com.example.carrenting.Service.UserAuthentication.UpdatePassword;
import com.example.carrenting.Service.UserAuthentication.UserProfile;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerSettingFragment extends Fragment {

    private TextView tvName;
    private CircleImageView imgAvatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_setting, container, false);

        tvName = view.findViewById(R.id.tv_name);
        imgAvatar = view.findViewById(R.id.img_avatar);

        // 👤 Hiển thị user fake
        tvName.setText("Trương Đức Quang Khoa");
        Picasso.get().load("https://techie.vn/wp-content/uploads/2023/01/Doge-meme-2-1024x614-1.webp")
                .placeholder(R.drawable.ic_person)
                .into(imgAvatar);

        // Thông tin tài khoản
        LinearLayout profile = view.findViewById(R.id.layout_information);
        profile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UserProfile.class));
            ((Activity) getActivity()).overridePendingTransition(0, 0);
        });

        // Chuyển sang chủ xe
        LinearLayout connect = view.findViewById(R.id.layout_connect);
        connect.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OwnerMainActivity.class);
            intent.putExtra("role", "owner"); // <-- Fake role
            startActivity(intent);
            ((Activity) getActivity()).overridePendingTransition(0, 0);
        });

        // Đổi mật khẩu
        LinearLayout changePass = view.findViewById(R.id.layout_change_password);
        changePass.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UpdatePassword.class));
            ((Activity) getActivity()).overridePendingTransition(0, 0);
        });

        // Xoá tài khoản (fake)
        LinearLayout delete = view.findViewById(R.id.layout_delete_account);
        delete.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Demo: Tính năng này bị khóa", Toast.LENGTH_SHORT).show();
        });

        // Đăng xuất (fake)
        LinearLayout signOut = view.findViewById(R.id.layout_sign_out);
        signOut.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear backstack
            startActivity(intent);
        });

        return view;
    }
}
