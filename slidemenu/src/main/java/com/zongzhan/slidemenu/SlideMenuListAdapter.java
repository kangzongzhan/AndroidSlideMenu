package com.zongzhan.slidemenu;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


public abstract class SlideMenuListAdapter <T extends SlideMenuItemViewHolder> extends RecyclerView.Adapter <T> {

    private T openedViewHolder;

    public T getOpenedViewHolder() {
        return openedViewHolder;
    }
    public void setOpenedViewHolder(T t) {
        openedViewHolder = t;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE && openedViewHolder != null) {
                    openedViewHolder.close();
                }
            }
        });
    }

}
