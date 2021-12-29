package com.example.login.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.AESUtils;
import com.example.login.R;
import com.example.login.activities.CheckMasterKeyActivity;
import com.example.login.manager.DBEngine;
import com.example.login.manager.DBEngineLogin;

import java.util.ArrayList;
/**
 * This class is a adapter class for the RecyclerView that used in maintaining passwords
 * The entire class is fully developed by Chi Zhang
 */
public class PasswordManagerAdapter extends RecyclerView.Adapter<PasswordManagerAdapter.ViewHolder> {

    // declaration
    private final Context context;
    private final ArrayList<String> passwordId;
    private final ArrayList<String> domain;
    private final ArrayList<String> account;
    private final ArrayList<String> password;
    //constructor
    public PasswordManagerAdapter(Context context,ArrayList passwordId,
                           ArrayList domain,
                           ArrayList account,
                           ArrayList password){
        this.passwordId=passwordId;
        this.context=context;
        this.domain =domain;
        this.account= account;
        this.password=password;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //set the view which have plenty of the my_row on it
        View view =inflater.inflate(R.layout.my_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordManagerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // get data from holder and set the view
        holder.domain_text.setText(domain.get(position));
        holder.account_text.setText(account.get(position));
        holder.password_text.setText(password.get(position));
        // my_row is the one block on the recycle view and there are many of these block
        holder.my_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CheckMasterKeyActivity.class);
                //click and it will carry some info to next activities
                intent.putExtra("passwordId",passwordId.get(position));
                intent.putExtra("domain",domain.get(position));
                intent.putExtra("account",account.get(position));
                try {
                    intent.putExtra("password", AESUtils.decrypt(password.get(position)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return domain.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView domain_text, account_text,password_text;
        LinearLayout my_row;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // get all element from the UI
            domain_text=itemView.findViewById(R.id.domain_row);
            account_text=itemView.findViewById(R.id.account_row);
            password_text=itemView.findViewById(R.id.password_row);
            my_row= itemView.findViewById(R.id.my_row);
        }
    }
}
