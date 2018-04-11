package com.homeautogroup.med_manager.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptyRecyclerView extends RecyclerView {

    private final AdapterDataObserver mEmptyObserver = new EmptyRecyclerDataObserver();
    private View mEmptyView;
    private Callback mCallback;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(mEmptyObserver);
        }

        mEmptyObserver.onChanged();
    }

    public void setEmptyView(View mEmptyView) {
        this.mEmptyView = mEmptyView;
    }

    public interface Callback {
        void onEmpty();
    }

    private final class EmptyRecyclerDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                if (adapter.getItemCount() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    EmptyRecyclerView.this.setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    EmptyRecyclerView.this.setVisibility(View.VISIBLE);
                    if (mCallback != null) mCallback.onEmpty();
                }
            }
        }
    }

}