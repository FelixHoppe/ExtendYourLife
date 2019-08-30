package felix_h.de.increaseyourlifetime;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.ramotion.fluidslider.FluidSlider;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Simple POJO model for example
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Item {

    private String price;
    private String pledgePrice;
    private String fromAddress;
    private String toAddress;
    private String requestsCount;
    private String date;
    private String time;
    private Drawable bg;
    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
    private String question;
    private FluidSlider slider;
    private TextView[] influences;

    private View.OnClickListener requestBtnClickListener;

    public Item() {
    }

    public Item(TextView[] influences, FluidSlider slider, String question, LineGraphSeries<DataPoint> series, GraphView graph, Drawable bg, String price, String pledgePrice, String fromAddress, String toAddress, String requestsCount, String date, String time) {
        this.series = series;
        this.graph = graph;
        this.bg = bg;
        this.price = price;
        this.pledgePrice = pledgePrice;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.requestsCount = requestsCount;
        this.date = date;
        this.time = time;
        this.question = question;
        this.slider = slider;
        this.influences = influences;
    }

    public LineGraphSeries<DataPoint> getSeries() { return series; }

    public Drawable getBackground() { return bg; }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPledgePrice() {
        return pledgePrice;
    }

    public void setPledgePrice(String pledgePrice) {
        this.pledgePrice = pledgePrice;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getRequestsCount() {
        return requestsCount;
    }

    public void setRequestsCount(String requestsCount) {
        this.requestsCount = requestsCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public View.OnClickListener getRequestBtnClickListener() {
        return requestBtnClickListener;
    }

    public void setRequestBtnClickListener(View.OnClickListener requestBtnClickListener) {
        this.requestBtnClickListener = requestBtnClickListener;
    }

    public FluidSlider getSlider() { return slider; }

    public String getQuestion() { return question; }

    public TextView[] getInfluences() { return influences; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (requestsCount != item.requestsCount) return false;
        if (price != null ? !price.equals(item.price) : item.price != null) return false;
        if (pledgePrice != null ? !pledgePrice.equals(item.pledgePrice) : item.pledgePrice != null)
            return false;
        if (fromAddress != null ? !fromAddress.equals(item.fromAddress) : item.fromAddress != null)
            return false;
        if (toAddress != null ? !toAddress.equals(item.toAddress) : item.toAddress != null)
            return false;
        if (date != null ? !date.equals(item.date) : item.date != null) return false;
        return !(time != null ? !time.equals(item.time) : item.time != null);

    }


    @Override
    public int hashCode() {
        int result = price != null ? price.hashCode() : 0;
        result = 31 * result + (pledgePrice != null ? pledgePrice.hashCode() : 0);
        result = 31 * result + (fromAddress != null ? fromAddress.hashCode() : 0);
        result = 31 * result + (toAddress != null ? toAddress.hashCode() : 0);
        result = 31 * result + (requestsCount != null ? requestsCount.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    /**
     * @return List of elements prepared for tests
     */
    public static ArrayList<Item> getTestingList(Context context, GraphView graph, FluidSlider slider, TextView[] influences) {
        Drawable d1 = ContextCompat.getDrawable(context, R.drawable.sport);
        Drawable d2 = ContextCompat.getDrawable(context, R.drawable.nutrition);
        Drawable d3 = ContextCompat.getDrawable(context, R.drawable.smoking);
        Drawable d4 = ContextCompat.getDrawable(context, R.drawable.outside);
        Drawable d5 = ContextCompat.getDrawable(context, R.drawable.mood);

        //Für jeden intro_graph series erstellen (immer Array aus sharedPreferences auslesen)


        String[] questions =
                {context.getString(R.string.sport_today),
                        context.getString(R.string.nutrition_today),
                        context.getString(R.string.smoking_today),
                        context.getString(R.string.going_outside_today),
                        context.getString(R.string.mood_today)};

        String[] dates = new String[5];
        String[] direction = new String[5];
        int[] frequency = new int[5];

        SharedPreferences prf;
        prf = context.getSharedPreferences("sport", context.MODE_PRIVATE);
        frequency[0] = prf.getInt("frequency", 0);
        dates[0] = prf.getString("last_time", context.getString(R.string.never));
        Float all_time = prf.getFloat("all_time", 0) / 60;
        if (all_time > 0)
            direction[0] = "+" + String.format("%.1f", all_time);
        else
            direction[0] = String.format("%.1f", all_time);

        String[] states1 =
                {
                        prf.getString("0_days_ago", ":0:0"),
                        prf.getString("1_days_ago", ":0:0"),
                        prf.getString("2_days_ago", ":0:0"),
                        prf.getString("3_days_ago", ":0:0"),
                        prf.getString("4_days_ago", ":0:0"),
                };
        DataPoint[] dp = new DataPoint[states1.length];
        int j = states1.length-1;
        for (int i = 0; i < states1.length; i++) {
            dp[i] = new DataPoint(i+1, (int) Float.parseFloat(states1[j].split(":")[1]));
            j--;
        }
        LineGraphSeries series0 = new LineGraphSeries<>(dp);
        series0.setDrawBackground(true);
        series0.setAnimated(true);
        series0.setDrawDataPoints(true);


        prf = context.getSharedPreferences("nutrition", context.MODE_PRIVATE);
        frequency[1] = prf.getInt("frequency", 0);
        dates[1] = prf.getString("last_time", context.getString(R.string.never));
        all_time = prf.getFloat("all_time", 0)/60;
        if(all_time > 0)
            direction[1] = "+" + String.format("%.1f", all_time);
        else
            direction[1] = String.format("%.1f", all_time);

        String[] states2 =
                {
                        prf.getString("0_days_ago", ":0:0"),
                        prf.getString("1_days_ago", ":0:0"),
                        prf.getString("2_days_ago", ":0:0"),
                        prf.getString("3_days_ago", ":0:0"),
                        prf.getString("4_days_ago", ":0:0"),
                };
        DataPoint[] dp2 = new DataPoint[states2.length];
        j = states2.length-1;
        for (int i = 0; i < states1.length; i++) {
            dp2[i] = new DataPoint(i+1, (int) Float.parseFloat(states2[j].split(":")[1]));
            j--;
        }
        LineGraphSeries series1 = new LineGraphSeries<>(dp2);
        series1.setDrawBackground(true);
        series1.setAnimated(true);
        series1.setDrawDataPoints(true);
        

        prf = context.getSharedPreferences("smoking", context.MODE_PRIVATE);
        frequency[2] = prf.getInt("frequency", 0);
        dates[2] = prf.getString("last_time", context.getString(R.string.never));
        all_time = prf.getFloat("all_time", 0)/60;
        if(all_time > 0)
            direction[2] = "+" + String.format("%.1f", all_time);
        else
            direction[2] = String.format("%.1f", all_time);

        String[] states3 =
                {
                        prf.getString("0_days_ago", ":0:0"),
                        prf.getString("1_days_ago", ":0:0"),
                        prf.getString("2_days_ago", ":0:0"),
                        prf.getString("3_days_ago", ":0:0"),
                        prf.getString("4_days_ago", ":0:0"),
                };
        DataPoint[] dp3 = new DataPoint[states3.length];
        j = states3.length-1;
        for (int i = 0; i < states3.length; i++) {
            dp3[i] = new DataPoint(i+1, (int) Float.parseFloat(states3[j].split(":")[1]));
            j--;
        }
        LineGraphSeries series2 = new LineGraphSeries<>(dp3);
        series2.setDrawBackground(true);
        series2.setAnimated(true);
        series2.setDrawDataPoints(true);


        prf = context.getSharedPreferences("outside", context.MODE_PRIVATE);
        frequency[3] = prf.getInt("frequency", 0);
        dates[3] = prf.getString("last_time", context.getString(R.string.never));
        all_time = prf.getFloat("all_time", 0)/60;
        if(all_time > 0)
            direction[3] = "+" + String.format("%.1f", all_time);
        else
            direction[3] = String.format("%.1f", all_time);

        String[] states4 =
                {
                        prf.getString("0_days_ago", ":0:0"),
                        prf.getString("1_days_ago", ":0:0"),
                        prf.getString("2_days_ago", ":0:0"),
                        prf.getString("3_days_ago", ":0:0"),
                        prf.getString("4_days_ago", ":0:0"),
                };
        DataPoint[] dp4 = new DataPoint[states4.length];
        j = states4.length-1;
        for (int i = 0; i < states4.length; i++) {
            dp4[i] = new DataPoint(i+1, (int) Float.parseFloat(states4[j].split(":")[1]));
            j--;
        }
        LineGraphSeries series3 = new LineGraphSeries<>(dp4);
        series3.setDrawBackground(true);
        series3.setAnimated(true);
        series3.setDrawDataPoints(true);


        prf = context.getSharedPreferences("mood", context.MODE_PRIVATE);
        frequency[4] = prf.getInt("frequency", 0);
        dates[4] = prf.getString("last_time", context.getString(R.string.never));
        all_time = prf.getFloat("all_time", 0)/60;
        if(all_time > 0)
            direction[4] = "+" + String.format("%.1f", all_time);
        else
            direction[4] = String.format("%.1f", all_time);

        String[] states5 =
                {
                        prf.getString("0_days_ago", ":0:0"),
                        prf.getString("1_days_ago", ":0:0"),
                        prf.getString("2_days_ago", ":0:0"),
                        prf.getString("3_days_ago", ":0:0"),
                        prf.getString("4_days_ago", ":0:0"),
                };
        DataPoint[] dp5 = new DataPoint[states5.length];
        j = states5.length-1;
        for (int i = 0; i < states5.length; i++) {
            dp5[i] = new DataPoint(i+1, (int) Float.parseFloat(states5[j].split(":")[1]));
            j--;
        }
        LineGraphSeries series4 = new LineGraphSeries<>(dp5);
        series4.setDrawBackground(true);
        series4.setAnimated(true);
        series4.setDrawDataPoints(true);




        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item(influences, slider, questions[0], series0, graph, d1, direction[0], String.valueOf(frequency[0]) + " / " + context.getString(R.string.week), context.getString(R.string.sport), "", context.getString(R.string.very_high), "TODAY", dates[0]));
        items.add(new Item(influences, slider, questions[1], series1, graph, d2, direction[1], String.valueOf(frequency[1]) + " / " + context.getString(R.string.week), context.getString(R.string.nutrition), "", context.getString(R.string.high), "TODAY", dates[1]));
        items.add(new Item(influences, slider, questions[2], series2, graph, d3, direction[2], String.valueOf(frequency[2]) + " / " + context.getString(R.string.week), context.getString(R.string.smoking), "", context.getString(R.string.very_high), "TODAY", dates[2]));
        items.add(new Item(influences, slider, questions[3], series3, graph, d4, direction[3], String.valueOf(frequency[3]) + " / " + context.getString(R.string.week), context.getString(R.string.outside), "", context.getString(R.string.medium), "TODAY", dates[3]));
        items.add(new Item(influences, slider, questions[4], series4, graph, d5, direction[4], String.valueOf(frequency[4]) + " / " + context.getString(R.string.week), context.getString(R.string.mood), "", context.getString(R.string.high), "TODAY", dates[4]));
        return items;

    }
}
