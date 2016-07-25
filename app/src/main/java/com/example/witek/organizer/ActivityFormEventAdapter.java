package com.example.witek.organizer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemDrawableTypes;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemResults;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Witek on 22.05.2016.
 */
public class ActivityFormEventAdapter extends RecyclerView.Adapter<ActivityFormEventAdapter.ActivityFormEventViewHolder>
                                        implements SwipeableItemAdapter<ActivityFormEventAdapter.ActivityFormEventViewHolder>{

    interface Swipeable extends SwipeableItemConstants {

    }
    List<ActivityFormEvent> adapter_list = new ArrayList<>();
    AddActivityFormEvent addActivityFormEvent;


    Context ctx;
    public ActivityFormEventAdapter(List<ActivityFormEvent> adapter_list,Context ctx){
        setHasStableIds(true);
        this.adapter_list = adapter_list;
        this.ctx = ctx;
        addActivityFormEvent = (AddActivityFormEvent) ctx;
    }
    public long getItemId(int position) {
        return adapter_list.get(position).getId(); // need to return stable (= not change even after position changed) value
    }

    public ActivityFormEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_form_event_card,parent,false);
        ActivityFormEventViewHolder activityFormEventViewHolder = new ActivityFormEventViewHolder(view,addActivityFormEvent);
        return activityFormEventViewHolder;
    }

    @Override
    public void onBindViewHolder(final ActivityFormEventViewHolder holder, final int position) {
        holder.activityName.setText(adapter_list.get(position).getName());
        holder.duration.setText(adapter_list.get(position).getDuration());
        holder.burnedKcalPerHour.setText(adapter_list.get(position).getKcalPerUnit());
        holder.burnedKcal.setText("");

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getPosition();
                if(position >= 0) {
                    adapter_list.remove(position);
                    notifyItemRemoved(position);

                    if(adapter_list.size()==0)
                        addActivityFormEvent.makeAddEventItemActive();
                }
            }
        });
        class GenericTextWatcher implements TextWatcher{
            private View v;
            private GenericTextWatcher(View v) {
                this.v = v;
            }
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            public void afterTextChanged(Editable editable) {
                int position = holder.getAdapterPosition();
                if(position >= 0 && position < adapter_list.size()) {
                    switch (v.getId()) {
                        case R.id.activityName: {
                            adapter_list.get(position).setName(
                                    String.valueOf(holder.activityName.getText())
                            );
                            if (adapter_list.get(position).getName().isEmpty()) {
                                addActivityFormEvent.addEventItem.setIcon(R.drawable.ic_add_inactive);
                                addActivityFormEvent.isAddItemEventActive = false;
                            }
                            else {
                                addActivityFormEvent.addEventItem.setIcon(R.drawable.ic_add_active);
                                addActivityFormEvent.isAddItemEventActive = true;
                            }
                            break;
                        }
                        case R.id.duration: {
                            adapter_list.get(position).setDuration(
                                    String.valueOf(holder.duration.getText())
                            );
                            holder.burnedKcal.setText(adapter_list.get(position).countBuriedKcal());
                            break;
                        }
                        case R.id.burnedKcalPerHour: {
                            adapter_list.get(position).setKcalPerUnit(
                                    String.valueOf(holder.burnedKcalPerHour.getText())
                            );
                            holder.burnedKcal.setText(adapter_list.get(position).countBuriedKcal());
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }
        }
        holder.activityName.addTextChangedListener(new GenericTextWatcher(holder.activityName));
        holder.burnedKcalPerHour.addTextChangedListener(new GenericTextWatcher(holder.burnedKcalPerHour));
        holder.duration.addTextChangedListener(new GenericTextWatcher(holder.duration));
        holder.burnedKcal.addTextChangedListener(new GenericTextWatcher(holder.burnedKcal));

    }

    @Override
    public int getItemCount() {
        return adapter_list.size();
    }

    public static class ActivityFormEventViewHolder extends AbstractSwipeableItemViewHolder{
        AddActivityFormEvent addActivityFormEvent;
        EditText activityName;
        EditText duration;
        EditText burnedKcalPerHour;
        EditText burnedKcal;
        ImageButton deleteButton;
        CardView cardView;

        public ActivityFormEventViewHolder(View itemView,AddActivityFormEvent addActivityFormEvent) {
            super(itemView);
            activityName = (EditText) itemView.findViewById(R.id.activityName);
            duration = (EditText) itemView.findViewById(R.id.duration);
            burnedKcalPerHour = (EditText) itemView.findViewById(R.id.burnedKcalPerHour);
            burnedKcal = (EditText) itemView.findViewById(R.id.burnedKcal);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteActivityButton);
            cardView = (CardView) itemView.findViewById(R.id.activityCardView);
            this.addActivityFormEvent = addActivityFormEvent;
        }

        @Override
        public View getSwipeableContainerView() {
            return cardView;
        }
    }
    @Override
    public SwipeResultAction onSwipeItem(ActivityFormEventViewHolder holder, int position, @SwipeableItemResults int result) {
        if (result == Swipeable.RESULT_CANCELED) {
            return new SwipeResultActionDefault();
        } else {
            return new MySwipeResultActionRemoveItem(this, position);
        }
    }

    @Override
    public int onGetSwipeReactionType(ActivityFormEventViewHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public void onSetSwipeBackground(ActivityFormEventViewHolder holder, int position, @SwipeableItemDrawableTypes int type) {
    }

    static class MySwipeResultActionRemoveItem extends SwipeResultActionRemoveItem {
        private ActivityFormEventAdapter adapter;
        private int position;

        public MySwipeResultActionRemoveItem(ActivityFormEventAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected void onPerformAction() {
            if(position >= 0){
                adapter.adapter_list.remove(position);
                adapter.notifyItemRemoved(position);
            }
            if(adapter.adapter_list.size()==0)
                adapter.addActivityFormEvent.makeAddEventItemActive();
        }

        }
    }


