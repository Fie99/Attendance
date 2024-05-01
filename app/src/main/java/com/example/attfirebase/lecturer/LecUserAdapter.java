package com.example.attfirebase.lecturer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attfirebase.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class LecUserAdapter extends FirestoreRecyclerAdapter<LecUser, LecUserAdapter.LecHolder> {

    public LecUserAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }


    @NonNull
    @Override
    public LecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lec_user_rv_layout, parent, false);
        return new LecHolder(v);

    }

    @Override
    protected void onBindViewHolder(@NonNull LecHolder holder, int position, @NonNull LecUser model) {

        holder.teacherID.setText(model.getTeacher_id());
        holder.teacherName.setText(model.getName());

    }

    class LecHolder extends RecyclerView.ViewHolder {

        TextView teacherID;
        TextView teacherName;

        public LecHolder(@NonNull View itemView) {
            super(itemView);

            teacherID = itemView.findViewById(R.id.teacherIDText);
            teacherName = itemView.findViewById(R.id.teacherNameText);
        }


    }
}
