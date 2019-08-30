package felix_h.de.increaseyourlifetime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.rebound.SpringConfig;
import com.jpeng.jpspringmenu.MenuListener;
import com.jpeng.jpspringmenu.SpringMenu;
import com.ramotion.foldingcell.FoldingCell;
import java.text.DecimalFormat;
import java.util.ArrayList;
import yanzhikai.textpath.SyncTextPathView;

public class MainActivity extends AppCompatActivity implements MenuListener {

    SpringMenu mSpringMenu;
    int back_pressed_counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(loadFromPreference("firstStart", "firstTime", this) == null)
        {
            saveToPreference("started", "firstStart", "firstTime", this);
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
        else
        {
            AppRater.app_launched(this);

            Toolbar toolbar = findViewById(R.id.toolbar);

            toolbar.setOnClickListener(v -> {

                saveToPreference("clicked","toolbar", "firstTime", getApplicationContext());

                int[] results = calculate();
                final int total_time = results[0];
                final int total_day_counter = results[1];
                final int total_won_time = results[2];
                final int total_lost_time = Math.abs(results[3]);


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_added_time, null);
                TextView added_time = mView.findViewById(R.id.added_time);
                TextView in_hours = mView.findViewById(R.id.in_hours);
                TextView daily_avg = mView.findViewById(R.id.daily_avg);
                TextView won_time_field = mView.findViewById(R.id.won_time_field);
                TextView lost_time_field = mView.findViewById(R.id.lost_time_field);
                TextView added_time_close_btn = mView.findViewById(R.id.added_time_close_btn);
                CardView share_card = mView.findViewById(R.id.share_card);
                TextView my_le_btn = mView.findViewById(R.id.my_le_btn);


                won_time_field.setText(total_won_time/60 + " " + getString(R.string.hours));
                lost_time_field.setText(total_lost_time/60 + " " + getString(R.string.hours));
                added_time.setText(total_time/60 + " " + getString(R.string.won_time));
                in_hours.setText(getString(R.string.in_hours) + " " + total_time);


                if(total_day_counter != 0)
                    daily_avg.setText(getString(R.string.avg_minutes) + " " + total_time/total_day_counter + " " + getString(R.string.minutes));

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                share_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text1)
                                + " " + total_time/60 + " " + getString(R.string.hours)
                                + "\n" + getString(R.string.share_text2)
                                + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                });

                my_le_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        int[] results = calculate();
                        final int total_time = results[0];

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_life_expectancy, null);
                        TextView le = mView.findViewById(R.id.le);
                        TextView won_years = mView.findViewById(R.id.won_years);
                        TextView previous_le = mView.findViewById(R.id.previous_le);
                        TextView achivable_le = mView.findViewById(R.id.achivable_le);
                        TextView added_time_close_btn = mView.findViewById(R.id.le_close);
                        CardView share_card = mView.findViewById(R.id.le_share_card);

                        DecimalFormat df = new DecimalFormat("#.###");
                        double my_le = Double.parseDouble(loadFromPreference("my_lifeexpectency", "user_data", getApplicationContext()));

                        Double tt = (double) total_time;


                        Double total_won_years = tt/(60*24*365);
                        Double today_le = my_le + total_won_years;
                        int year = Integer.parseInt(loadFromPreference("year_of_birth", "user_data", getApplicationContext()));
                        Double achivable_years;
                        if(today_le-(2018-year) > 0)
                            achivable_years = today_le + (today_le-(2018-year))*0.2 + 10;
                        else
                            achivable_years = today_le + 1*0.2 + 10;

                        le.setText(df.format(today_le) + " " + getString(R.string.years));
                        won_years.setText(getString(R.string.won_years) + ": " + df.format(total_won_years));
                        previous_le.setText(df.format(my_le) + " " + getString(R.string.years));
                        achivable_le.setText(df.format(achivable_years) + " " + getString(R.string.years));


                        mBuilder.setView(mView);
                        final AlertDialog dialog2 = mBuilder.create();
                        dialog2.show();

                        share_card.setOnClickListener(v1 -> {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text3)
                                    + " " + today_le + " " + getString(R.string.years)
                                    + "\n" + getString(R.string.share_text2)
                                    + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                            sendIntent.setType("text/plain");
                            startActivity(sendIntent);
                        });

                        added_time_close_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                            }
                        });
                    }
                });

                added_time_close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            });



            SyncTextPathView stpv = findViewById(R.id.stpv);

            int value = calculate()[0]/60;
            if(value < 0)
                stpv.setText(value + " " + getString(R.string.hours));
            else
                stpv.setText("+" + value + " " + getString(R.string.hours));

            stpv.setDuration(4000);
            stpv.startAnimation(0,1);


            //init SpringMenu
            mSpringMenu = new SpringMenu(this, R.layout.view_menu);
            mSpringMenu.setMenuListener(this);
            mSpringMenu.setFadeEnable(true);
            mSpringMenu.setChildSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20, 5));
            mSpringMenu.setDragOffset(0.08f);
            mSpringMenu.setMenuListener(new MenuListener() {
                @Override
                public void onMenuOpen() {
                    saveToPreference("open", "options", "firstTime", getApplicationContext());
                }

                @Override
                public void onMenuClose() {

                }

                @Override
                public void onProgressUpdate(float value, boolean bouncing) {

                }
            });

            ListBean[] listBeen = {
                new ListBean(R.mipmap.settings, getString(R.string.settings)),
                new ListBean(R.mipmap.feedback, getString(R.string.feedback)),
                new ListBean(R.mipmap.credits, getString(R.string.licenses)),
                new ListBean(R.mipmap.privacy_policy, getString(R.string.privacy_policy)),
                new ListBean(R.mipmap.mail, getString(R.string.contact)),
                new ListBean(R.mipmap.about, getString(R.string.about))
            };

            MyAdapter adapter = new MyAdapter(this, listBeen);
            ListView listView = mSpringMenu.findViewById(R.id.test_listView);
            listView.setAdapter(adapter);


            // get our list view
            ListView theListView = findViewById(R.id.mainListView);

            TextView[] influences = {
                    findViewById(R.id.today_influence),
                    findViewById(R.id.today_influence_last),
                    findViewById(R.id.week_influence),
                    findViewById(R.id.week_influence_last),
                    findViewById(R.id.all_time_influence),
                    findViewById(R.id.all_time_influence_last)
            };

            // prepare elements to display
            final ArrayList<Item> items = Item.getTestingList(this, findViewById(R.id.graph), findViewById(R.id.content_slider), influences);

            // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
            final FoldingCellListAdapter fcAdapter = new FoldingCellListAdapter(this, items);

            // set elements to fcAdapter
            theListView.setAdapter(fcAdapter);

            // set on click event listener to list view
            theListView.setOnItemClickListener((fcAdapterView, view, pos, l) -> {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                saveToPreference("open", "card", "firstTime", getApplicationContext());
                // register in fcAdapter that state for selected cell is toggled
                if(fcAdapter.registerToggle(pos))
                {
                    int value1 = calculate()[0]/60;
                    if(value1 < 0)
                        stpv.setText(value1 + " " + getString(R.string.hours));
                    else
                        stpv.setText("+" + value1 + " " + getString(R.string.hours));

                    stpv.setDuration(500);
                    stpv.startAnimation(0,1);
                }
            });

            if(loadFromPreference("intro", "first_time", getApplicationContext()) == null)
            {
                saveToPreference("started", "intro", "first_time", getApplicationContext());
                intro(1, false);
            }
            else if(loadFromPreference("card", "firstTime", getApplicationContext()) == null && AppRater.get_launch_counter() > 1)
                intro(2, true);
            else if(loadFromPreference("toolbar", "firstTime", getApplicationContext()) == null && AppRater.get_launch_counter() > 2)
                intro(1, true);
            else if(loadFromPreference("options", "firstTime", getApplicationContext()) == null && AppRater.get_launch_counter() > 3)
                intro(4, true);
        }
    }

    private void intro(int index, boolean finish) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_intro, null);
        ImageView im = mView.findViewById(R.id.intro_image);
        TextView tv = mView.findViewById(R.id.intro_next_btn);
        TextView intro_text = mView.findViewById(R.id.intro_text);

        switch(index){
            case 1:
                im.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.intro_taskbar));
                intro_text.setText(getString(R.string.intro1));
                break;
            case 2:
                im.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.intro_card));
                intro_text.setText(getString(R.string.intro2));
                break;
            case 3:
                im.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.intro_graph));
                intro_text.setText(getString(R.string.intro3));
                break;
            case 4:
                im.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.intro_arrow));
                intro_text.setText(getString(R.string.intro4));
                break;
            case 5:
                finish = true;
                break;
            default:
                finish = true;
                break;
        }
        final boolean f = finish;

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        tv.setOnClickListener(v -> {
            if(f)
                dialog.dismiss();
            else
            {
                dialog.dismiss();
                intro(index + 1, false);
            }
        });
    }

    private int[] calculate() {

        SharedPreferences temp_prf = getApplicationContext().getSharedPreferences("time", Context.MODE_PRIVATE);

        int total = 0;
        int day_counter = temp_prf.getInt("daily_avg", 0);
        int won_time = 0;
        //int lost_time = 0;
        SharedPreferences prf;
        prf = getApplicationContext().getSharedPreferences("sport", MODE_PRIVATE);
        won_time += prf.getInt("sum_pos", 0);
        //lost_time += prf.getInt("sum_neg", 0);
        total += prf.getFloat("all_time", 0);
        prf = getApplicationContext().getSharedPreferences("nutrition", MODE_PRIVATE);
        won_time += prf.getInt("sum_pos", 0);
        //lost_time += prf.getInt("sum_neg", 0);
        total += prf.getFloat("all_time", 0);
        prf = getApplicationContext().getSharedPreferences("smoking", MODE_PRIVATE);
        won_time += prf.getInt("sum_pos", 0);
        //lost_time += prf.getInt("sum_neg", 0);
        total += prf.getFloat("all_time", 0);
        prf = getApplicationContext().getSharedPreferences("outside", MODE_PRIVATE);
        won_time += prf.getInt("sum_pos", 0);
        //lost_time += prf.getInt("sum_neg", 0);
        total += prf.getFloat("all_time", 0);
        prf = getApplicationContext().getSharedPreferences("mood", MODE_PRIVATE);
        won_time += prf.getInt("sum_pos", 0);
        //lost_time += prf.getInt("sum_neg", 0);
        total += prf.getFloat("all_time", 0);
        final int total_time = total;
        //final int total_lost_time = lost_time;
        final int total_lost_time = won_time - total_time;


        return new int[]{total_time, day_counter, won_time, total_lost_time};
    }


    public void saveToPreference(String str, String name, String preference, Context mContext)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(preference, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, str);
        editor.apply();
    }

    public String loadFromPreference(String name, String preference, Context mContext)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(preference, 0);
        return prefs.getString(name, null);
    }

    @Override
    public void onMenuOpen() {
    }

    @Override
    public void onMenuClose() {
    }

    @Override
    public void onProgressUpdate(float value, boolean bouncing) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mSpringMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed(){
        back_pressed_counter ++;
        if (back_pressed_counter == 1){
            Toast.makeText(this, R.string.back_pressed, Toast.LENGTH_LONG).show();
        }
        else {
            this.finishAffinity();
            back_pressed_counter = 0;
        }
    }
}

