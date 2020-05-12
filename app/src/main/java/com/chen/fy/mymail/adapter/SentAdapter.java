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
import com.chen.fy.mymail.beans.DraftItem;
import com.chen.fy.mymail.beans.SentItem;
import com.chen.fy.mymail.interfaces.IItemClickListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SentAdapter extends RecyclerView.Adapter<SentAdapter.ViewHolder> {

    private List<SentItem> list;
    private Context context;

    private IItemClickListener mClickListener;

    //构造方法,并传入数据源
    public SentAdapter(Context context, List<SentItem> list) {
        this.context = context;
        this.list = list;
    }

    public void setClickListener(IItemClickListener clickListener) {
        this.mClickListener = clickListener;
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
        SentItem sentItem = list.get(i);

        Glide.with(context).load(R.drawable.user_test).into(viewHolder.civHeadIcon);
        viewHolder.tvName.setText(sentItem.getRecipientAddress());
        viewHolder.tvSubject.setText(sentItem.getSubject());
        viewHolder.tvContent.setText(sentItem.getContent());

        String[] strings = sentItem.getCreatedAt().split(" ");
        viewHolder.tvDate.setText(strings[0]);
        viewHolder.tvTime.setText(strings[1].substring(0, strings[1].length() - 3));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClickItem(
                            sentItem.getSubject()
                            , sentItem.getRecipientAddress()
                            , strings[0]+" "+strings[1].substring(0, strings[1].length() - 3)
                            , sentItem.getContent()
                    );
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onLongClickItem(sentItem.getRecipientAddress());
                return true;
            }
        });
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

