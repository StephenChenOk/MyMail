package com.chen.fy.mymail.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.fy.mymail.R;
import com.chen.fy.mymail.beans.InboxItem;
import com.chen.fy.mymail.utils.DateUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private List<InboxItem> list;
    private Context context;

    //构造方法,并传入数据源
    public InboxAdapter(Context context, List<InboxItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //反射每行的子布局,并把view传入viewHolder中,以便获取控件对象
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.inbox_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        InboxItem inboxItem = list.get(i);

        Glide.with(context).load(inboxItem.getHeadIcon()).into(viewHolder.civHeadIcon);
        viewHolder.tvName.setText(inboxItem.getName());
        viewHolder.tvSubject.setText(inboxItem.getSubject());
        viewHolder.tvContent.setText(inboxItem.getContent());

        viewHolder.tvDate.setText(DateUtils.dateToDateString(inboxItem.getDate()));
        viewHolder.tvTime.setText(DateUtils.dateToTimeString(inboxItem.getDate()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 内部类,获取各控件的对象
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civHeadIcon;
        TextView tvName;
        TextView tvSubject;
        TextView tvContent;
        TextView tvDate;
        TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            civHeadIcon = itemView.findViewById(R.id.civ_head_icon);
            tvName = itemView.findViewById(R.id.tv_name_inbox_item);
            tvSubject = itemView.findViewById(R.id.tv_subject_inbox_item);
            tvContent = itemView.findViewById(R.id.tv_content_inbox_item);
            tvDate = itemView.findViewById(R.id.tv_date_inbox_item);
            tvTime = itemView.findViewById(R.id.tv_time_inbox_item);
        }
    }

}

