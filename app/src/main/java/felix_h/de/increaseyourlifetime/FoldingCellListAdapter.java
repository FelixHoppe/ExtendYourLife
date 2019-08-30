package felix_h.de.increaseyourlifetime;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.ramotion.fluidslider.FluidSlider;
import com.ramotion.foldingcell.FoldingCell;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FoldingCellListAdapter extends ArrayAdapter<Item> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private AdapterView.OnItemClickListener defaultRequestBtnClickListener;
    private Context context;
    final int max = 5;
    final int min = 0;
    final int total = max - min;

    public FoldingCellListAdapter(Context context, List<Item> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get item for selected view
        Item item = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);
            // binding view parts to view holder

            viewHolder.in_hours = cell.findViewById(R.id.in_hours);
            viewHolder.today_influence = cell.findViewById(R.id.today_influence);
            viewHolder.today_influence_last = cell.findViewById(R.id.today_influence_last);
            viewHolder.week_influence = cell.findViewById(R.id.week_influence);
            viewHolder.week_influence_last = cell.findViewById(R.id.week_influence_last);
            viewHolder.all_time_influence = cell.findViewById(R.id.all_time_influence);
            viewHolder.all_time_influence_last = cell.findViewById(R.id.all_time_influence_last);
            viewHolder.fluidSlider = cell.findViewById(R.id.content_slider);
            viewHolder.question = cell.findViewById(R.id.above_slider);
            viewHolder.title = cell.findViewById(R.id.cell_name);
            viewHolder.graph = cell.findViewById(R.id.graph);
            viewHolder.bg = cell.findViewById(R.id.cell_title_layout_RL); //Verändert
            viewHolder.price = cell.findViewById(R.id.title_price);
            viewHolder.time = cell.findViewById(R.id.title_time_label);
            viewHolder.date = cell.findViewById(R.id.title_date_label);
            viewHolder.fromAddress = cell.findViewById(R.id.title_from_address);
            viewHolder.requestsCount = cell.findViewById(R.id.title_requests_count);
            viewHolder.pledgePrice = cell.findViewById(R.id.title_pledge);
            viewHolder.contentRequestBtn = cell.findViewById(R.id.content_request_btn);
            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (null == item)
            return cell;


        // bind data from selected element to view through view holder

        String prf = "";
        switch (position){
            case 0:
                prf = "sport";
                break;
            case 1:
                prf = "nutrition";
                break;
            case 2:
                prf = "smoking";
                break;
            case 3:
                prf = "outside";
                break;
            case 4:
                prf = "mood";
        }
        SharedPreferences prefs = context.getSharedPreferences(prf, Context.MODE_PRIVATE);

        viewHolder.title.setText(item.getFromAddress());
        viewHolder.question.setText(item.getQuestion());
        viewHolder.graph.removeAllSeries();
        viewHolder.graph.addSeries(item.getSeries());
        viewHolder.bg.setBackground(item.getBackground());

        viewHolder.price.setText(item.getPrice());

        viewHolder.time.setText(item.getTime());
        viewHolder.fromAddress.setText(item.getFromAddress());
        viewHolder.requestsCount.setText(item.getRequestsCount());
        viewHolder.pledgePrice.setText(item.getPledgePrice());

        viewHolder.all_time_influence.setText((int) prefs.getFloat("all_time", 0) + " " + context.getString(R.string.minutes));
        if((int) prefs.getFloat("all_time_added", 1) != prefs.getFloat("yesterday", 0))
            viewHolder.today_influence.setText((int) prefs.getFloat("all_time_added", 0) + " " + context.getString(R.string.minutes));
        else
            viewHolder.today_influence.setText("0 " + context.getString(R.string.minutes));
        viewHolder.today_influence_last.setText(context.getString(R.string.yesterday) + ":" + prefs.getFloat("yesterday", 0) + " " + context.getString(R.string.minutes));
        viewHolder.week_influence.setText((int) prefs.getFloat("time_this_week", 0) + " " + context.getString(R.string.minutes));

        viewHolder.in_hours.setText(String.format("%.1f", (prefs.getFloat("all_time", 0) / 60)) + " " + context.getString(R.string.hours));



        viewHolder.fluidSlider.setBeginTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                viewHolder.question.setVisibility(View.INVISIBLE);
                return Unit.INSTANCE;
            }
        });

        viewHolder.fluidSlider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                viewHolder.question.setVisibility(View.VISIBLE);

                int state = Integer.parseInt(viewHolder.fluidSlider.getBubbleText());

                float gained_minutes = 0;

                SharedPreferences temp_prf = context.getSharedPreferences("time", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                long last_save = temp_prf.getLong("last_save_2", 0);
                if (System.currentTimeMillis() - last_save > 3600000 * 24)
                {
                    editor.putLong("last_save_2", System.currentTimeMillis());
                    editor.putInt("daily_avg", temp_prf.getInt("daily_avg", 0) + 1);
                }
                editor.apply();

                switch (position){
                    case 0:
                        switch (state){
                            case 0:
                                gained_minutes = -75;
                                break;
                            case 1:
                                gained_minutes = 10;
                                break;
                            case 2:
                                gained_minutes = 90;
                                break;
                            case 3:
                                gained_minutes = 140;
                                break;
                            case 4:
                                gained_minutes = 150;
                                break;
                        }
                        update(viewHolder, "sport", gained_minutes, item);
                        break;
                    case 1:
                        switch (state){
                            case 0:
                                gained_minutes = -105;
                                break;
                            case 1:
                                gained_minutes = -65;
                                break;
                            case 2:
                                gained_minutes = 15;
                                break;
                            case 3:
                                gained_minutes = 85;
                                break;
                            case 4:
                                gained_minutes = 130;
                                break;
                        }
                        update(viewHolder, "nutrition", gained_minutes, item);
                        break;
                    case 2:
                        switch (state){
                            case 0:
                                gained_minutes = 65;
                                break;
                            case 1:
                                gained_minutes = -50;
                                break;
                            case 2:
                                gained_minutes = -105;
                                break;
                            case 3:
                                gained_minutes = -160;
                                break;
                            case 4:
                                gained_minutes = -210;
                                break;
                        }
                        update(viewHolder, "smoking", gained_minutes, item);
                    break;
                    case 3:
                        switch (state){
                            case 0:
                                gained_minutes = -35;
                                break;
                            case 1:
                                gained_minutes = -5;
                                break;
                            case 2:
                                gained_minutes = 35;
                                break;
                            case 3:
                                gained_minutes = 55;
                                break;
                            case 4:
                                gained_minutes = 70;
                                break;
                        }
                        update(viewHolder, "outside", gained_minutes, item);
                        break;
                    case 4:
                        switch (state){
                            case 0:
                                gained_minutes = -75;
                                break;
                            case 1:
                                gained_minutes = -10;
                                break;
                            case 2:
                                gained_minutes = 20;
                                break;
                            case 3:
                                gained_minutes = 95;
                                break;
                            case 4:
                                gained_minutes = 185;
                                break;
                        }
                        update(viewHolder, "mood", gained_minutes, item);
                        break;
                }
                viewHolder.pledgePrice.setText(item.getPledgePrice());
                return Unit.INSTANCE;
            }
        });

        // Java 8 lambda
        viewHolder.fluidSlider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (min + total * pos));
            if (Integer.parseInt(value) != 5)
                viewHolder.fluidSlider.setBubbleText(value);
            else
                viewHolder.fluidSlider.setBubbleText("4");

            return Unit.INSTANCE;
        });

        viewHolder.fluidSlider.setPosition((float) 1/6*3);
        viewHolder.fluidSlider.setStartText(String.valueOf(min));
        viewHolder.fluidSlider.setEndText(String.valueOf(max - 1));

        // set custom btn handler for list item from that item
        if (item.getRequestBtnClickListener() != null) {
            viewHolder.contentRequestBtn.setOnClickListener(item.getRequestBtnClickListener());
        } else {
            // (optionally) add "default" handler if no handler found in item
        }

        return cell;
    }

    // simple methods for register cell state changes
    public boolean registerToggle(int position) {
        boolean unfolded = unfoldedIndexes.contains(position);
        if (unfolded)
            registerFold(position);
        else
            registerUnfold(position);
        return unfolded;
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public AdapterView.OnItemClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(AdapterView.OnItemClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }

    private void update(ViewHolder viewHolder, String preference, float gained_minutes, Item item) {
        for (int i = 0; i < 2; i++) {

            SharedPreferences prefs = context.getSharedPreferences(preference, context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            String[] states =
                    {
                            prefs.getString("0_days_ago", ":0"),
                            prefs.getString("1_days_ago", ":0"),
                            prefs.getString("2_days_ago", ":0"),
                            prefs.getString("3_days_ago", ":0"),
                            prefs.getString("4_days_ago", ":0"),
                    };
            DataPoint[] dp = new DataPoint[states.length];
            int j = states.length-1;
            for (int z = 0; z < states.length; z++) {
                dp[z] = new DataPoint(z+1, (int) Float.parseFloat(states[j].split(":")[1]));
                j--;
            }
            LineGraphSeries series0 = new LineGraphSeries<>(dp);
            series0.setDrawBackground(true);
            series0.setDrawDataPoints(true);

            viewHolder.graph.removeAllSeries();
            viewHolder.graph.addSeries(series0);

            DateFormat df = new SimpleDateFormat("EEE d MMM");
            String date = df.format(Calendar.getInstance().getTime());
            editor.putString("last_time", date);
            item.setTime(prefs.getString("last_time", context.getString(R.string.never)));
            viewHolder.time.setText(item.getTime());


            Float at = prefs.getFloat("all_time", 0) / 60;
            if(at < 0)
                item.setPrice(String.format("%.1f", (at)));
            else
                item.setPrice("+" + String.format("%.1f", (at)));

            float lastTime = prefs.getFloat("all_time_added", 0f);

            String[] last_week = new String[7];
            for (int x = 0; x < last_week.length; x++) {
                last_week[x] = prefs.getString(x + "_days_ago", null);
            }

            float all_time_minutes;
            // check if next day

            long last_save = prefs.getLong("last_save", 0);

            if (System.currentTimeMillis() - last_save > 3600000 * 24){
                if (last_save != 0)
                    editor.putFloat("yesterday", Float.parseFloat(prefs.getString("0_days_ago", ":0").split(":")[1]));
                else
                    editor.putFloat("yesterday", 0);

                editor.putLong("last_save", System.currentTimeMillis());
                editor.putString("1_days_ago", last_week[0]);
                editor.putString("2_days_ago", last_week[1]);
                editor.putString("3_days_ago", last_week[2]);
                editor.putString("4_days_ago", last_week[3]);
                editor.putString("5_days_ago", last_week[4]);
                editor.putString("6_days_ago", last_week[5]);

                float prev_all_time = prefs.getFloat("all_time", 0f);
                all_time_minutes = prev_all_time + gained_minutes;

                if(gained_minutes < 0)
                    editor.putInt("sum_neg", (int) (prefs.getInt("sum_neg", 0) - gained_minutes));
                else
                    editor.putInt("sum_pos", (int) (prefs.getInt("sum_pos", 0) + gained_minutes));

            } else {
                float prev_all_time = prefs.getFloat("all_time", 0f);
                //TODO all_time_minutes und sum_pos / sum_neg Berechnen
                all_time_minutes = prev_all_time + gained_minutes - lastTime;

                float prev_sum = prefs.getFloat("sum_added", 0);

                if(gained_minutes < 0)
                    if(prev_sum > 0)
                    {
                        editor.putInt("sum_neg", (int) (prefs.getInt("sum_neg", 0) - gained_minutes ));
                        editor.putInt("sum_pos", (int) (prefs.getInt("sum_pos", 0) - prev_sum));
                    }
                    else
                        editor.putInt("sum_neg", (int) ((prefs.getInt("sum_neg", 0) + prev_sum) - gained_minutes));
                else
                    if(prev_sum > 0)
                        editor.putInt("sum_pos", (int) (prefs.getInt("sum_pos", 0) - prev_sum + gained_minutes ));
                    else
                    {
                        editor.putInt("sum_pos", (int) (prefs.getInt("sum_pos", 0) + gained_minutes ));
                        editor.putInt("sum_neg", (int) (prefs.getInt("sum_neg", 0) + prev_sum));
                    }
            }
            editor.putFloat("sum_added", gained_minutes);


            last_week[0] = System.currentTimeMillis() + ":" + gained_minutes;
            editor.putString("0_days_ago", last_week[0]);

            int frequency = 0;
            float time_this_week = 0;
            for (String s : last_week) {
                if (s != null) {
                    String[] separated = s.split(":");
                    Calendar c1 = Calendar.getInstance(); // today
                    c1.add(Calendar.DAY_OF_YEAR, -7); // 7 days ago
                    Calendar c2 = Calendar.getInstance();
                    c2.setTimeInMillis(Long.parseLong(separated[0]));

                    if (c1.before(c2))
                    {
                        frequency++;
                        time_this_week += Float.parseFloat(separated[1]);
                    }
                }
            }
            editor.putFloat("time_this_week", time_this_week);
            editor.putFloat("all_time_added", gained_minutes);
            editor.putFloat("all_time", all_time_minutes);
            editor.putInt("frequency", frequency);

            item.setPledgePrice(frequency + " / " + context.getString(R.string.week));
            viewHolder.price.setText(item.getPrice());

            String atm_digit = "";
            String g_digit = "";
            String y_digit = "";
            String w_digit = "";

            if(all_time_minutes >= 0)
                atm_digit = "+";

            if(gained_minutes >= 0)
                g_digit = "+";

            if(prefs.getFloat("yesterday", 0f) >= 0)
                y_digit = "+";

            if(time_this_week >= 0)
                w_digit = "+";

            viewHolder.all_time_influence.setText(atm_digit + (int) all_time_minutes + " " + context.getString(R.string.minutes));
            viewHolder.today_influence.setText(g_digit + (int) gained_minutes + " " + context.getString(R.string.minutes));
            viewHolder.today_influence_last.setText(context.getString(R.string.yesterday) + ":" + y_digit + (int) prefs.getFloat("yesterday", 0f) + " " + context.getString(R.string.minutes));
            viewHolder.week_influence.setText(w_digit + (int) time_this_week + " " + context.getString(R.string.minutes));
            viewHolder.in_hours.setText(atm_digit + String.format("%.1f", (all_time_minutes / 60)) + " " + context.getString(R.string.hours));
            viewHolder.time.setText(item.getTime());
            editor.apply();
        }

    }


    // View lookup cache
    private static class ViewHolder {
        GraphView graph;
        RelativeLayout bg;//Verändert
        TextView price;
        TextView contentRequestBtn;
        TextView pledgePrice;
        TextView fromAddress;
        TextView requestsCount;
        TextView date;
        TextView time;
        TextView title;
        TextView question;
        FluidSlider fluidSlider;
        TextView today_influence;
        TextView today_influence_last;
        TextView week_influence;
        TextView week_influence_last;
        TextView all_time_influence;
        TextView all_time_influence_last;
        TextView in_hours;

    }
}