package com.chen.fy.mymail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.fy.mymail.R;
import com.chen.fy.mymail.beans.ContactPerson;
import com.chen.fy.mymail.interfaces.IJumpActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddressBookAdapter extends RecyclerView.Adapter<AddressBookAdapter.ViewHolder> {

    private List<ContactPerson> mLists;
    private Context mContext;

    private IJumpActivity mJumpActivity;

    //构造方法,并传入数据源
    public AddressBookAdapter(Context context, List<ContactPerson> list) {
        this.mContext = context;
        this.mLists = list;
    }

    public void setJumpActivity(IJumpActivity jumpActivity) {
        this.mJumpActivity = jumpActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //反射每行的子布局,并把view传入viewHolder中,以便获取控件对象
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.address_book_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        ContactPerson contactPerson = mLists.get(i);

        Glide.with(mContext).load(R.drawable.user_test).into(viewHolder.civHeadIcon);
        viewHolder.tvName.setText(contactPerson.getName());
        viewHolder.tvAddress.setText(contactPerson.getAddress());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mJumpActivity != null) {
                    mJumpActivity.jumpActivity(contactPerson.getName(),
                            contactPerson.getAddress());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    /**
     * 内部类,获取各控件的对象
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civHeadIcon;
        TextView tvName;
        TextView tvAddress;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            civHeadIcon = itemView.findViewById(R.id.civ_head_icon_address);
            tvName = itemView.findViewById(R.id.tv_name_address_book_item);
            tvAddress = itemView.findViewById(R.id.tv_address_item);
        }
    }

}

