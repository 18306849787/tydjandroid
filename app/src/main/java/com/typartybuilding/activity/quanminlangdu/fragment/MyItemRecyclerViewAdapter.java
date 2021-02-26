package com.typartybuilding.activity.quanminlangdu.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.quanminlangdu.fragment.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List<DummyContent.DummyItem> mValues;
    private ItemFragment.OnListFragmentInteractionListener mListener;
    private Context context;
    private int mType;

    public MyItemRecyclerViewAdapter(List<DummyContent.DummyItem> items, int type,ItemFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mType = type;
    }

    public void setNewData(List<DummyContent.DummyItem> items) {
        if (mValues == null) {
            mValues = new ArrayList<>();
        }
        mValues.clear();
        if (items!=null){
            mValues.addAll(items);
        }
    }

    public void setData(List<DummyContent.DummyItem> items){
        this.mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.readTitle.setText(mValues.get(position).readTitle);

        if (mValues.get(position).readAuthor == null || mValues.get(position).readAuthor.equals("")){
            if (mValues.get(position).readNumber==null || mValues.get(position).readNumber.equals("") || mValues.get(position).readNumber.equals("null")){
                //holder.readAuthor.setText((mValues.get(position).readAuthor+" | "+mValues.get(position).readNumber+"字"));
                holder.readAuthor.setText("");
            }else {
                holder.readAuthor.setText((mValues.get(position).readNumber+"字"));
            }
        }else {
            if (mValues.get(position).readNumber==null || mValues.get(position).readNumber.equals("") || mValues.get(position).readNumber.equals("null")){
                holder.readAuthor.setText((mValues.get(position).readAuthor));
            }else {
                holder.readAuthor.setText((mValues.get(position).readAuthor+" | "+mValues.get(position).readNumber+"字"));
            }
        }

        //holder.readAuthor.setText((mValues.get(position).readAuthor+" | "+mValues.get(position).readNumber+"字"));
        holder.readFrequency.setText(mValues.get(position).readFrequency);


        if (mValues.get(position).readProfile == null || mValues.get(position).readProfile.equals("")){
            holder.linearLayout.setVisibility(View.GONE);
            holder.mSanjiao.setVisibility(View.GONE);
        }else {
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.mSanjiao.setVisibility(View.VISIBLE);
            holder.readProfile.setText(mValues.get(position).readProfile);
        }

        Glide.with(context).load(mValues.get(position).readCover).into(holder.bookImage);
        holder.btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onListFragmentInteraction(holder.mItem,mType);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView readTitle;
        public TextView readAuthor;
        public TextView readFrequency;
        public ImageView bookImage;
        public ImageButton btnRead;
        public TextView readProfile;
        private LinearLayout linearLayout;
        public DummyContent.DummyItem mItem;
        public ImageView mSanjiao;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            readTitle = (TextView) view.findViewById(R.id.readTitle);
            readAuthor = (TextView) view.findViewById(R.id.readAuthor);
            readFrequency = (TextView) view.findViewById(R.id.readFrequency);
            bookImage = (ImageView) view.findViewById(R.id.book_image);
            btnRead = (ImageButton) view.findViewById(R.id.btn_read);
            readProfile = (TextView) view.findViewById(R.id.readProfile);
            linearLayout = view.findViewById(R.id.profile_parent);
            mSanjiao = view.findViewById(R.id.profile_parent_sanjiao);
        }
    }
}
