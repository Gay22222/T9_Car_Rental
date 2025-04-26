//package com.example.carrenting.FragmentPages.Owner;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.carrenting.ActivityPages.CustomerMainActivity;
//import com.example.carrenting.ActivityPages.OwnerMainActivity;
//import com.example.carrenting.Model.User;
//import com.example.carrenting.R;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.squareup.picasso.Picasso;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class OwnerSettingFragment extends Fragment {
//
//    private TextView tvName;
//    private CircleImageView imgAvatar;
//
//    private FirebaseFirestore dtb_user;
//    private FirebaseUser firebaseUser;
//    private User user = new User();
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.owner_fragment_setting,
//                container, false);
//
//        imgAvatar = view.findViewById(R.id.img_avatar);
//        tvName = view.findViewById(R.id.tv_name);
//
//        dtb_user = FirebaseFirestore.getInstance();
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        user.setUser_id(firebaseUser.getUid());
//
//        dtb_user.collection("Users")
//                .whereEqualTo("user_id", user.getUser_id())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            for (QueryDocumentSnapshot document : task.getResult()){
//                                tvName.setText(document.get("username").toString());
//                                user.setAvatarURL(document.get("avatarURL").toString());
//                                if (!document.get("avatarURL").toString().isEmpty()) {
//                                    Picasso.get().load(user.getAvatarURL()).into(imgAvatar);
//                                }
//                                else {
//                                    user.setAvatarURL("");
//                                }
//                            }
//                        }
//                        else {
//                            //
//                            Toast.makeText(view.getContext(), "Không thể lấy thông tin", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//
//        LinearLayout connect = (LinearLayout) view.findViewById(R.id.layout_connect);
//        connect.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent i = new Intent(getActivity(), CustomerMainActivity.class);
//                startActivity(i);
//                ((Activity) getActivity()).overridePendingTransition(0, 0);
//            }
//        });
//
//        return view;
//    }
//}

package com.example.carrenting.FragmentPages.Owner;

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

import com.example.carrenting.ActivityPages.CustomerMainActivity;
import com.example.carrenting.R;
import com.example.carrenting.Service.UserAuthentication.UpdatePassword;
import com.example.carrenting.Service.UserAuthentication.UserProfile;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class OwnerSettingFragment extends Fragment {

    private TextView tvName;
    private CircleImageView imgAvatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.owner_fragment_setting, container, false);

        tvName = view.findViewById(R.id.tv_name);
        imgAvatar = view.findViewById(R.id.img_avatar);

        // 🎭 Fake user
        tvName.setText("Nguyễn Việt Khôi");
        Picasso.get().load("data:image/jpeg;base64,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")
                .placeholder(R.drawable.ic_person)
                .into(imgAvatar);

        // 👉 Thông tin tài khoản
        view.findViewById(R.id.layout_information).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UserProfile.class));
            ((Activity) getActivity()).overridePendingTransition(0, 0);
        });

        // 👉 Chuyển sang giao diện khách
        view.findViewById(R.id.layout_connect).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CustomerMainActivity.class));
            ((Activity) getActivity()).overridePendingTransition(0, 0);
        });

        // 👉 Đổi mật khẩu
        view.findViewById(R.id.layout_change_password).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UpdatePassword.class));
            ((Activity) getActivity()).overridePendingTransition(0, 0);
        });

        // 👉 Xoá tài khoản (fake)
        view.findViewById(R.id.layout_delete_account).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Demo: Tính năng này bị khóa", Toast.LENGTH_SHORT).show();
        });

        // 👉 Đăng xuất (fake)
        view.findViewById(R.id.layout_sign_out).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Demo: Đã đăng xuất khỏi tài khoản nhà cung cấp", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), CustomerMainActivity.class));
            ((Activity) getActivity()).overridePendingTransition(0, 0);
        });

        return view;
    }
}
