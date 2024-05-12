package com.example.entriviados10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<com.example.entriviados10.User> userList;

    public UserAdapter(List<com.example.entriviados10.User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_ranking, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        Glide.with(context).load(user.getImageURL()).into(holder.userPicture);
        holder.userName.setText(user.getName());
        holder.userScore.setText(String.valueOf(user.getScore()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userPicture;
        TextView userName, userScore;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userPicture = itemView.findViewById(R.id.userPicture);
            userName = itemView.findViewById(R.id.userName);
            userScore = itemView.findViewById(R.id.userScore);
        }
    }
}

