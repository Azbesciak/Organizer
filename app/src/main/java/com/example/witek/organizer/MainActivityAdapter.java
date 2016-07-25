package com.example.witek.organizer;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivityAdapter extends ExpandableRecyclerAdapter<MainActivityAdapter.EventsList> {
    public static final int TYPE_PERSON = 1001;
    EventsAdapter eventsAdapter;
    MainActivity mainActivity;
    CardView summaryLabel;
    EditText selectedDayKcalLimitValue;
    EditText selectedActualKcalValue;

    Map<String,List<EventsList>> eventsMap = new TreeMap<>();
    Map<String,DailyBalance> dailyBalanceMap = new TreeMap<>();
    public MainActivityAdapter(Context context) {
        super(context);
        mainActivity = (MainActivity) context;
        eventsAdapter = new EventsAdapter(context);
        eventsAdapter.open();
        summaryLabel = (CardView) mainActivity.findViewById(R.id.include);
        selectedActualKcalValue = (EditText) mainActivity.findViewById(R.id.actualKcalValue);
        selectedDayKcalLimitValue = (EditText) mainActivity.findViewById(R.id.selectedDayKcalLimitValue);
//        selectedDayKcalLimitValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                        if(selectedDayKcalLimitValue.isFocused())
//                            saveSelectedLimit(mainActivity.getSelectedDate(),String.valueOf(selectedDayKcalLimitValue.getText()));
//            }
//        });
        dailyBalanceMap = eventsAdapter.getDailyBalanceList();
        updateRecycler(mainActivity.getSelectedDate());
        if(dailyBalanceMap.containsKey(mainActivity.getCurrentDay())) {
            selectedDayKcalLimitValue.setText(dailyBalanceMap.get(mainActivity.getCurrentDay()).getKcalLimit());
            selectedActualKcalValue.setText(dailyBalanceMap.get(mainActivity.getCurrentDay()).getReachedKcal());
            mainActivity.kcalLimitToday.setText(dailyBalanceMap.get(mainActivity.getCurrentDay()).getKcalLimit());
            mainActivity.reachedKcalToday.setText(dailyBalanceMap.get(mainActivity.getCurrentDay()).getReachedKcal());
        }
        mainActivity.kcalLimitToday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                saveSelectedLimit(mainActivity.getCurrentDay(),mainActivity.kcalLimitToday.getText().toString());
            }
        });
    }

        public void updateRecycler(String date) {
            setItems(updateAdapter(date));
        }

    public static class EventsList extends ExpandableRecyclerAdapter.ListItem {
        private String title;
        private String kcalAmount = "";
        private Event event;

        public EventsList(String title) {
            super(TYPE_HEADER);
            this.event = null;
            this.title = title;
        }

        public EventsList(Event event) {
            super(TYPE_PERSON);
            this.event = event;
            title = event.getName();
            kcalAmount = event.getKcalBalance();
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        public String getKcalAmount() {
            return kcalAmount;
        }

        public void setKcalAmount(String kcalAmount) {
            this.kcalAmount = kcalAmount;
        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        TextView name;
        TextView kcalAmount;
        CardView cardView;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.item_arrow));

            name = (TextView) view.findViewById(R.id.item_header_name);
            kcalAmount = (TextView) view.findViewById(R.id.kcalBalanceFromTime);
            cardView = (CardView) view.findViewById(R.id.greaterEventCardView);

        }

        public void bind(int position) {
            super.bind(position);
            name.setText(visibleItems.get(position).title);
            kcalAmount.setText(visibleItems.get(position).kcalAmount);
        }
    }

    public class LowerItemViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        TextView name;
        TextView kcalAmount;
        CardView cardView;
        public LowerItemViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_name);
            kcalAmount = (TextView) view.findViewById(R.id.kcalAmount);
            cardView = (CardView) view.findViewById(R.id.lowerEventCardView);

        }

        public void bind(int position) {
            name.setText(visibleItems.get(position).title);
            kcalAmount.setText(visibleItems.get(position).kcalAmount);
            if(visibleItems.get(position).getEvent() instanceof ActivityFormEvent) {
                cardView.setCardBackgroundColor(Color.rgb(251, 233, 231));
            } else if(visibleItems.get(position).getEvent() instanceof FoodFormEvent) {
                cardView.setCardBackgroundColor(Color.rgb(232, 245, 233));
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:{
                return new HeaderViewHolder(inflate(R.layout.item_header, parent));
            }
            case TYPE_PERSON:
            default:
                return new LowerItemViewHolder(inflate(R.layout.item_event, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:{
                ((HeaderViewHolder) holder).bind(position);
                break;
            }
            case TYPE_PERSON:
            default:
                ((LowerItemViewHolder) holder).bind(position);
                break;
        }
    }
    public List<EventsList> updateAdapter(String date){

        if((!MainActivity.isChagnedMap.containsKey(date) || MainActivity.isChagnedMap.get(date)) ||
                !eventsMap.containsKey(date) || eventsMap.get(date) == null) {
            Map<String, List<Event>> map = eventsAdapter.getDayEvents(date);

            List<EventsList> list = new ArrayList<>();
            int i = 0;
            double totalBalance = 0;
            for (String s : map.keySet()) {
                list.add(new EventsList(s));
                double balance = 0;
                int headerPosition = i;
                i++;
                for (Event e : map.get(s)) {
                    list.add(new EventsList(e));
                    i++;
                    double temp = 0;
                    try {
                        if(e instanceof FoodFormEvent) {
                            temp = Double.valueOf(e.getKcalBalance());

                        }else if (e instanceof  ActivityFormEvent) {
                            temp = -Double.valueOf(e.getKcalBalance());
                        }
                    } catch (Exception exc) {}
                    balance += temp;
                }
                if(balance != 0) {
                    list.get(headerPosition).setKcalAmount(String.valueOf(balance));
                    totalBalance += balance;
                }

            }

            String totalBalanceString = String.valueOf(totalBalance);
            saveReachedKcal(date,totalBalanceString);

            eventsMap.put(date, list);
            MainActivity.isChagnedMap.put(date,false);
//            Toast.makeText(mainActivity.getApplicationContext(),"pobieram " + date, Toast.LENGTH_SHORT).show();
            return list;
        }else {
            selectedActualKcalValue.setText(dailyBalanceMap.get(date).getReachedKcal());
            selectedDayKcalLimitValue.setText(dailyBalanceMap.get(date).getKcalLimit());
            return eventsMap.get(date);
        }

    }

    public void saveSelectedLimit (String date, String limit) {
        if( !dailyBalanceMap.containsKey(date)) {
            eventsAdapter.insertDailyBalance(new DailyBalance(date,limit));
            dailyBalanceMap.put(date, new DailyBalance(date,limit));
        } else {
            DailyBalance temp = dailyBalanceMap.get(date);
            temp.setKcalLimit(limit);
            eventsAdapter.updateDailyBalance(temp);
            dailyBalanceMap.get(date).setKcalLimit(limit);
        }
        if(mainActivity.getCurrentDay().equals(mainActivity.getSelectedDate())) {
            mainActivity.kcalLimitToday.setText(limit);
            selectedDayKcalLimitValue.setText(limit);
        }
    }
    public  void saveReachedKcal (String date, String reachedKcal) {
        if( !dailyBalanceMap.containsKey(date)) {
            DailyBalance temp = new DailyBalance(date,"");
            temp.setReachedKcal(reachedKcal);
            eventsAdapter.insertDailyBalance(temp);
            dailyBalanceMap.put(date,temp);
        } else {
            DailyBalance temp = dailyBalanceMap.get(date);
            temp.setReachedKcal(reachedKcal);
            eventsAdapter.updateDailyBalance(temp);
//            if() {
//                Toast.makeText(mainActivity.getApplicationContext(),"aktualizuje " + date,Toast.LENGTH_SHORT).show();
//            };
            dailyBalanceMap.get(date).setReachedKcal(reachedKcal);
        }
        if(mainActivity.getCurrentDay().equals(mainActivity.getSelectedDate())) {
            mainActivity.reachedKcalToday.setText(reachedKcal);
            selectedActualKcalValue.setText(reachedKcal);
        }
    }
    public void updateDailyLimit(String date) {
        if(dailyBalanceMap.containsKey(date)){
            selectedDayKcalLimitValue.setText(dailyBalanceMap.get(date).getKcalLimit());
            selectedActualKcalValue.setText(dailyBalanceMap.get(date).getReachedKcal());
        }
//        if(date.equals(mainActivity.getCurrentDay())) {
//            mainActivity.
//        }

    }

   // public void get
//    private String getSelectedLimit(String date) {
//        String temp = String.valueOf(selectedDayKcalLimitValue.getText());
//        if(temp.isEmpty()) {
//            dailyBalanceMap.get(date);
//        }
//    }

}
