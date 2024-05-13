package com.example.entriviados10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final Context context;
    private final List<com.example.entriviados10.User> userList;
    private final String registeredUserName;

    public UserAdapter(List<com.example.entriviados10.User> userList, Context context, String registeredUserName) {
        this.userList = userList;
        this.context = context;
        this.registeredUserName = registeredUserName;
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

        holder.order.setText(String.valueOf(position + 1));
        Glide.with(context)
                .load(user.getImageURL())
                .into(holder.userPicture);

        holder.userName.setText(user.getName());
        holder.userScore.setText(String.valueOf(user.getScore()));
        if (user.getName().equals(registeredUserName)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userPicture;
        TextView userName, userScore, order;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            order = itemView.findViewById(R.id.rankingOrder);
            userPicture = itemView.findViewById(R.id.userPicture);
            userName = itemView.findViewById(R.id.userName);
            userScore = itemView.findViewById(R.id.userScore);
        }
    }
}
