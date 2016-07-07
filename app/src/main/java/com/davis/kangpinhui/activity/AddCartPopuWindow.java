package com.davis.kangpinhui.activity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.api.ApiService;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.Product;
import com.davis.kangpinhui.model.ProductDetail;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.util.CommonManager;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.util.UtilText;
import com.davis.kangpinhui.views.BadgeView;
import com.davis.kangpinhui.views.FlowLayout;

import de.greenrobot.event.EventBus;
import retrofit2.Call;

/**
 * Created by davis on 16/6/6.
 */
public class AddCartPopuWindow {

    private Context context;
    public AddCartPopuWindow(Context context){
        this.context=context;
        initPopupWindow();
    }

    private ImageView add_cart_image, add_cart_image_close;

    private TextView add_cart_text_vip_price, add_cart_text_price, add_cart_guige,
            add_cart_add_mins, add_cart_add, add_cart_add_center, add_cart_add_btn;

    private LinearLayout add_cart_add_linear_sp;

    private View add_cart_pop_view;

    private FlowLayout add_cart_flow;

    private BadgeView backgroundDefaultBadge;


    public void setBackgroundDefaultBadge(BadgeView backgroundDefaultBadge) {
        this.backgroundDefaultBadge = backgroundDefaultBadge;
    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(@NonNull View view, @IdRes int id)
    {
        return (T) (view.findViewById(id));
    }

    public PopupWindow addpopupWindow;
    //初始化分类
    private void initPopupWindow() {
        // TODO Auto-generated method stub
        View view = LayoutInflater.from(context).inflate(R.layout.activity_product_detail_add_cart, null);

        add_cart_flow = $(view, R.id.add_cart_flow);
        add_cart_pop_view = $(view, R.id.add_cart_pop_view);
        add_cart_add_linear_sp = $(view, R.id.add_cart_add_linear_sp);
        add_cart_image = $(view, R.id.add_cart_image);
        add_cart_image_close = $(view, R.id.add_cart_image_close);
        add_cart_text_vip_price = $(view, R.id.add_cart_text_vip_price);
        add_cart_text_price = $(view, R.id.add_cart_text_price);
        add_cart_add_mins = $(view, R.id.add_cart_add_mins);
        add_cart_add_center = $(view, R.id.add_cart_add_center);
        add_cart_add_btn = $(view, R.id.add_cart_add_btn);
        add_cart_guige = $(view, R.id.add_cart_guige);
        add_cart_add = $(view, R.id.add_cart_add);
        addpopupWindow = new PopupWindow(view,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        addpopupWindow.setFocusable(true);
        addpopupWindow.setOutsideTouchable(true);
        addpopupWindow.setAnimationStyle(R.style.popwin_add_cart_anim_style);
        addpopupWindow.setOnDismissListener(new popupWindowclickListener());
        addpopupWindow.setBackgroundDrawable(new BitmapDrawable());


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addpopupWindow != null && addpopupWindow.isShowing()) {
                    addpopupWindow.dismiss();
                }
            }
        });

        add_cart_pop_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addpopupWindow != null && addpopupWindow.isShowing()) {
                    addpopupWindow.dismiss();
                }
            }
        });
        add_cart_image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addpopupWindow != null && addpopupWindow.isShowing()) {
                    addpopupWindow.dismiss();
                }
            }
        });
    }

    class popupWindowclickListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            if (add_cart_add_center != null) {
                add_cart_add_center.setText("1");
            }
        }
    }


    public void setBindPopData(final ProductDetail productDetail) {
        Glide.with(context).load(ApiService.picurl + productDetail.spicurl)
                .placeholder(R.mipmap.img_defualt_bg)
                .error(R.mipmap.img_defualt_bg)
                .into(add_cart_image);

        add_cart_text_vip_price.setText("");
        add_cart_text_price.setText("");

        add_cart_text_vip_price.append("会员价");
        add_cart_text_vip_price.append(UtilText.getProductDetail("¥"));
        add_cart_text_vip_price.append(UtilText.getBigProductDetail(productDetail.fvipprice));
        add_cart_text_vip_price.append("/" + productDetail.sstandard);

        add_cart_text_price.append("康品价");
        add_cart_text_price.append(UtilText.getProductDetail("¥"));
        add_cart_text_price.append(UtilText.getBigProductDetail(productDetail.fprice));
        add_cart_text_price.append("/" + productDetail.sstandard);

        add_cart_add_center.setText("1");


        add_cart_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppApplication.isLogin(context)) {

                    String num = add_cart_add_center.getText().toString().trim();
                    Call<BaseModel> call = ApiInstant.getInstant().addCart(AppApplication.apptype, AppApplication.shopid, num, productDetail.iproductid, srequire, AppApplication.token);

                    call.enqueue(new ApiCallback<BaseModel>() {
                        @Override
                        public void onSucssce(BaseModel baseModel) {
                            ToastUitl.showToast("添加成功");
//                            EventBus.getDefault().post(new Extendedinfo());

                            String number = AppApplication.getCartcount();
                            if (TextUtils.isEmpty(number) || number.equals("0.0")) {
                                number = "0";
                            }
                            int n = (int)Float.parseFloat(number);
                            n++;
                            AppApplication.setCartcount(n+"");

                            if(backgroundDefaultBadge!=null)
                            backgroundDefaultBadge.setText(n + "");

                            addpopupWindow.dismiss();
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }

            }
        });
        add_cart_guige.setText(productDetail.sstandard);
        add_cart_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = add_cart_add_center.getText().toString().trim();

                int n = Integer.valueOf(number);
                n++;
                add_cart_add_center.setText(n + "");
            }
        });

        add_cart_add_mins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = add_cart_add_center.getText().toString().trim();

                int n = Integer.valueOf(number);

                if (n <= 1) {
                    n = 1;
                } else {
                    n--;
                }
                add_cart_add_center.setText(n + "");
            }
        });


        if (!TextUtils.isEmpty(productDetail.srequire)) {
            add_cart_add_linear_sp.setVisibility(View.VISIBLE);

            String req = productDetail.srequire;
            String[] list = req.split(" ");
            getRequre(list);
        } else {
            add_cart_add_linear_sp.setVisibility(View.GONE);
        }
    }
    public void setBindPopData(final Product productDetail) {
        Glide.with(context).load( productDetail.picurl)
                .placeholder(R.mipmap.img_defualt_bg)
                .error(R.mipmap.img_defualt_bg)
                .into(add_cart_image);

        add_cart_text_vip_price.setText("");
        add_cart_text_price.setText("");

        add_cart_text_vip_price.append("会员价");
        add_cart_text_vip_price.append(UtilText.getProductDetail("¥"));
        add_cart_text_vip_price.append(UtilText.getBigProductDetail(productDetail.fvipprice));
        add_cart_text_vip_price.append("/" + productDetail.sstandard);

        add_cart_text_price.append("康品价");
        add_cart_text_price.append(UtilText.getProductDetail("¥"));
        add_cart_text_price.append(UtilText.getBigProductDetail(productDetail.fprice));
        add_cart_text_price.append("/" + productDetail.sstandard);

        add_cart_add_center.setText("1");


        add_cart_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppApplication.isLogin(context)) {

                    String num = add_cart_add_center.getText().toString().trim();
                    Call<BaseModel> call = ApiInstant.getInstant().addCart(AppApplication.apptype, AppApplication.shopid, num, productDetail.iproductid, srequire, AppApplication.token);

                    call.enqueue(new ApiCallback<BaseModel>() {
                        @Override
                        public void onSucssce(BaseModel baseModel) {
                            ToastUitl.showToast("添加成功");
//                            EventBus.getDefault().post(new Extendedinfo());

                            String number = AppApplication.getCartcount();
                            if (TextUtils.isEmpty(number) || number.equals("0.0")) {
                                number = "0";
                            }
                            int n = (int)Float.parseFloat(number);
                            n++;
                            AppApplication.setCartcount(n+"");
                            if(backgroundDefaultBadge!=null)
                            backgroundDefaultBadge.setText(n + "");

                            addpopupWindow.dismiss();
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }

            }
        });
        add_cart_guige.setText(productDetail.sstandard);
        add_cart_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = add_cart_add_center.getText().toString().trim();

                int n = Integer.valueOf(number);
                n++;
                add_cart_add_center.setText(n + "");
            }
        });

        add_cart_add_mins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = add_cart_add_center.getText().toString().trim();

                int n = Integer.valueOf(number);

                if (n <= 1) {
                    n = 1;
                } else {
                    n--;
                }
                add_cart_add_center.setText(n + "");
            }
        });


        if (!TextUtils.isEmpty(productDetail.srequire)) {
            add_cart_add_linear_sp.setVisibility(View.VISIBLE);

            String req = productDetail.srequire;
            String[] list = req.split(" ");
            getRequre(list);
        } else {
            add_cart_add_linear_sp.setVisibility(View.GONE);
        }
    }

    private String[] list;

    private void getRequre(String[] list) {

        this.list = list;
        add_cart_flow.removeAllViews();
        for (int i = 0; i < list.length; i++) {
            add_cart_flow.addView(newFlowTagView(list[i], i));
        }
    }

    public View newFlowTagView(final String tag, final int i) {
        final TextView textView = (TextView) View.inflate(context, R.layout.layout_flow_tag, null);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        int dp = CommonManager.dpToPx(25);
        params.setMargins(dp, dp, dp, dp);
        textView.setLayoutParams(params);
        textView.setText(tag);
        textView.setTextColor(context.getResources().getColor(R.color.black));
        textView.setBackgroundResource(R.drawable.bg_flow_tag_unselect);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequre(i);
                srequire=tag;
            }
        });
        return textView;
    }

    public View newFlowTag(final String tag, final int i) {
        final TextView textView = (TextView) View.inflate(context, R.layout.layout_flow_tag, null);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        int dp = CommonManager.dpToPx(25);
        params.setMargins(dp, dp, dp, dp);
        textView.setLayoutParams(params);
        textView.setText(tag);
        textView.setTextColor(context.getResources().getColor(R.color.black));
        textView.setBackgroundColor(context.getResources().getColor(R.color.lightblue));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequre(-1);
                srequire="";
            }
        });
        return textView;
    }

    private String srequire = "";

    private void getRequre(int j) {
        add_cart_flow.removeAllViews();
        for (int i = 0; i < list.length; i++) {
            if (i == j) {
                add_cart_flow.addView(newFlowTag(list[i], i));
            } else {
                add_cart_flow.addView(newFlowTagView(list[i], i));
            }
        }


    }

}
