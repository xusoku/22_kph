package com.davis.kangpinhui.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.model.Address;
import com.davis.kangpinhui.model.Shop;
import com.davis.kangpinhui.util.AppManager;
import com.davis.kangpinhui.util.CommonManager;
import com.davis.kangpinhui.util.DisplayMetricsUtils;
import com.davis.kangpinhui.util.LocalUtil;
import com.davis.kangpinhui.util.LogUtils;
import com.davis.kangpinhui.util.SharePreferenceUtils;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.views.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * AMapV1地图中简单介绍poisearch搜索
 */
public class PoiKeywordSearchActivity extends Activity implements
        AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, TextWatcher,
        PoiSearch.OnPoiSearchListener {
    private AMap aMap;
    private MapView mapView;
    private AutoCompleteTextView searchText;// 输入搜索关键字
    private String keyWord = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条
    private EditText editCity;// 要输入的城市名字或者城市区号
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    private ImageView searchText_back;
    private TextView poi_text;

    private Shop shop;
    private String type = "";

    public static void jumpPoiKeywordSearchActivity(Context cot, Shop id, String str) {
        Intent it = new Intent(cot, PoiKeywordSearchActivity.class);
        it.putExtra("id", id);
        it.putExtra("type", str);
        cot.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poikeywordsearch);

        poi_text = (TextView) findViewById(R.id.poi_text);
        searchText_back = (ImageView) findViewById(R.id.searchText_back);
        listView = (LoadMoreListView) findViewById(R.id.poi_search_lst);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        shop = getIntent().getParcelableExtra("id");

        type = getIntent().getStringExtra("type");
        if (shop == null) {
            return;
        }
        init();
        initPopupSortWindow();
        setPoiText();
        searchText_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setPoiText() {
        String ss =searchText.getText().toString().trim();
        if(TextUtils.isEmpty(ss)){
            ss="关键词";
            poi_text.setText("配送范围内暂无与"+ss+"相关的小区/大楼");
        }else{
            poi_text.setText("配送范围内暂无与\""+ss+"\"相关的小区/大楼");
        }

    }


    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置页面监听
     */
    private void setUpMap() {

        String[] s = shop.center.split(",");
        LatLng latLng = new LatLng(Double.parseDouble(s[1]), Double.parseDouble(s[0]));

        //绘制marker
        Marker marker = aMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.mark_b)))
                .draggable(true));


        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));// 设置指定的可视区域地图

        aMap.addPolygon(new PolygonOptions().addAll(createDDRectangle(shop.polygon)).fillColor(Color.parseColor("#550192dc"))
                .strokeColor(Color.RED).strokeWidth(1));
        searchText = (AutoCompleteTextView) findViewById(R.id.searchText);
        searchText.addTextChangedListener(this);// 添加文本输入框监听事件
        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
    }

    /**
     * 点击搜索按钮
     */
    public void searchButton() {
        keyWord = checkEditText(searchText);
        if ("".equals(keyWord)) {
            ToastUitl.showToast("请输入搜索关键字");
            return;
        } else {
            doSearchQuery();
        }
    }


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        list.clear();
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "");
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(createDRectangle(shop.polygon)));//
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**
     * 开始进行poi搜索
     */
    protected void donextSearchQuery() {
        currentPage++;
        query = new PoiSearch.Query(keyWord, "");
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(createDRectangle(shop.polygon)));//
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**
     * 生成一个多边形
     */
    private List<LatLonPoint> createDRectangle(String pot) {
        List<LatLonPoint> list = new ArrayList<>();
        String[] strs = pot.split(";");
        for (String str : strs) {
            String[] s = str.split(",");
            LatLonPoint latLng = new LatLonPoint(Double.parseDouble(s[1]), Double.parseDouble(s[0]));
            list.add(latLng);
        }
        return list;
    }

    /**
     * 生成一个多边形
     */
    private List<LatLng> createDDRectangle(String pot) {
        List<LatLng> list = new ArrayList<>();
        String[] strs = pot.split(";");
        for (String str : strs) {
            String[] s = str.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(s[1]), Double.parseDouble(s[0]));
            list.add(latLng);
        }
        return list;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        return null;
    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!IsEmptyOrNullString(newText)) {
            searchButton();
        }
    }


    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                        aMap.addPolygon(new PolygonOptions().addAll(createDDRectangle(shop.polygon)).fillColor(Color.parseColor("#550000ff"))
                                .strokeColor(Color.RED).strokeWidth(1));
                        listView.setVisibility(View.VISIBLE);
                        list.addAll(poiItems);
                        adapter.notifyDataSetChanged();

                        if (list.size() >= 20)
                            listView.onLoadSucess(true);
                        else listView.onLoadSucess(false);
                    } else {
                        setPoiText();
                        listView.setVisibility(View.GONE);
                        listView.onLoadSucess(false);
                    }
                }
            } else {
                setPoiText();
                listView.setVisibility(View.GONE);
                listView.onLoadSucess(false);
            }
        } else {
            setPoiText();
            listView.setVisibility(View.GONE);
            listView.onLoadSucess(false);
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        // TODO Auto-generated method stub

    }

    private LoadMoreListView listView;
    private ArrayList<PoiItem> list;
    private CommonBaseAdapter<PoiItem> adapter;

    private void initPopupSortWindow() {
        // TODO Auto-generated method stub
        list = new ArrayList<>();
        adapter = new CommonBaseAdapter<PoiItem>(this, list, R.layout.activity_poikeywordsearch_item) {
            @Override
            public void convert(ViewHolder holder, PoiItem itemData, int position) {
                holder.setText(R.id.poi_item_title, itemData.getTitle());
                holder.setText(R.id.poi_item_address, "地址:" + itemData.getSnippet());
                if (!TextUtils.isEmpty(itemData.getTel())) {
                    holder.getView(R.id.poi_item_phone).setVisibility(View.VISIBLE);
                    holder.setText(R.id.poi_item_phone, "电话:" + itemData.getTel());
                } else {
                    holder.getView(R.id.poi_item_phone).setVisibility(View.GONE);
                }

            }
        };
        listView.setAdapter(adapter);

        listView.setOnLoadListener(new LoadMoreListView.OnLoadListener() {
            @Override
            public void onLoad(LoadMoreListView listView) {
                if (list.size() >= 20)
                    donextSearchQuery();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!TextUtils.isEmpty(type) && type.equals("my_address")) {
                    AppApplication.poiItem = list.get(position);
                    finish();
                    AppManager.getAppManager().finishActivity(ShopActivity.class);
                    CommonManager.dismissSoftInputMethod(PoiKeywordSearchActivity.this, listView.getWindowToken());
                    return;
                }
                SharePreferenceUtils.getSharedPreferences().putString("address", list.get(position).getTitle());
                LatLonPoint s = list.get(position).getLatLonPoint();
                LatLng latLng = new LatLng(s.getLatitude(), s.getLongitude());
                LocalUtil.getShopid(latLng, AppApplication.shoplist);
                finish();
                AppManager.getAppManager().finishActivity(ShopActivity.class);
                EventBus.getDefault().post(new Address());
                CommonManager.dismissSoftInputMethod(PoiKeywordSearchActivity.this, listView.getWindowToken());

            }
        });
    }

    public static String checkEditText(EditText editText) {
        if (editText != null && editText.getText() != null
                && !(editText.getText().toString().trim().equals(""))) {
            return editText.getText().toString().trim();
        } else {
            return "";
        }
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }
}
