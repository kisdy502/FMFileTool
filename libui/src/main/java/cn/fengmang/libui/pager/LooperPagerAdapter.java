package cn.fengmang.libui.pager;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/6.
 */
public class LooperPagerAdapter<T> extends PagerAdapter {

    private boolean canLoop;
    private boolean realCanLoop = true;
    protected List<T> mDatas;

    private ViewPager mViewPager;

    private IHolder holderCreator;
    private LinkedList<ViewPagerHolder> mViewHolderCache = null;
    private LinkedList<ViewPagerHolder> mViewHolderUsedCache = null;
    private OnPageClickListener onItemClickListener;


    private DataSetObservable mRealCanLoopObservable = new DataSetObservable(); //realCanLoop变化模式
    private boolean myNotify = false;

    public LooperPagerAdapter(IHolder holderCreator) {
        this(holderCreator, true);
    }

    public LooperPagerAdapter(IHolder holderCreator, boolean canloop) {
        this.holderCreator = holderCreator;
        mViewHolderCache = new LinkedList<>();
        mViewHolderUsedCache = new LinkedList<>();
        setCanLoop(canloop);
    }

    @Override
    public int getCount() {
        return realCanLoop ? getRealCount() * 100 : getRealCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(position, container);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        ViewPagerHolder viewHolder = null;
        for (int i = mViewHolderUsedCache.size() - 1; i >= 0; i--) {
            viewHolder = mViewHolderUsedCache.get(i);

            if (viewHolder.getPosition() == position) {
                mViewHolderUsedCache.remove(viewHolder);
                break;
            }
            viewHolder = null;
        }
        if (viewHolder != null) {
            mViewHolderCache.add(viewHolder);
        }
    }

    public int getRealCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public View getView(int position, ViewGroup container) {
        ViewPagerHolder holder = null;
        final int realPosition = adapterPostiton2RealDataPosition(position);
        int viewType = holderCreator.getViewType(realPosition);

        for (int i = mViewHolderCache.size() - 1; i >= 0; i--) {
            if (mViewHolderCache.get(i).getViewType() == viewType && mViewHolderCache.get(i).getPosition() == position) {
                holder = mViewHolderCache.get(i);
                mViewHolderCache.remove(holder);
                break;
            }
        }
        if (holder == null) {
            for (int i = mViewHolderCache.size() - 1; i >= 0; i--) {
                if (mViewHolderCache.get(i).getViewType() == viewType) {
                    holder = mViewHolderCache.get(i);
                    mViewHolderCache.remove(holder);
                    break;
                }
            }
        }

        if (holder == null) {
            holder = holderCreator.createView(mViewPager.getContext(), container, realPosition, viewType);
        }

        mViewHolderUsedCache.add(holder);
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(realPosition);
                }
            }
        });

        if (mDatas != null && !mDatas.isEmpty()) {
            if (myNotify || position != holder.getPosition()) {
                // 恢复一下状态
                holderCreator.UpdateUI(container.getContext(), holder, realPosition, mDatas.get(realPosition));
            }
        }

        holder.setPosition(position);
        return holder.getConvertView();
    }

    public int adapterPostiton2RealDataPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        return position % realCount;
    }

    public int realPostiton2AdapterPosition(int curAdapterPosition, int realPosition) {
        if (realCanLoop) {
            int oldRealCur = adapterPostiton2RealDataPosition(curAdapterPosition);
            int toCur = curAdapterPosition + realPosition - oldRealCur;
            return toCur;
        }
        return realPosition >= 0 ? realPosition : 0;
    }

    public int startAdapterPosition(int dataPosition) {
        if (realCanLoop) {
            return getRealCount() * 50 + dataPosition;                  //总数中部位置
        }
        return dataPosition;
    }

    /**
     * 控制AdapterPosition范围
     *
     * @param adapterPosition
     * @return
     */
    public int controlAdapterPosition(int adapterPosition) {
        if (realCanLoop) {
            if (adapterPosition > getRealCount() * 80 || adapterPosition < getRealCount() * 20) {
                return startAdapterPosition(adapterPostiton2RealDataPosition(adapterPosition));
            }
        }
        return adapterPosition;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        boolean loop = canLoop ? (getRealCount() > 1 ? true : false) : false;
        if (realCanLoop ^ loop) {
            realCanLoop = loop;
            mRealCanLoopObservable.notifyChanged();
        }
    }

    public boolean isCanLoop() {
        return canLoop;
    }

    public boolean isRealCanLoop() {
        return realCanLoop;
    }

    public void setOnItemClickListener(OnPageClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setDatas(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    public void setViewPager(LooperViewPager viewPager) {
        this.mViewPager = viewPager;
    }


    public void registerRealCanLoopObserver(DataSetObserver observer) {
        mRealCanLoopObservable.registerObserver(observer);
    }
}
