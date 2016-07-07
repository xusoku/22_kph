package com.davis.kangpinhui.fragment.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davis.kangpinhui.R;
import com.davis.kangpinhui.util.LogUtils;
import com.davis.kangpinhui.views.ProgressWheel;
import com.umeng.analytics.MobclickAgent;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment
{

    public Context mContext;
    public LayoutInflater mInflater;
    private View contentView;
    private FrameLayout layBody;
    private ViewStub stubLoadingFailed;
    private FrameLayout layLoadingFailed;
    private LinearLayout layClickReload;
    private ProgressWheel loadingProgress;
    private boolean isFirstLoading = false;
    private boolean isViewCreated = false;
    private boolean isInit = false;

    public String TAG="";
    private Toast toast;

    @Override
    public void onAttach(Activity activity)
    {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mContext = activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LogUtils.e("getSimpleName", this.getClass().getSimpleName().toString());
        TAG=this.getClass().getSimpleName().toString();
        initVariable();

    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(@IdRes int id)
    {
        return (T) (contentView.findViewById(id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_base, null);
        // 内容区
        layBody = (FrameLayout) view.findViewById(R.id.layBody);
        stubLoadingFailed = (ViewStub) view.findViewById(R.id.stubLoadingFailed);
        addContentView();
        findViews(view);
        return view;
    }

    /***
     * 设置内容区域d
     */
    public void addContentView()
    {
        int resId = setContentView();
        View layContentView = mInflater.inflate(resId, null);
        if (layContentView == null) {
            return;
        }
//        layContentView.setBackgroundColor(0x00000000);
        contentView = layContentView.findViewById(R.id.content);
        if (contentView == null) {
            contentView = layContentView;
        }
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layContentView.setLayoutParams(layoutParams);
        layBody.addView(layContentView, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint() && !isInit) {
            initData();
            setListener();
            isInit = true;
        }
        else {
            showLoadingView();//这里可以自定义显示其他view
        }
        isViewCreated = true;
    }

    /*该方法每次都在Fragment可见或不可见时调用一次，且在onCreateView方法前调用的*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("123", "isVisibleToUser" + isVisibleToUser);
        if (isVisibleToUser && isViewCreated && !isInit) {
            hideLoadingView();
            isInit = true;
            initData();
            setListener();
        }
    }

    protected abstract void initVariable();

    protected abstract int setContentView();

    protected abstract void findViews(View view);

    protected abstract void initData();

    protected abstract void setListener();



    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(@NonNull View view, @IdRes int id)
    {
        return (T) (view.findViewById(id));
    }


    public void showLoadingFailedView()
    {
        isFirstLoading = true;
        onFragmentLoadingFailed();
    }


    public void onFragmentFirstLoadingNoData()
    {
        if (layLoadingFailed == null) {
            return;
        }
        layLoadingFailed.setVisibility(View.VISIBLE);
        ImageView base_image= (ImageView) layLoadingFailed.findViewById(R.id.base_image);
        TextView base_text= (TextView) layLoadingFailed.findViewById(R.id.base_text);

        base_image.setImageResource(R.mipmap.state_no_data);
        base_text.setText("暂无数据");
        layClickReload.setClickable(false);
        layClickReload.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.INVISIBLE);
        isFirstLoading = true;
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
    }
    /*如果首次加载先失败，就显示失败界面，
    好处是：当首次成功显示内容区后，当再次下拉刷新失败时，就不会显示失败界面，保留原来的界面
   同时，对于一个界面多个接口，之间也不会有影响
   isFirstLoading=false;
   */
    public void onFragmentLoadingFailed()
    {
        if (layLoadingFailed == null || !isFirstLoading) {
            return;
        }
        layClickReload.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.INVISIBLE);
        isFirstLoading = false;
    }

    /*如果首次加载先成功，就显示成功界面，
       对于一个界面多个接口，之间不会有影响
       isFirstLoading=false;
       */
    public void onFragmentLoadingSuccess()
    {

        if (layLoadingFailed == null || !isFirstLoading) {
            return;
        }
        layLoadingFailed.setVisibility(View.GONE);
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
        isFirstLoading = false;
    }

    private void hideLoadingView()
    {
        layLoadingFailed.setVisibility(View.GONE);
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
    }

    private void showLoadingView()
    {
        if (layLoadingFailed == null) {
            layLoadingFailed = (FrameLayout) stubLoadingFailed.inflate();
            layClickReload = (LinearLayout) layLoadingFailed.findViewById(R.id.layClickReload);
            loadingProgress = (ProgressWheel) layLoadingFailed.findViewById(R.id.loadingProgress);
            layClickReload.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    startFragmentLoading();
                }
            });
        }
        layLoadingFailed.setVisibility(View.VISIBLE);
        layClickReload.setVisibility(View.INVISIBLE);
        loadingProgress.setVisibility(View.VISIBLE);

        if (contentView != null) {
            contentView.setVisibility(View.INVISIBLE);
        }

    }

    public void startFragmentLoading()
    {
        showLoadingView();
        isFirstLoading = true;
        onFragmentLoading();

    }

    protected void onFragmentLoading()
    {

    }
    public boolean getisInit(){
        return isInit;
    }

    private void showTextToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(mContext);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(mContext);
    }
}
