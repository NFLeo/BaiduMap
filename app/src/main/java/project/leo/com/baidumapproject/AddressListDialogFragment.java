package project.leo.com.baidumapproject;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class AddressListDialogFragment extends BottomSheetDialogFragment {

    private LinearLayout mLlSearchMusic;
    private RecyclerView recyclerView;
    private View contentView;

    /*data*/
    private AddressAdapter adapter;
    private List<String> categoryData = new ArrayList<>();

    public static AddressListDialogFragment newInstance() {
        return new AddressListDialogFragment();
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {

        if (contentView == null) {
            contentView = inflater.inflate(R.layout.dialog_fragment_address, container, false);
            findView();
            initView();
        } else {
            if (contentView.getParent() != null) {
                ViewGroup parent = (ViewGroup) contentView.getParent();
                parent.removeView(contentView);
            }
        }

        return contentView;
    }

    /**
     * 初始化设置高度
     */
    private void initHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //窗口高度
        int screenHeight = dm.heightPixels;
        setDefaultHeight(screenHeight / 2);
    }

    private void findView() {
        mLlSearchMusic = contentView.findViewById(R.id.ll_serach_music);
        recyclerView = contentView.findViewById(R.id.recyclerview);
    }

    private void initView() {

        initHeight();
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");
        categoryData.add("ASDSD");

        adapter = new AddressAdapter(mActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(adapter);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        adapter.setDatas(categoryData);
    }

    @Override
    protected boolean backAction() {
        //不处理，直接隐藏掉
        dismiss();
        return super.backAction();
    }
}