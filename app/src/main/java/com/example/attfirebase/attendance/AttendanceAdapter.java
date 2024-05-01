package com.example.attfirebase.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attfirebase.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AttendanceAdapter extends FirestoreRecyclerAdapter<Student, AttendanceAdapter.StudentHolder> {
    //Listeners to handle short and long clicks on each RecyclerView item
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;
    private boolean listening = false;


    public AttendanceAdapter(@NonNull FirestoreRecyclerOptions<Student> options) {
        super(options);
    }

    //Sets each TextView in each item to the student's ID, retrieved from the Student class
    @Override
    protected void onBindViewHolder(@NonNull StudentHolder holder, int position, @NonNull Student model) {

        holder.studentTV.setText(model.getStudent_id());
    }

    //Inflates layout for each item
    @NonNull
    @Override
    public StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_rv_layout,
                parent, false);

        return new StudentHolder(v);
    }

    public void setStudents(List<String> students) {
    }

    public void updateData(List<Student> attendedStudentIds) {
    }
    

    class StudentHolder extends RecyclerView.ViewHolder {


        TextView studentTV;

        public StudentHolder(@NonNull View itemView) {
            super(itemView);
            studentTV = itemView.findViewById(R.id.studentTV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int longPosition = getAdapterPosition();

                    if(longPosition != RecyclerView.NO_POSITION && longListener != null) {
                        longListener.onItemLongClick(getSnapshots().getSnapshot(longPosition), longPosition);
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;

    }

    public interface OnItemLongClickListener {
        void onItemLongClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longListener) {
        this.longListener = longListener;
    }

    public void startListening() {
        if (!listening) {
            super.startListening();
            listening = true;
        }
    }

    public void stopListening() {
        if (listening) {
            super.stopListening();
            listening = false;
        }
    }


}

