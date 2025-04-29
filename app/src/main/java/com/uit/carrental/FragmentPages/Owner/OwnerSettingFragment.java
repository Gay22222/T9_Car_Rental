package com.uit.carrental.FragmentPages.Owner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.uit.carrental.ActivityPages.CustomerMainActivity;
import com.uit.carrental.Service.UserAuthentication.ProfileManagement;
import com.uit.carrental.R;
import com.uit.carrental.Service.UserAuthentication.ForgotPasswordActivity;
import com.uit.carrental.Service.UserAuthentication.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;


public class OwnerSettingFragment extends Fragment {

    private TextView tvName;
    private CircleImageView imgAvatar;
    private FirebaseFirestore dtb_user;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.owner_fragment_setting, container, false);

        imgAvatar = view.findViewById(R.id.img_avatar);
        tvName = view.findViewById(R.id.tv_name);

        dtb_user = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        if (firebaseUser != null) {
            dtb_user.collection("Users")
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String avatarURL = documentSnapshot.getString("avatarURL");

                            if (username != null) {
                                tvName.setText(username);
                            }
                            if (avatarURL != null && !avatarURL.isEmpty()) {
                                Picasso.get().load(avatarURL).into(imgAvatar);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(view.getContext(), "Không thể lấy thông tin.", Toast.LENGTH_SHORT).show();
                    });
        }

        // Sự kiện các nút
        LinearLayout profile = view.findViewById(R.id.layout_information);
        LinearLayout password = view.findViewById(R.id.layout_change_password);
        LinearLayout deleteAccount = view.findViewById(R.id.layout_delete_account);
        LinearLayout logout = view.findViewById(R.id.layout_sign_out);
        LinearLayout connect = view.findViewById(R.id.layout_connect);


        profile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileManagement.class));
            ((Activity) getActivity()).overridePendingTransition(0, 0);
        });

        password.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
            ((Activity) getActivity()).overridePendingTransition(0, 0);
        });

        deleteAccount.setOnClickListener(v -> {
            if (firebaseUser == null) return;

            dtb_user.collection("Users").document(firebaseUser.getUid())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        firebaseUser.delete().addOnCompleteListener(task -> {
                            Toast.makeText(getContext(), "Đã xóa tài khoản!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            requireActivity().finish();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Xóa tài khoản thất bại.", Toast.LENGTH_SHORT).show();
                    });
        });

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        connect.setOnClickListener(v -> {
            if (firebaseUser == null) return;

            dtb_user.collection("Users")
                    .document(firebaseUser.getUid())
                    .update("currentRole", "customer")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Đã chuyển sang giao diện khách hàng!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), CustomerMainActivity.class));
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Chuyển vai trò thất bại.", Toast.LENGTH_SHORT).show();
                    });
        });

        return view;
    }
}
