package com.typartybuilding.activity.second.homepager;

import android.text.format.DateFormat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseListAct;
import com.typartybuilding.bean.pblibrary.SysMessageBean;
import com.typartybuilding.loader.HomePagerLoader;
import com.typartybuilding.network.https.RequestCallback;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-17
 * @describe
 */
@Route(path = SysMessageAct.PATH)
public class SysMessageAct extends BaseListAct {
    public static final String PATH = "/act/sysmsg";
    SysMsgAdapter sysMsgAdapter;
    HomePagerLoader homePagerLoader;

    @Override
    public void initData() {
        super.initData();
        mTitleTv.setText("系统消息");
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setBackground(getDrawable(R.color.c_F7F7F7));

        homePagerLoader = new HomePagerLoader();
        sysMsgAdapter = new SysMsgAdapter();

        recyclerView.setAdapter(sysMsgAdapter);
        homePagerLoader.getSysMessage().subscribe(new RequestCallback<SysMessageBean>() {
            @Override
            public void onSuccess(SysMessageBean sysMessageBean) {
                List<SysMessageBean.RowsBean> rowsBean = sysMessageBean.getRows();
                sysMsgAdapter.setNewData(rowsBean);
            }

            @Override
            public void onFail(Throwable e) {

            }
        });
    }


    public class SysMsgAdapter extends BaseMultiItemQuickAdapter<SysMessageBean.RowsBean, BaseViewHolder> {

        public SysMsgAdapter() {
            super(null);
            addItemType(0, R.layout.layout_item_sys_msg_point);
        }

        @Override
        protected void convert(BaseViewHolder helper, SysMessageBean.RowsBean item) {
            switch (item.getItemType()) {
                case 0:
                    helper.setText(R.id.sys_msg_title,item.getMessageTitle());
                    helper.setText(R.id.sys_msg_content,item.getMessageContent());
                    helper.setText(R.id.sys_msg_time, DateFormat.format("MM/dd",item.getSendTime()));
                    break;
            }
        }
    }

}
