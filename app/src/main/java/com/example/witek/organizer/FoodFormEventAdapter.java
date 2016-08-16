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
public class FoodFormEventAdapter
        extends RecyclerView.Adapter<FoodFormEventAdapter.FoodFormEventViewHolder>
        implements SwipeableItemAdapter<FoodFormEventAdapter.FoodFormEventViewHolder> {

    List<FoodFormEvent> adapter_list = new ArrayList<>();
    AddFoodFormEvent addFoodFormEvent;
    Context ctx;


    public FoodFormEventAdapter (List<FoodFormEvent> adapter_list, Context ctx) {
        setHasStableIds(true);
        this.adapter_list = adapter_list;
        this.ctx = ctx;
        addFoodFormEvent = (AddFoodFormEvent) ctx;
    }

    public long getItemId(int position) {
        return adapter_list.get(position)
                           .getId(); // need to return stable (= not change even after position
        // changed) value
    }

    interface Swipeable extends SwipeableItemConstants {

    }

    public FoodFormEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.food_form_event_card, parent, false);
        FoodFormEventViewHolder foodFormEventViewHolder = new FoodFormEventViewHolder(view,
                                                                                      addFoodFormEvent);
        return foodFormEventViewHolder;
    }

    @Override
    public void onBindViewHolder(final FoodFormEventViewHolder holder, final int position) {
        holder.foodName.setText(adapter_list.get(position).getName());
        holder.weight.setText(adapter_list.get(position).getWeight());
        holder.kcalPer100g.setText(adapter_list.get(position).getKcalPerUnit());
        holder.gainedKcal.setText("");

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position >= 0) {
                    adapter_list.remove(position);
                    notifyItemRemoved(position);

                    if (adapter_list.size() == 0) {
                        addFoodFormEvent.makeAddEventItemActive();
                    }
                }
            }
        });
        class GenericTextWatcher implements TextWatcher {
            private View v;

            private GenericTextWatcher(View v) {
                this.v = v;
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            public void afterTextChanged(Editable editable) {
                int position = holder.getAdapterPosition();
                if (position >= 0 && position < adapter_list.size()) {
                    switch (v.getId()) {
                        case R.id.foodName: {
                            adapter_list.get(position).setName(
                                    String.valueOf(holder.foodName.getText())
                            );
                            if (adapter_list.get(position).getName().isEmpty()) {
                                addFoodFormEvent.addEventItem.setIcon(R.drawable.ic_add_inactive);
                                AddFoodFormEvent.isAddItemEventActive = false;
                            } else {
                                addFoodFormEvent.addEventItem.setIcon(R.drawable.ic_add_active);
                                AddFoodFormEvent.isAddItemEventActive = true;
                            }
                            break;
                        }
                        case R.id.weight: {
                            adapter_list.get(position).setWeight(
                                    String.valueOf(holder.weight.getText())
                            );
                            holder.gainedKcal.setText(adapter_list.get(position).countGainedKcal());
                            break;
                        }
                        case R.id.kcalPer100g: {
                            adapter_list.get(position).setKcalPerUnit(
                                    String.valueOf(holder.kcalPer100g.getText())
                            );
                            holder.gainedKcal.setText(adapter_list.get(position).countGainedKcal());
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }
        }
        holder.foodName.addTextChangedListener(new GenericTextWatcher(holder.foodName));
        holder.kcalPer100g.addTextChangedListener(new GenericTextWatcher(holder.kcalPer100g));
        holder.weight.addTextChangedListener(new GenericTextWatcher(holder.weight));
        holder.gainedKcal.addTextChangedListener(new GenericTextWatcher(holder.gainedKcal));

    }

    @Override
    public int getItemCount() {
        return adapter_list.size();
    }

    @Override
    public SwipeResultAction onSwipeItem(FoodFormEventViewHolder holder, int position,
                                         @SwipeableItemResults int result) {
        if (result == Swipeable.RESULT_CANCELED) {
            return new SwipeResultActionDefault();
        } else {
            return new MySwipeResultActionRemoveItem(this, position);
        }
    }

    public static class FoodFormEventViewHolder extends AbstractSwipeableItemViewHolder {
        AddFoodFormEvent addFoodFormEvent;
        EditText foodName;
        EditText weight;
        EditText kcalPer100g;
        EditText gainedKcal;
        ImageButton deleteButton;
        CardView cardView;

        public FoodFormEventViewHolder(View itemView, AddFoodFormEvent addFoodFormEvent) {
            super(itemView);
            foodName = (EditText) itemView.findViewById(R.id.foodName);
            weight = (EditText) itemView.findViewById(R.id.weight);
            kcalPer100g = (EditText) itemView.findViewById(R.id.kcalPer100g);
            gainedKcal = (EditText) itemView.findViewById(R.id.gainedKcal);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteFoodButton);
            cardView = (CardView) itemView.findViewById(R.id.foodCardView);
            this.addFoodFormEvent = addFoodFormEvent;
        }

        @Override
        public View getSwipeableContainerView() {
            return cardView;
        }
    }

    @Override
    public int onGetSwipeReactionType(FoodFormEventViewHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public void onSetSwipeBackground(FoodFormEventViewHolder holder, int position,
                                     @SwipeableItemDrawableTypes int type) {
    }

    static class MySwipeResultActionRemoveItem extends SwipeResultActionRemoveItem {
        private FoodFormEventAdapter adapter;
        private int position;

        public MySwipeResultActionRemoveItem(FoodFormEventAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected void onPerformAction() {
            if (position >= 0) {
                adapter.adapter_list.remove(position);
                adapter.notifyItemRemoved(position);
            }
            if (adapter.adapter_list.size() == 0) {
                adapter.addFoodFormEvent.makeAddEventItemActive();
            }
        }

    }
}


