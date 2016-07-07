package com.davis.kangpinhui.fragment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.MainActivity;
import com.davis.kangpinhui.activity.PolygonActivity;
import com.davis.kangpinhui.activity.SearchResultActivity;
import com.davis.kangpinhui.activity.TuangouChihuoActivity;
import com.davis.kangpinhui.model.Banner;
import com.davis.kangpinhui.model.Index;
import com.davis.kangpinhui.model.Product;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.CartListActivity;
import com.davis.kangpinhui.activity.ProductDetailActivity;
import com.davis.kangpinhui.activity.RechargeActivity;
import com.davis.kangpinhui.activity.SearchActivity;
import com.davis.kangpinhui.activity.ShopActivity;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.adapter.recycleradapter.CommonRecyclerAdapter;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.fragment.base.BaseFragment;
import com.davis.kangpinhui.util.CommonManager;
import com.davis.kangpinhui.util.LogUtils;
import com.davis.kangpinhui.util.SharePreferenceUtils;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.util.UtilText;
import com.davis.kangpinhui.views.BadgeView;
import com.davis.kangpinhui.views.MySwipeRefreshLayout;
import com.davis.kangpinhui.views.loopbanner.LoopPageAdapter;
import com.davis.kangpinhui.views.viewlineloop.LoopBanner;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by davis on 16/5/18.
 */
public class IndexFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout index_rechange;
    private LinearLayout index_tuan;
    private LinearLayout index_classic;
    private LoopBanner index_loopbanner;
    private LinearLayout headerView;

    private ListView content;
    private LinearLayout index_cart;
    private LinearLayout index_search;
    private TextView index_local_select;
    private LinearLayout index_local_select_linear;
    private LinearLayout no_linear_shopid;
    private TextView no_shopid_add_address;
    private TextView no_shopid_see_sending;
    private TextView no_shopid_text;
    private MySwipeRefreshLayout index_refresh;

    private boolean isRefreshOrLoad = false;

    ArrayList<Banner> bannerList = new ArrayList<>();
    ArrayList<Index.Productlist> recommandList = new ArrayList<Index.Productlist>();

    BadgeView backgroundDefaultBadge;

    @Override
    protected void initVariable() {

    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_index;
    }

    @Override
    protected void findViews(View view) {

        headerView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_index_header, null);

        index_loopbanner = $(headerView, R.id.index_loopbanner);
        index_classic = $(headerView, R.id.index_classic);
        index_tuan = $(headerView, R.id.index_tuan);
        index_rechange = $(headerView, R.id.index_rechange);


        no_linear_shopid = $(view, R.id.no_linear_shopid);
        no_shopid_text = $(view, R.id.no_shopid_text);
        no_shopid_text.setText("");
        no_shopid_text.append("Hi，小康恭候多时了～");
        no_shopid_text.append(UtilText.getOrderDetail("门店周边三公里平均一小时送达"));
        no_shopid_text.append("，请输入地址找到为您服务的门店吧!");
        no_shopid_add_address = $(view, R.id.no_shopid_add_address);
        no_shopid_see_sending = $(view, R.id.no_shopid_see_sending);
        index_refresh = $(view, R.id.index_refresh);
        index_local_select = $(view, R.id.index_local_select);
        index_local_select_linear = $(view, R.id.index_local_select_linear);
        index_cart = $(view, R.id.index_cart);
        index_search = $(view, R.id.index_search);

        content = $(view, R.id.content);
        content.addHeaderView(headerView);
        index_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshOrLoad = true;
                LogUtils.e(TAG, "isRefreshOrLoad");
                getDate();
            }
        });

        index_loopbanner.setPageIndicator(true);
        backgroundDefaultBadge = new BadgeView(getActivity());
        backgroundDefaultBadge.setTargetView(index_cart);

        String add = SharePreferenceUtils.getSharedPreferences().getString("address", "选择配送地址");
        index_local_select.setText("送至:" + add);
    }

    @Override
    protected void initData() {

        if (!TextUtils.isEmpty(AppApplication.shopid)) {
            no_linear_shopid.setVisibility(View.GONE);
            startFragmentLoading();
        } else {
            no_linear_shopid.setVisibility(View.VISIBLE);
        }

    }

    public void setcartNumber() {
        String number = AppApplication.getCartcount();
        if (!TextUtils.isEmpty(number) && !number.equals("0") && !number.equals("0.0") && backgroundDefaultBadge != null){
            backgroundDefaultBadge.setVisibility(View.VISIBLE);
            backgroundDefaultBadge.setText((int) Float.parseFloat(number) + "");
        }else{
            setcartNumberLoginout();
        }
    }
    public void setcartNumberLoginout() {
        if(backgroundDefaultBadge!=null)
            backgroundDefaultBadge.setVisibility(View.GONE);
    }

    public void setindex_local_select(String str) {
        index_local_select.setText("送至:" + str);
        initData();
    }

    @Override
    protected void onFragmentLoading() {
        super.onFragmentLoading();
        LogUtils.e(TAG, "onFragmentLoading");
        isRefreshOrLoad = true;
        getDate();
    }

    private void getDate() {
        Call<BaseModel<Index>> call = ApiInstant.getInstant().getIndex(AppApplication.apptype, AppApplication.shopid, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<Index>>() {
            @Override
            public void onSucssce(BaseModel<Index> indexBaseModel) {
                onFragmentLoadingSuccess();//显示内容区
                CommonManager.setRefreshingState(index_refresh, false);//隐藏下拉刷新
                if (isRefreshOrLoad) {
                    bannerList.clear();
                    recommandList.clear();
                }
                Index index = indexBaseModel.object;
                bannerList.addAll(index.bannerList);
                recommandList.addAll(index.recommandList);
                getBannerData();
                getContentData();
            }

            @Override
            public void onFailure() {
                if (isRefreshOrLoad) {
                    onFragmentLoadingFailed();//显示刷新失败网络异常
                    CommonManager.setRefreshingState(index_refresh, false);//隐藏下拉刷新
                } else {
//                    listShowFilm.onLoadFailed();//加载失败
                }
            }
        });
    }

    public void getBannerData() {
        index_loopbanner.setPageAdapter(new LoopPageAdapter<Banner>(mContext, bannerList, R.layout.layout_main_banner_item) {

            @Override
            public void convert(ViewHolder holder, final Banner itemData, final int position) {
                // TODO Auto-generated method stub
                ImageView imageView = (ImageView) holder.getConvertView();
                String img = itemData.picurl;
                Glide.with(getActivity()).load(img)
                        .placeholder(R.mipmap.img_defualt_bg)
                        .error(R.mipmap.img_defualt_bg)
                        .into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String gotype = itemData.gotype;
                        if(TextUtils.isEmpty(gotype)){
                            gotype="";
                        }
                        String tempvalue = itemData.tempvalue;
                        if(TextUtils.isEmpty(tempvalue)){
                            tempvalue="";
                        }
                        if(gotype.equals("detail")){
                            ProductDetailActivity.jumpProductDetailActivity(getActivity(), tempvalue);
                        }else if(gotype.equals("rootlist")){
                            SearchResultActivity.jumpSearchResultActivity(getActivity(),"",false,"",tempvalue);
                        }else if(gotype.equals("list")){
                            SearchResultActivity.jumpSearchResultActivity(getActivity(),"",false,tempvalue,"");
                        }else if(gotype.equals("special")){
                            SearchResultActivity.jumpSearchResultActivity(getActivity(),itemData.title,true,tempvalue);
                        }else if(gotype.equals("search")){
                            SearchResultActivity.jumpSearchResultActivity(getActivity(),tempvalue,true);
                        }else{
                            ToastUitl.showToast("暂无定义");
                        }
                    }
                });
            }
        });
    }


    public void getContentData() {
        content.setAdapter(new CommonBaseAdapter<Index.Productlist>(getActivity(), recommandList, R.layout.fragment_index_item_layout) {
            @Override
            public void convert(ViewHolder holder, final Index.Productlist itemData, int position) {

                String image = itemData.picurl;
                ArrayList<Product> list = itemData.list;

                holder.setImageByUrl(R.id.fragment_index_item_image, image);

                holder.getView(R.id.fragment_index_item_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchResultActivity.jumpSearchResultActivity(getActivity(),itemData.title,true,itemData.id);
                    }
                });
                getContentitemData((RecyclerView) holder.getView(R.id.fragment_index_item_recycler), list);
            }
        });
    }

    private CommonRecyclerAdapter<Product> adapter;

    public void getContentitemData(RecyclerView loadrecycler, final ArrayList<Product> list) {


        adapter = new CommonRecyclerAdapter<Product>(getActivity(), list, R.layout.fragment_index_item_layout_item) {
            @Override
            public void convert(BaseViewHolder holder, Product itemData, int position) {

                ImageView iv = holder.getView(R.id.fragment_index_item_image_item);
                Glide.with(getActivity())
                        .load(itemData.picurl)
                        .placeholder(R.mipmap.img_defualt_bg)
                        .error(R.mipmap.img_defualt_bg)
                        .into(iv);

                TextView tv_name = holder.getView(R.id.fragment_index_item_name);
                tv_name.setText(itemData.productname);

                TextView tv_price = holder.getView(R.id.fragment_index_item_price);
                tv_price.setText("");
                tv_price.append("¥ ");
                tv_price.append((UtilText.getIndexPrice(itemData.fprice)));
                tv_price.append("/" + itemData.sstandard);
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        loadrecycler.setLayoutManager(linearLayoutManager);

        loadrecycler.setAdapter(adapter);
        adapter.setOnItemClickLitener(new CommonRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View itemView, int position) {
                ProductDetailActivity.jumpProductDetailActivity(getActivity(), list.get(position).iproductid);
            }

            @Override
            public void onItemLongClick(View itemView, int position) {

            }
        });
    }

    @Override
    protected void setListener() {

        index_local_select_linear.setOnClickListener(this);
        index_classic.setOnClickListener(this);
        index_rechange.setOnClickListener(this);
        index_tuan.setOnClickListener(this);
        index_cart.setOnClickListener(this);
        index_search.setOnClickListener(this);
        no_shopid_add_address.setOnClickListener(this);
        no_shopid_see_sending.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.index_local_select_linear:
                Intent it = new Intent(getActivity(), ShopActivity.class);
                startActivityForResult(it, 0);
                break;
            case R.id.index_classic:
                MainActivity activity = (MainActivity) getActivity();
                activity.change2();
                break;
            case R.id.index_rechange:
                RechargeActivity.jumpRechangeActivity(getActivity());
                break;
            case R.id.index_tuan:
                if(AppApplication.isLogin(getActivity()))
                TuangouChihuoActivity.jumpTuangouChihuoActivity(getActivity());
                break;
            case R.id.index_cart:
                CartListActivity.jumpCartListActivity(getActivity());
                break;
            case R.id.index_search:
                SearchActivity.jumpSearchActivity(getActivity(), "");
                break;
            case R.id.no_shopid_see_sending:
                if (AppApplication.shoplist.size() > 0)
                    PolygonActivity.jumpPolygonActivity(getActivity(), AppApplication.shoplist);
                else
                    ToastUitl.showToast("暂无数据");
                break;
            case R.id.no_shopid_add_address:
                ShopActivity.jumpShopActivity(getActivity());
                break;
        }
    }
}
