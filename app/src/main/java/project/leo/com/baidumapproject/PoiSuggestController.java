package project.leo.com.baidumapproject;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe :
 * Created by Leo on 2018/6/27 on 18:12.
 */
public class PoiSuggestController {
    private AutoCompleteTextView evPOI;
    private FragmentActivity activity;

    private SuggestionSearch mSuggestionSearch = null;
    private List<String> suggest = new ArrayList<>();
    private ArrayAdapter sugAdapter;
    private String targetCity;

    public PoiSuggestController(FragmentActivity activity, String targetCity) {
        this.activity = activity;
        this.targetCity = targetCity;
        initView();
        initEditView();
    }

    private void initView() {
        evPOI = activity.findViewById(R.id.ev_poi);
    }

    private void initEditView() {
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(suggestionResultListener);
        evPOI.setThreshold(1);

        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        evPOI.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(cs.toString()).city(targetCity));
            }
        });

        evPOI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AddressListDialogFragment fragment = AddressListDialogFragment.newInstance(evPOI.getText().toString(), targetCity);
                fragment.show(activity.getSupportFragmentManager(), "POI");
                fragment.setItemListener(new AddressListDialogFragment.ItemClick() {
                    @Override
                    public void itemResult(PoiInfo data) {
                        if (clickCallBack != null && data != null) {
                            clickCallBack.itemResult(data);
                        }
                    }
                });
            }
        });
    }

    private AddressListDialogFragment.ItemClick clickCallBack;

    public void setItemClickCallBack(AddressListDialogFragment.ItemClick clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    private OnGetSuggestionResultListener suggestionResultListener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult res) {
            if (res == null || res.getAllSuggestions() == null) {
                return;
            }
            suggest = new ArrayList<>();
            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                if (info.key != null) {
                    suggest.add(info.key);
                }
            }

            sugAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, suggest);
            evPOI.setAdapter(sugAdapter);
            if (sugAdapter != null) {
                sugAdapter.notifyDataSetChanged();
            }
        }
    };

    // 退出时销毁
    public void destpry() {
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
        }
    }
}
