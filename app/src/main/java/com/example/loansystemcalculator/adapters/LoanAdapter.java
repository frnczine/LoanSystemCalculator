package com.example.loansystemcalculator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loansystemcalculator.R;
import com.example.loansystemcalculator.models.LoanApplication;

import java.util.List;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder> {

    private Context context;
    private List<LoanApplication> loans;

    public LoanAdapter(List<LoanApplication> loans) {
        this.loans = loans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_loan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoanApplication loan = loans.get(position);

        holder.txtLoanType.setText(loan.getLoanType() + " Loan");
        holder.txtLoanAmount.setText(String.format("Amount: ₱%,.2f", loan.getLoanAmount()));
        holder.txtLoanTerm.setText("Term: " + loan.getMonthsToPay() + " months");
        holder.txtApplicationDate.setText("Applied: " + loan.getApplicationDate());
        holder.txtMonthlyPayment.setText(String.format("Monthly: ₱%,.2f", loan.getMonthlyAmortization()));

        // Set status with color coding
        holder.txtStatus.setText(loan.getStatus());
        switch (loan.getStatus().toLowerCase()) {
            case "pending":
                holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                holder.txtStatus.setBackgroundResource(R.drawable.rounded_status_pending);
                break;
            case "approved":
                holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                holder.txtStatus.setBackgroundResource(R.drawable.rounded_status_approved);
                break;
            case "disapproved":
                holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                holder.txtStatus.setBackgroundResource(R.drawable.rounded_status_disapproved);
                break;
            default:
                holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return loans != null ? loans.size() : 0;
    }

    public void setLoans(List<LoanApplication> loans) {
        this.loans = loans;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLoanType, txtLoanAmount, txtLoanTerm, txtApplicationDate, txtMonthlyPayment, txtStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLoanType = itemView.findViewById(R.id.txtLoanType);
            txtLoanAmount = itemView.findViewById(R.id.txtLoanAmount);
            txtLoanTerm = itemView.findViewById(R.id.txtLoanTerm);
            txtApplicationDate = itemView.findViewById(R.id.txtApplicationDate);
            txtMonthlyPayment = itemView.findViewById(R.id.txtMonthlyPayment);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}