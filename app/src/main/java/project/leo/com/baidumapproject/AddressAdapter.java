package project.leo.com.baidumapproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context mContext;
    private List<PoiInfo> datas;

    public AddressAdapter(Context mContext, List<PoiInfo> list) {
        this.mContext = mContext;
        this.datas = list;
    }

    public void setDatas(List<PoiInfo> datas) {
        if (datas == null) {
            return;
        }

        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_list_poi, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final PoiInfo bean = datas.get(position);
        holder.mTvName.setText(bean.name);
        holder.mTvDetail.setText(bean.address);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.itemListener(position, bean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private ItemClick listener;

    public void setItemListener(ItemClick listener) {
        this.listener = listener;
    }

    interface ItemClick {
        void itemListener(int position, PoiInfo data);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvIcon;
        private TextView mTvName;
        private TextView mTvDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvDetail = itemView.findViewById(R.id.tv_detail);
        }
    }
}
