package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.ProductDetail;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.api.ApiService;
import com.davis.kangpinhui.util.CommonManager;
import com.davis.kangpinhui.util.DisplayMetricsUtils;
import com.davis.kangpinhui.util.ShareManager;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.util.UtilText;
import com.davis.kangpinhui.views.BadgeView;
import com.davis.kangpinhui.views.FlowLayout;
import com.davis.kangpinhui.views.MyScrollViewSmooh;
import com.davis.kangpinhui.views.TBLayout;
import com.davis.kangpinhui.views.XWebView;
import com.davis.kangpinhui.views.dargview.DavisWebView;
import com.davis.kangpinhui.views.dargview.DivasScrollViewPageOne;
import com.davis.kangpinhui.views.dargview.DragLayout;
import com.davis.kangpinhui.views.loopbanner.LoopPageAdapter;
import com.davis.kangpinhui.views.loopbanner.LoopBanner;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import retrofit2.Call;


public class ProductDetailActivity extends BaseActivity implements TBLayout.OnPullListener, TBLayout.OnPageChangedListener {


    private String id = "";

    private ViewPager product_detail_banner;
    private TextView
            product_detail_title,
            product_detail_price,
            product_detail_logo_text,
            product_detail_date,
            product_detail_save,
            product_detail_producter;

    private TBLayout mLayout;
    private ScrollView  mFooter;
    private MyScrollViewSmooh mHeader;
    private TextView product_detail_title_text;
    private XWebView product_detail_xweb;
    private LinearLayout mHeaderContent ,mFooterContent;


    private LinearLayout product_detail_title_text_linear, add_cart_number_linear;

    private AddCartPopuWindow addpopupWindow;
    private ImageView add_cart_icon;
    private LinearLayout product_detail_back;

    private ProductDetail productDetail;

    public static void jumpProductDetailActivity(Context conx, String id) {
        Intent it = new Intent(conx, ProductDetailActivity.class);
        it.putExtra("id", id);
        conx.startActivity(it);
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_product_detail;
    }

    @Override
    protected void initVariable() {
        id = getIntent().getStringExtra("id");
    }

    @Override
    protected void findViews() {
        setTranslucentStatusBarGone();
        product_detail_banner = $(R.id.product_detail_banner);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) DisplayMetricsUtils.getWidth(), (int) DisplayMetricsUtils.getWidth());
        product_detail_banner.setLayoutParams(layoutParams);
        product_detail_back = $(R.id.product_detail_back);
        add_cart_number_linear = $(R.id.add_cart_number_linear);
        mLayout = $(R.id.product_detail_drag);

        mLayout.setOnPullListener(this);
        mLayout.setOnContentChangeListener(this);
        product_detail_title = $(R.id.product_detail_title);
        product_detail_price = $(R.id.product_detail_price);
        product_detail_logo_text = $(R.id.product_detail_logo_text);
        product_detail_date = $(R.id.product_detail_date);
        product_detail_save = $(R.id.product_detail_save);
        product_detail_xweb = $(R.id.product_detail_xweb);
        product_detail_producter = $(R.id.product_detail_producter);
        mHeader = (MyScrollViewSmooh) findViewById(R.id.header);
        mFooter = (ScrollView) findViewById(R.id.footer);
        mHeaderContent = (LinearLayout) mHeader.getChildAt(0);
        mFooterContent = (LinearLayout) mFooter.getChildAt(0);
        product_detail_title_text = $(R.id.product_detail_title_text);
        product_detail_title_text_linear = $(R.id.product_detail_title_text_linear);
        add_cart_icon = $(R.id.add_cart_icon);


        backgroundDefaultBadge = new BadgeView(this);
        backgroundDefaultBadge.setTargetView(add_cart_number_linear);
        setcartNumber();
        addpopupWindow=new AddCartPopuWindow(this);
        addpopupWindow.setBackgroundDefaultBadge(backgroundDefaultBadge);
    }

    @Override
    protected void onActivityLoading() {
        super.onActivityLoading();
    }

    @Override
    protected void initData() {
        startActivityLoading();
        Call<BaseModel<ProductDetail>> call = ApiInstant.getInstant().getProductDetail(AppApplication.apptype,
                id, AppApplication.shopid);
        call.enqueue(new ApiCallback<BaseModel<ProductDetail>>() {
            @Override
            public void onSucssce(BaseModel<ProductDetail> productDetailBaseModel) {
                onActivityLoadingSuccess();
                productDetail = productDetailBaseModel.object;
                ArrayList<String> bannerList = productDetail.piclist;
                getBannerData(bannerList);
                setBindData(productDetail);
                addpopupWindow.setBindPopData(productDetail);
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });

    }


    public void getBannerData(ArrayList<String> bannerList) {
        product_detail_banner.setAdapter(new LoopPageAdapter<String>(mContext, bannerList, R.layout.layout_main_banner_item) {

            @Override
            public void convert(ViewHolder holder, final String itemData, final int position) {
                // TODO Auto-generated method stub
                ImageView imageView = (ImageView) holder.getConvertView();
                String img = itemData;
                Glide.with(ProductDetailActivity.this).load(img)
                        .placeholder(R.mipmap.img_defualt_bg)
                        .error(R.mipmap.img_defualt_bg)
                        .into(imageView);
//                        .placeholder(R.drawable.placeholder)
//                        .error(R.drawable.imagenotfound)
            }
        });
    }

    public void setBindData(ProductDetail productDetail) {
        product_detail_title.setText(productDetail.sphysicname);
        product_detail_price.setText("");
        product_detail_price.append(UtilText.getProductDetail("¥"));
        product_detail_price.append(UtilText.getBigProductDetail(productDetail.fprice));
        product_detail_price.append("/" + productDetail.sstandard);
        product_detail_logo_text.setText(productDetail.sbrandname);
        product_detail_date.setText(productDetail.sshelflife);
        product_detail_save.setText(productDetail.sstorage);
        product_detail_producter.setText(productDetail.sfactoryname);

        product_detail_xweb.loadDataWithBaseURL(null, "<style type=\"text/css\">img{max-width:100%;height: auto;margin-bottom: .1rem;}</style>"+(productDetail.scontentinfo)+"<p>.</p>", "text/html", "utf-8", null);
    }


    @Override
    protected void setListener() {
        product_detail_title_text_linear.setAlpha(0);
    }

    @Override
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.product_detail_back:
                finish();
                break;
            case R.id.add_cart_addlinear:
                if (productDetail != null) {
                    addpopupWindow.addpopupWindow.showAtLocation(product_detail_xweb, Gravity.NO_GRAVITY, 0, 0);
                } else {
                    ToastUitl.showToast("暂无数据");
                }
                break;
            case R.id.add_cart_number_linear:
                CartListActivity.jumpCartListActivity((this));
                break;
            case R.id.product_detail_share:
                ShareManager mShareManager = new ShareManager(mActivity);
                mShareManager.setTitle("康品汇-家门口的生鲜店");
                mShareManager.setText("家门口的康品汇又有好赞的生鲜啦，我猜你肯定喜欢，快来戳我啊~");
                mShareManager.setWebUrl("http://m.kangpinhui.com/common/html.do?type=shareurl");
                mShareManager.setTitleUrl("http://m.kangpinhui.com/common/html.do?type=shareurl");
                mShareManager.setImageUrl("http://img.kangpinhui.com/images/logo200.png");
                mShareManager.showShareDialog(this);
                break;
        }

    }

    private BadgeView backgroundDefaultBadge;

    public void setcartNumber() {
        String number = AppApplication.getCartcount();
        if (!TextUtils.isEmpty(number) && !number.equals("0") && !number.equals("0.0"))
            backgroundDefaultBadge.setText((int)Float.parseFloat(number)+"");
    }

    @Override
    public boolean headerFootReached(MotionEvent event) {
        if (mHeader.getScrollY() + mHeader.getHeight() >= mHeaderContent
                .getHeight()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean footerHeadReached(MotionEvent event) {
        if (mFooter.getScrollY() == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void onPageChanged(int stub) {
        switch (stub) {
            case TBLayout.SCREEN_HEADER:
                Log.d("tag", "SCREEN_HEADER");
                product_detail_title_text_linear.setAlpha(0);
                break;
            case TBLayout.SCREEN_FOOTER:
                Log.d("tag", "SCREEN_FOOTER");
                product_detail_title_text_linear.setAlpha(1);
                break;
        }
    }
}
