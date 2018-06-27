package project.leo.com.baidumapproject;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;


public class AddressListDialogFragment extends BottomSheetDialogFragment {

    public static final String KEY = "KEY";
    public static final String TARGET = "TARGET";

    private RecyclerView recyclerView;
    private View contentView;
    private TextView tvResult;

    /*data*/
    private AddressAdapter adapter;
    private String searchKey;
    private String targetCity;
    private PoiSearch mPoiSearch = null;

    public static AddressListDialogFragment newInstance(String searchKey, String targetCity) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY, searchKey);
        bundle.putString(TARGET, targetCity);
        AddressListDialogFragment fragment = new AddressListDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {

        if (getArguments() != null) {
            searchKey = getArguments().getString(KEY, "");
            targetCity = getArguments().getString(TARGET, "");
        }

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
        recyclerView = contentView.findViewById(R.id.recyclerview);
        tvResult = contentView.findViewById(R.id.tv_result);
    }

    private void initView() {

        initHeight();
        initPoiSearch();

        adapter = new AddressAdapter(mActivity, poiInfos);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(adapter);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);

        adapter.setItemListener(new AddressAdapter.ItemClick() {
            @Override
            public void itemListener(int position, PoiInfo data) {
                dismiss();
                if (itemListener != null) {
                    itemListener.itemResult(data);
                }
            }
        });
    }

    private void initPoiSearch() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(listener);
        mPoiSearch.searchInCity((new PoiCitySearchOption()).city(targetCity).keyword(searchKey).pageNum(0));
    }

    private List<PoiInfo> poiInfos = new ArrayList<>();

    private OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult result) {
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                setResultView("未找到结果");
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR && poiInfos != null) {
                setResultView("");
                poiInfos = new ArrayList<>();
                poiInfos.addAll(result.getAllPoi());
                adapter.setDatas(poiInfos);
                return;
            }

            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                String strInfo = "在";
                for (CityInfo cityInfo : result.getSuggestCityList()) {
                    strInfo += cityInfo.city;
                    strInfo += ",";
                }
                strInfo += "找到结果";
                Toast.makeText(mActivity, strInfo, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        }
    };

    private void setResultView(String tips) {
        if (TextUtils.isEmpty(tips)) {
            recyclerView.setVisibility(View.VISIBLE);
            tvResult.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText(tips);
        }
    }

    private ItemClick itemListener;

    public void setItemListener(ItemClick listener) {
        this.itemListener = listener;
    }

    public interface ItemClick {
        void itemResult(PoiInfo data);
    }
}