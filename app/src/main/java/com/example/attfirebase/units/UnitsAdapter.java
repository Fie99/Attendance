package com.example.attfirebase.units;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attfirebase.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import java.util.List;

public class UnitsAdapter extends FirestoreRecyclerAdapter<StudentUnitsItem, UnitsAdapter.UnitsHolder>{

    private OnItemClickListener listener;

    public UnitsAdapter(@NonNull FirestoreRecyclerOptions<StudentUnitsItem> options) {
        super(options);
    }

    protected void onBindViewHolder(@NonNull UnitsHolder holder, int position, @NonNull StudentUnitsItem model) {
        holder.name.setText(model.getUnit());
        holder.lecturer_name.setText(model.getUnit_lecturer());
        holder.date.setText(model.getUnit_date());
    }
    public String getUnitId(int position) {
        return getSnapshots().getSnapshot(position).getId();
    }

    @NonNull
    public UnitsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.units_rv_layout, parent, false);
        return new UnitsHolder(v);
    }

    class UnitsHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lecturer_name;
        TextView date;

        public UnitsHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.moduleName);
            lecturer_name = itemView.findViewById(R.id.module_lecturer_name);
            date = itemView.findViewById(R.id.lecture_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(UnitsAdapter.OnItemClickListener listener) {

        this.listener = listener;

    }
    // Method to set unitsList
    public void setUnitsList(List<StudentUnitsItem> unitsList) {
        notifyDataSetChanged();
    }
}
