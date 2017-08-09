package com.utilfreedom.brainmath.Services;


import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.utilfreedom.brainmath.model.Room;

import java.util.List;

public class DiffCallback extends DiffUtil.Callback{

    private final List<Room> oldRooms;
    private final List<Room> newRooms;

    public DiffCallback(List<Room> oldRooms, List<Room> newRooms) {
        this.oldRooms = oldRooms;
        this.newRooms = newRooms;
    }

    @Override
    public int getOldListSize() {
        return oldRooms.size();
    }

    @Override
    public int getNewListSize() {
        return newRooms.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldRooms.get(oldItemPosition).getUID().matches(newRooms.get(newItemPosition).getUID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldRooms.get(oldItemPosition).equals(newRooms.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}