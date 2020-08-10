package com.example.taizhongoldtownguideapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class friendListRecycleViewAdapter extends RecyclerView.Adapter<friendListRecycleViewAdapter.friendListRecycleViewHolder> {

    private List<String> friendList = new ArrayList<>();
    private CollectionReference teamMemberRef;
    private final LayoutInflater mInflater;

    class friendListRecycleViewHolder extends RecyclerView.ViewHolder{
        public final TextView wordItemView;
        final friendListRecycleViewAdapter mAdapter;

        public friendListRecycleViewHolder(View itemView, friendListRecycleViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.location_context);
            this.mAdapter = adapter;


        }
    }

    public friendListRecycleViewAdapter(Context context, List<String> friendList, CollectionReference teamMemberRef) {
        mInflater = LayoutInflater.from(context);
        this.friendList = friendList;
        this.teamMemberRef = teamMemberRef;
    }

    @NonNull
    @Override
    public friendListRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.person_info_recycle_view_item,
                parent, false);
        return new friendListRecycleViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final friendListRecycleViewHolder holder, final int position) {
        teamMemberRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String mCurrent = task.getResult().getDocuments().get(position).get("userName").toString();
                            holder.wordItemView.setText(mCurrent);
                        } else {
                            Log.d("firebaseMember", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
