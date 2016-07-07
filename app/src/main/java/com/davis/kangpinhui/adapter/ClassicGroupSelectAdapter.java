package com.davis.kangpinhui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davis.kangpinhui.model.Category;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.SearchResultActivity;
import com.davis.kangpinhui.views.NoScrollGridView;
import com.davis.kangpinhui.views.PinnedHeaderListView;

import java.util.List;

public class ClassicGroupSelectAdapter extends BaseAdapter
        implements OnScrollListener, PinnedHeaderListView.PinnedHeaderAdapter {

    private static final String TAG = ClassicGroupSelectAdapter.class.getSimpleName();

    private Context mContext;
    private List<Category> Categorys;
    private LayoutInflater mLayoutInflater;

    public ClassicGroupSelectAdapter(Context pContext, List<Category> Categorys) {
        mContext = pContext;
        this.Categorys = Categorys;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.fragment_classic_right_item, null);
            viewHolder = new ViewHolder();
            viewHolder.noScrollGridView = (NoScrollGridView) convertView
                    .findViewById(R.id.classic_rootid_right_nogrid);
            viewHolder.title = (TextView) convertView.findViewById(R.id.classic_rootid_right_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 获取数据
        Category itemEntity = (Category) getItem(position);
        if(itemEntity.clist.size()>0){
            if(itemEntity.clist.get(0).id.equals("0")){
                itemEntity.clist.remove(0);
            }
        }
        GridViewAdapter gridViewAdapter = new GridViewAdapter(itemEntity);
        viewHolder.noScrollGridView.setAdapter(gridViewAdapter);


        if (needTitle(position)) {
            // 显示标题并设置内容 
            viewHolder.title.setText(itemEntity.name);
            viewHolder.title.setVisibility(View.VISIBLE);
        } else {
            // 内容项隐藏标题
            viewHolder.title.setVisibility(View.GONE);
        }

        return convertView;
    }

    public int getCount() {
        if (null != Categorys) {
            return Categorys.size();
        }
        return 0;
    }

    public Object getItem(int position) {
        if (null != Categorys && position < getCount()) {
            return Categorys.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).controlPinnedHeader(firstVisibleItem);
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }


    public int getPinnedHeaderState(int position) {
        if (getCount() == 0 || position < 0) {
            return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_GONE;
        }

        if (isMove(position) == true) {
            return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP;
        }

        return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_VISIBLE;
    }


    public void configurePinnedHeader(View headerView, int position, int alpaha) {
        // 设置标题的内容
        Category itemEntity = (Category) getItem(position);
        String headerValue = itemEntity.name;

        if (!TextUtils.isEmpty(headerValue)) {
            TextView headerTextView = (TextView) headerView.findViewById(R.id.classic_rootid_list_item);
            headerTextView.setText(headerValue);
        }

    }

    /**
     * 判断是否需要显示标题
     *
     * @param position
     * @return
     */
    private boolean needTitle(int position) {
        // 第一个肯定是分类
        if (position == 0) {
            return true;
        }

        // 异常处理
        if (position < 0) {
            return false;
        }

        // 当前  // 上一个
        Category currentEntity = (Category) getItem(position);
        Category previousEntity = (Category) getItem(position - 1);
        if (null == currentEntity || null == previousEntity) {
            return false;
        }

        String currentTitle = currentEntity.name;
        String previousTitle = previousEntity.name;
        if (null == previousTitle || null == currentTitle) {
            return false;
        }

        // 当前item分类名和上一个item分类名不同，则表示两item属于不同分类
        if (currentTitle.equals(previousTitle)) {
            return false;
        }

        return true;
    }


    private boolean isMove(int position) {
        // 获取当前与下一项
        Category currentEntity = (Category) getItem(position);
        Category nextEntity = (Category) getItem(position + 1);
        if (null == currentEntity || null == nextEntity) {
            return false;
        }

        // 获取两项header内容
        String currentTitle = currentEntity.name;
        String nextTitle = nextEntity.name;
        if (null == currentTitle || null == nextTitle) {
            return false;
        }

        // 当前不等于下一项header，当前项需要移动了
        if (!currentTitle.equals(nextTitle)) {
            return true;
        }

        return false;
    }

    private class ViewHolder {
        TextView title;
        NoScrollGridView noScrollGridView;
    }

    private class GridViewAdapter extends BaseAdapter {

        private Category tcategory;
        private List<Category> categories;

        public GridViewAdapter(Category category) {
            this.tcategory=category;
            this.categories = category.clist;
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Object getItem(int position) {
            return categories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                convertView = mLayoutInflater.inflate(R.layout.fragment_classic_right_nogrid_item, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView
                        .findViewById(R.id.classic_rootid_right_nogrid_title);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.classic_rootid_right_nogrid_image);
                viewHolder.classic_rootid_right_nogrid_linear= (LinearLayout) convertView.findViewById(R.id.classic_rootid_right_nogrid_linear);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Category category = categories.get(position);

            viewHolder.textView.setText(category.name);
            Glide.with(mContext).load(category.picurl)
                    .placeholder(R.mipmap.img_defualt_bg)
                    .error(R.mipmap.img_defualt_bg)
                    .into(viewHolder.imageView);

           viewHolder.classic_rootid_right_nogrid_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchResultActivity.jumpSearchResultActivity(mContext,"",false,category.id,tcategory.id);
                }
            });

            return convertView;
        }

        final class ViewHolder {
            TextView textView;
            ImageView imageView;
            LinearLayout classic_rootid_right_nogrid_linear;
        }
    }

    public interface CitySelectInterface {
        void selectCity(String name, String id, String defaultCityId);
    }

}
