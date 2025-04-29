package com.uit.carrental.FragmentPages.Customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.uit.carrental.ActivityPages.OwnerMainActivity;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.uit.carrental.Service.UserAuthentication.LoginActivity;
import com.uit.carrental.Service.UserAuthentication.UpdatePassword;
import com.uit.carrental.Service.UserAuthentication.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerSettingFragment extends Fragment {

    private TextView tvName;
    private CircleImageView imgAvatar;

    private FirebaseFirestore dtb_user;
    private FirebaseUser firebaseUser;
    private User user = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_setting,
                container, false);

        imgAvatar = view.findViewById(R.id.img_avatar);
        tvName = view.findViewById(R.id.tv_name);

        dtb_user = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user.setUser_id(firebaseUser.getUid());

        dtb_user.collection("Users")
                .whereEqualTo("user_id", user.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                tvName.setText(document.get("username").toString());
                                user.setAvatarURL(document.get("avatarURL").toString());
                                if (!document.get("avatarURL").toString().isEmpty()) {
                                    Picasso.get().load(user.getAvatarURL()).into(imgAvatar);
                                }
                                else {
                                    user.setAvatarURL("");
                                }
                            }
                        }
                        else {
                            //
                            Toast.makeText(view.getContext(), "Không thể lấy thông tin", Toast.LENGTH_LONG).show();
                        }
                    }
                });


        LinearLayout connect = view.findViewById(R.id.layout_connect);
        connect.setOnClickListener(v -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser == null) return;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(firebaseUser.getUid())
                    .update("currentRole", "owner")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Đã chuyển sang giao diện chủ xe!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), OwnerMainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Chuyển vai trò thất bại.", Toast.LENGTH_SHORT).show();
                    });
        });


        LinearLayout changePass = (LinearLayout) view.findViewById(R.id.layout_change_password);
        changePass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getActivity(), UpdatePassword.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });



        LinearLayout profile = (LinearLayout) view.findViewById(R.id.layout_information);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UserProfile.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        LinearLayout delete = (LinearLayout) view.findViewById(R.id.layout_delete_account);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(view.getContext(), "User account deleted.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        ((Activity) getActivity()).overridePendingTransition(0, 0);
                                    } else {
                                        Toast.makeText(view.getContext(), "Failed to delete user account.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        LinearLayout signOut = (LinearLayout) view.findViewById(R.id.layout_sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });
        return view;
        /*        return inflater.inflate(R.layout.customer_fragment_user, container, false);*/
    }

}