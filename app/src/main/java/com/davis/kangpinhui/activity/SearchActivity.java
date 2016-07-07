package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.db.SearchHistroy;
import com.davis.kangpinhui.db.SearchHistroyDao;
import com.davis.kangpinhui.util.CommonManager;
import com.davis.kangpinhui.views.FlowLayout;

import java.util.ArrayList;

import retrofit2.Call;

public class SearchActivity extends BaseActivity {

    private FlowLayout mFlowLayout;
    private ListView search_list_histroy;
    private LinearLayout search_back;
    private LinearLayout search_right_iv;
    private TextView search_clear_tv;
    private EditText search_et;
    private SearchHistroyDao dao;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_search;
    }


    public static void jumpSearchActivity(Context context, String key) {
        Intent it = new Intent(context, SearchActivity.class);
        it.putExtra("key", key);
        context.startActivity(it);
    }

    @Override
    protected void initVariable() {
        dao = new SearchHistroyDao(this);

    }

    @Override
    protected void findViews() {

        search_et = $(R.id.search_et);
        search_back = $(R.id.search_back);
        search_right_iv = $(R.id.search_right_iv);
        search_list_histroy = $(R.id.search_list_histroy);
        search_clear_tv = $(R.id.search_clear_tv);
        mFlowLayout = $(R.id.search_flowlayout);
        getRefuse();
    }

    @Override
    protected void initData() {

        Call<BaseModel<ArrayList<String>>> call = ApiInstant.getInstant().getSearchTag(AppApplication.apptype);

        call.enqueue(new ApiCallback<BaseModel<ArrayList<String>>>() {
            @Override
            public void onSucssce(BaseModel<ArrayList<String>> arrayListBaseModel) {
                ArrayList<String> list = arrayListBaseModel.object;
                if (list != null) {
                    for (String tag : list) {
                        mFlowLayout.addView(newFlowTagView(tag));
                    }
                    mFlowLayout.setVisibility(View.VISIBLE);
                } else {
                    mFlowLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    protected void setListener() {
        search_list_histroy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String key=dao.getKeyList().get(position).getKey();
                SearchResultActivity.jumpSearchResultActivity(SearchActivity.this,key,true);

            }
        });
    }

    @Override
    public void doClick(View view) {

        switch (view.getId()) {
            case R.id.search_back:
                finish();
                break;
            case R.id.search_right_iv:

                getSearchKey();
                break;
            case R.id.search_clear_tv:
                dao.removeall();
                getRefuse();
                break;
        }
    }

    private void getSearchKey() {
        String str = search_et.getText().toString().trim();
        if(!TextUtils.isEmpty(str)){
        SearchHistroy histroy = new SearchHistroy();
        histroy.setKey(str);
        dao.add(histroy);
        getRefuse();
            SearchResultActivity.jumpSearchResultActivity(SearchActivity.this, str,true);
        }
        search_et.setText("");
    }

    private void getRefuse() {

        search_list_histroy.setAdapter(new CommonBaseAdapter<SearchHistroy>(mActivity, (ArrayList<SearchHistroy>) dao.getKeyList(), R.layout.activity_search_histroy_item) {
            @Override
            public void convert(ViewHolder holder, SearchHistroy itemData, int position) {
                holder.setText(R.id.search_list_history_item, itemData.getKey());
            }
        });
    }

    public View newFlowTagView(final String tag) {
        final TextView textView = (TextView) View.inflate(mActivity, R.layout.layout_flow_tag, null);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        int dp = CommonManager.dpToPx(25);
        params.setMargins(dp, dp, dp, dp);
        textView.setLayoutParams(params);
        textView.setText(tag);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setBackgroundResource(R.drawable.bg_flow_tag_unselect);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchResultActivity.jumpSearchResultActivity(SearchActivity.this, tag,true);
            }
        });
        return textView;
    }
}
