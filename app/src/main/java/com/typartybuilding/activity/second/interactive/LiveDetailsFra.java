package com.typartybuilding.activity.second.interactive;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.loader.InteractiveLoader;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-20
 * @describe
 */
public class LiveDetailsFra extends BaseFra {

    private InteractiveLoader interactiveLoader;
    @Override
    public void initData() {
        interactiveLoader = new InteractiveLoader();

        interactiveLoader.getLiveDetail(1);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_fra_live_room;
    }
}
