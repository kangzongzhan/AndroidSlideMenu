package com.zongzhan.slidemenu;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SlideMenuItemViewHolder extends RecyclerView.ViewHolder implements SlideMenuLayout.SlideMenuListener {

        private SlideMenuListAdapter adapter;

        public SlideMenuItemViewHolder(SlideMenuListAdapter adapter, View itemView) {
            super(itemView);
            ((SlideMenuLayout)itemView).setSlideMenuListener(this);
            this.adapter = adapter;
        }

        @Override
        public void onOpenChanged(boolean open) {
            if (open) {
                if (adapter.getOpenedViewHolder() != null) {
                    adapter.getOpenedViewHolder().close();
                }
                adapter.setOpenedViewHolder(this);
            } else {
                adapter.setOpenedViewHolder(null);
            }
        }

    @Override
    public void onStateChanged(int state) {
        if (state != SlideMenuLayout.SCROLL_STATE_IDLE && adapter.getOpenedViewHolder() != null && adapter.getOpenedViewHolder() != this) {
            adapter.getOpenedViewHolder().close();
        }
    }

    public void close() {
            SlideMenuLayout layout = (SlideMenuLayout) itemView;
            layout.close();
        }
    }