package com.example.loansystemcalculator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loansystemcalculator.AdminLoan;
import com.example.loansystemcalculator.R;

import java.util.ArrayList;

public class AdminLoanAdapter extends RecyclerView.Adapter<AdminLoanAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AdminLoan> list;
    private OnLoanActionListener listener;

    // Listener interface for approve/reject actions
    public interface OnLoanActionListener {
        void onApprove(AdminLoan loan);
        void onReject(AdminLoan loan);
    }

    public AdminLoanAdapter(Context context, ArrayList<AdminLoan> list, OnLoanActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminLoan loan = list.get(position);

        holder.txtDateRequested.setText(String.valueOf(loan.applicationDate));
        holder.txtApplicationID.setText(String.valueOf(loan.loanId));
        holder.txtEmployeeID.setText(String.valueOf(loan.employeeId));
        holder.txtFullName.setText(loan.fullName);
        holder.txtEmail.setText(String.valueOf(loan.clientEmail));
        holder.txtBasicSalary.setText("₱" + loan.basicSalary);
        holder.txtTermYears.setText(loan.termYears + " years");
        holder.txtLoanType.setText(String.valueOf(loan.loanType));
        holder.txtLoanAmount.setText("₱" + loan.loanAmount);
        holder.txtStatus.setText(String.valueOf(loan.status));

        if (loan.status.equalsIgnoreCase("Pending")) {
            holder.layoutActions.setVisibility(View.VISIBLE);

            holder.btnApprove.setEnabled(true);
            holder.btnReject.setEnabled(true);

            holder.btnApprove.setOnClickListener(v -> {
                listener.onApprove(loan);
                holder.btnApprove.setEnabled(false);
                holder.btnReject.setEnabled(false);
            });

            holder.btnReject.setOnClickListener(v -> {
                listener.onReject(loan);
                holder.btnApprove.setEnabled(false);
                holder.btnReject.setEnabled(false);
            });

        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDateRequested, txtApplicationID, txtEmployeeID, txtFullName, txtEmail, txtBasicSalary, txtTermYears, txtLoanType, txtLoanAmount, txtStatus;

        LinearLayout layoutActions;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateRequested = itemView.findViewById(R.id.txtDateRequested);
            txtApplicationID = itemView.findViewById(R.id.txtApplicationID);
            txtEmployeeID = itemView.findViewById(R.id.txtEmployeeID);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtBasicSalary = itemView.findViewById(R.id.txtBasicSalary);
            txtTermYears = itemView.findViewById(R.id.txtTermYears);
            txtLoanType = itemView.findViewById(R.id.txtLoanType);
            txtLoanAmount = itemView.findViewById(R.id.txtLoanAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}

