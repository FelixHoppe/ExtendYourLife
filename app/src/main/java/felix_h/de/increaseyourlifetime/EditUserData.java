package felix_h.de.increaseyourlifetime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ramotion.fluidslider.FluidSlider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import yanzhikai.textpath.SyncTextPathView;


public class EditUserData extends AppCompatActivity {

    int back_pressed_counter = 0;
    final int max = 6;
    final int min = 1;
    final int total = max - min;
    final int current_year = Calendar.getInstance().get(Calendar.YEAR);
    double lifeexpectancy = 0;
    String gender;
    String country;
    int height = 175;
    int weight = 75;
    int nutrition = 3;
    int smoking = 3;
    int yearOfBirth = 1975;
    int sport = 3;
    ImageButton img_btn_next;


    @SuppressWarnings("Convert2Lambda")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_data);

        gender = loadFromPreference("gender", "user_data", getApplicationContext());
        country = loadFromPreference("country", "user_data", getApplicationContext());

        if (loadFromPreference("nutrition", "user_data", getApplicationContext()) != null)
            nutrition = Integer.parseInt(loadFromPreference("nutrition", "user_data", getApplicationContext()));

        if (loadFromPreference("smoking1", "user_data", getApplicationContext()) != null)
            smoking = Integer.parseInt(loadFromPreference("smoking1", "user_data", getApplicationContext()));

        if (loadFromPreference("sport", "user_data", getApplicationContext()) != null)
            sport = Integer.parseInt(loadFromPreference("sport", "user_data", getApplicationContext()));

        if (loadFromPreference("year_of_birth", "user_data", getApplicationContext()) != null)
            yearOfBirth = Integer.parseInt(loadFromPreference("year_of_birth", "user_data", getApplicationContext()));
        else
            saveToPreference(String.valueOf(yearOfBirth), "year_of_birth", "user_data", getApplicationContext());

        if (loadFromPreference("height", "user_data", getApplicationContext()) != null)
            height = Integer.parseInt(loadFromPreference("height", "user_data", getApplicationContext()));

        if(loadFromPreference("weight", "user_data", getApplicationContext()) != null)
            weight = Integer.parseInt(loadFromPreference("weight", "user_data", getApplicationContext()));

        img_btn_next = findViewById(R.id.img_btn_next);

        Spinner spinner = findViewById(R.id.country_spinner);
        List<String> list = new ArrayList<>();

        AssetManager assetManager = getAssets();
        String[] data = {};
        try {
            data = assetManager.list("data");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String i : data)
            list.add(i);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt(getString(R.string.countries));

        if(country != null)
            spinner.setSelection(list.indexOf(country));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lifeexpectancy = getLE(list.get(position), gender, yearOfBirth);
                saveToPreference(list.get(position), "country", "user_data", getApplicationContext());
                calculateLifeSpan();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RadioButton mRB = findViewById(R.id.maleRB);
        mRB.setChecked(gender == "MEN");
        RadioButton fRB = findViewById(R.id.femaleRB);
        fRB.setChecked(gender == "WOMEN");

        mRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fRB.setChecked(!mRB.isChecked());
                if (mRB.isChecked())
                    saveToPreference("MEN", "gender", "user_data", getApplicationContext());
                calculateLifeSpan();
            }
        });

        fRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRB.setChecked(!fRB.isChecked());
                if (fRB.isChecked())
                    saveToPreference("WOMEN", "gender", "user_data", getApplicationContext());
                calculateLifeSpan();
            }
        });

        Intent i = new Intent(this, MainActivity.class);

        img_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gender != null & country != null)
                {
                    if(loadFromPreference("first_le", "first_time", getApplicationContext()) == null)
                        saveToPreference(String.valueOf(lifeexpectancy), "first_le", "first_time", getApplicationContext());
                    startActivity(i);
                }
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.no_gender_country), Toast.LENGTH_LONG).show();
            }
        });

        yearOfBirth();
        sport();
        smoking();
        nutrition();
        height();
        weight();

    }

    private void weight () {

        final TextView textView = findViewById(R.id.weightTextView);
        final FluidSlider slider = findViewById(R.id.weightFluidSlider);

        int minWeight = 30;
        int maxWeight = 230;
        int totalWeight = maxWeight - minWeight;

        slider.setBeginTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.INVISIBLE);
                return Unit.INSTANCE;
            }
        });

        slider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.VISIBLE);
                saveToPreference(slider.getBubbleText(), "weight", "user_data", getApplicationContext());
                calculateLifeSpan();
                return Unit.INSTANCE;
            }
        });

        // Java 8 lambda
        slider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (minWeight + totalWeight * pos));
            slider.setBubbleText(value);

            return Unit.INSTANCE;
        });


        float avg = weight - minWeight;
        float total2 = (float) totalWeight;
        float slider_pos = avg / total2;

        slider.setPosition(slider_pos);
        slider.setStartText(String.valueOf(minWeight));
        slider.setEndText(String.valueOf(maxWeight));

    }

    private void height () {

        final TextView textView = findViewById(R.id.heightTextView);
        final FluidSlider slider = findViewById(R.id.heightFluidSlider);

        int minHeight = 150;
        int maxHeight = 220;
        int totalHeight = maxHeight - minHeight;

        slider.setBeginTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.INVISIBLE);
                return Unit.INSTANCE;
            }
        });

        slider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.VISIBLE);
                saveToPreference(slider.getBubbleText(), "height", "user_data", getApplicationContext());
                calculateLifeSpan();
                return Unit.INSTANCE;
            }
        });

        // Java 8 lambda
        slider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (minHeight + totalHeight * pos));
            slider.setBubbleText(value);

            return Unit.INSTANCE;
        });

        float avg = height - minHeight;
        float total2 = (float) totalHeight;
        float slider_pos = avg / total2;

        slider.setPosition(slider_pos);
        slider.setStartText(String.valueOf(minHeight));
        slider.setEndText(String.valueOf(maxHeight));
    }

    private void nutrition () {

        final TextView textView = findViewById(R.id.nutritionTextView);
        final FluidSlider slider = findViewById(R.id.nutritionFluidSlider);

        slider.setBeginTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.INVISIBLE);
                return Unit.INSTANCE;
            }
        });

        slider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.VISIBLE);
                saveToPreference(slider.getBubbleText(), "nutrition", "user_data", getApplicationContext());
                calculateLifeSpan();
                return Unit.INSTANCE;
            }
        });

        // Java 8 lambda
        slider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (min + total * pos));
            if (Integer.parseInt(value) != 6)
                slider.setBubbleText(value);
            else
                slider.setBubbleText("5");

            return Unit.INSTANCE;
        });

        slider.setPosition((float) 1/6*nutrition);
        slider.setStartText(String.valueOf(min));
        slider.setEndText(String.valueOf(max - 1));

    }

    private void smoking () {

        final TextView textView = findViewById(R.id.smokingTextView);
        final FluidSlider slider = findViewById(R.id.smokingFluidSlider);


        slider.setBeginTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.INVISIBLE);
                return Unit.INSTANCE;
            }
        });

        slider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.VISIBLE);
                saveToPreference(slider.getBubbleText(), "smoking1", "user_data", getApplicationContext());
                calculateLifeSpan();
                return Unit.INSTANCE;
            }
        });

        // Java 8 lambda
        slider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (min + total * pos));
            if (Integer.parseInt(value) != 6)
                slider.setBubbleText(value);
            else
                slider.setBubbleText("5");

            return Unit.INSTANCE;
        });

        slider.setPosition((float) 1/6*smoking);
        slider.setStartText(String.valueOf(min));
        slider.setEndText(String.valueOf(max - 1));

    }

    private void yearOfBirth () {

        final TextView textView = findViewById(R.id.yearTextView);
        final FluidSlider slider = findViewById(R.id.yearFluidSlider);

        int minYear = 1900;
        int maxYear = 2019;
        int totalYear = maxYear - minYear;

        slider.setBeginTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.INVISIBLE);
                return Unit.INSTANCE;
            }
        });

        slider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.VISIBLE);
                saveToPreference(slider.getBubbleText(), "year_of_birth", "user_data", getApplicationContext());
                yearOfBirth = Integer.parseInt(slider.getBubbleText());
                calculateLifeSpan();
                return Unit.INSTANCE;
            }
        });

        // Java 8 lambda
        slider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (minYear + totalYear * pos));
            slider.setBubbleText(value);
            return Unit.INSTANCE;
        });


        float avg = yearOfBirth - minYear;
        float total2 = (float) totalYear;
        float slider_pos = avg / total2;


        slider.setPosition(slider_pos);
        slider.setStartText(String.valueOf(minYear));
        slider.setEndText(String.valueOf(maxYear));
    }

    private void sport () {

        final TextView textView = findViewById(R.id.sportTextView);
        final FluidSlider slider = findViewById(R.id.sportFluidSlider);


        slider.setBeginTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.INVISIBLE);
                return Unit.INSTANCE;
            }
        });

        slider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                textView.setVisibility(View.VISIBLE);
                saveToPreference(slider.getBubbleText(), "sport", "user_data", getApplicationContext());
                calculateLifeSpan();
                return Unit.INSTANCE;
            }
        });

        // Java 8 lambda
        slider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (min + total * pos));
            if (Integer.parseInt(value) != 6)
                slider.setBubbleText(value);
            else
                slider.setBubbleText("5");

            return Unit.INSTANCE;
        });

        // slider.setPosition(sport);
        slider.setPosition((float) 1/6*sport);
        slider.setStartText(String.valueOf(min));
        slider.setEndText(String.valueOf(max - 1));
    }


    private void calculateLifeSpan () {

        gender = loadFromPreference("gender", "user_data", getApplicationContext());
        country = loadFromPreference("country", "user_data", getApplicationContext());

        if (loadFromPreference("nutrition", "user_data", getApplicationContext()) != null)
            nutrition = Integer.parseInt(loadFromPreference("nutrition", "user_data", getApplicationContext()));

        if (loadFromPreference("smoking1", "user_data", getApplicationContext()) != null)
            smoking = Integer.parseInt(loadFromPreference("smoking1", "user_data", getApplicationContext()));

        if (loadFromPreference("sport", "user_data", getApplicationContext()) != null)
            sport = Integer.parseInt(loadFromPreference("sport", "user_data", getApplicationContext()));

        if (loadFromPreference("year_of_birth", "user_data", getApplicationContext()) != null)
            yearOfBirth = Integer.parseInt(loadFromPreference("year_of_birth", "user_data", getApplicationContext()));

        if (loadFromPreference("height", "user_data", getApplicationContext()) != null &
                loadFromPreference("weight", "user_data", getApplicationContext()) != null) {
            height = Integer.parseInt(loadFromPreference("height", "user_data", getApplicationContext()));
            weight = Integer.parseInt(loadFromPreference("weight", "user_data", getApplicationContext()));
        }

        double bmiLifeShorten;
        int age = current_year - yearOfBirth;
        double optBmi;

        if (age <= 24)
            optBmi = 21.5;
        else if (age <= 34)
            optBmi = 22.5;
        else if (age <= 44)
            optBmi = 23.5;
        else if (age <= 54)
            optBmi = 24.5;
        else if (age <= 64)
            optBmi = 25.5;
        else
            optBmi = 26.5;

        double dHeight = height;
        dHeight /= 100;
        double bmi = weight / (dHeight * dHeight);

        double bmiDif = bmi - optBmi;

        if (bmiDif <= 0)
            bmiLifeShorten = Math.abs(bmiDif * 1.2);
        else
            bmiLifeShorten = bmiDif * 0.35;

        lifeexpectancy = getLE(country, gender, yearOfBirth);

        lifeexpectancy -= bmiLifeShorten;

        double nutrition_max_effect = 14;
        if(nutrition == 1)
            lifeexpectancy -= 2 * nutrition_max_effect/4;
        if(nutrition == 2)
            lifeexpectancy -= 1 * nutrition_max_effect/4;
        if(nutrition == 3)
            lifeexpectancy -= 0;
        if(nutrition == 4)
            lifeexpectancy += 1 * nutrition_max_effect/4;
        if(nutrition == 5)
            lifeexpectancy += 2 * nutrition_max_effect/4;


        double sport_max_effect = 12;
        if(sport == 1)
            lifeexpectancy -= 2 * sport_max_effect/4;
        if(sport == 2)
            lifeexpectancy -= 1 * sport_max_effect/4;
        if(sport == 3)
            lifeexpectancy -= 0;
        if(sport == 4)
            lifeexpectancy += 1 * sport_max_effect/4;
        if(sport == 5)
            lifeexpectancy += 2 * sport_max_effect/4;


        double smoking_max_effect = 13;
        double smoking_percentage = 0.26;
        if (smoking == 1)
            lifeexpectancy += smoking_max_effect * smoking_percentage;
        if (smoking == 2)
            lifeexpectancy -= 1 * (smoking_max_effect * (1 - smoking_percentage) / 4);
        if (smoking == 3)
            lifeexpectancy -= 2 * (smoking_max_effect * (1 - smoking_percentage) / 4);
        if (smoking == 4)
            lifeexpectancy -= 3 * (smoking_max_effect * (1 - smoking_percentage) / 4);
        if (smoking == 5)
            lifeexpectancy -= 4 * (smoking_max_effect * (1 - smoking_percentage) / 4);

        lifeexpectancy += 0.2*(getLE(country, gender, yearOfBirth)-lifeexpectancy);

        if(lifeexpectancy < 20)
            if(gender == "WOMEN")
                lifeexpectancy = 20-0.01*(2018-yearOfBirth);
            else
                lifeexpectancy = 19-0.01*(2018-yearOfBirth);

        SyncTextPathView stpv = findViewById(R.id.edit_user_data_stpv);
        DecimalFormat df = new DecimalFormat("#.#");
        stpv.setText(df.format(lifeexpectancy));

        saveToPreference(String.valueOf(lifeexpectancy), "my_lifeexpectency", "user_data", this);

        stpv.setDuration(1000);
        stpv.startAnimation(0, 1);
    }


    public boolean saveToPreference (String str, String name, String preference, Context mContext)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(preference, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, str);
        return editor.commit();
    }

    public String loadFromPreference (String name, String preference, Context mContext)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(preference, 0);
        return prefs.getString(name, null);
    }

    public double getLE (String country, String gender, int year){

        if (year == 0)
            year = 1975;
        if (gender == null)
            gender = "TOTAL";
        if (country == null)
            country = "United States";

        int newDif = 0;
        int oldDif = 0;
        if (year < 1960)
        {
            oldDif = 1960 - year;
            year = 1960;
        }
        if(year > 2016)
        {
            newDif = year - 2016;
            year = 2016;
        }


        AssetManager assetManager = getAssets();
        try {
            InputStream is = assetManager.open("data/" + country + "/" + gender + "/lifeexpectancy.dat");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            BufferedReader br = new BufferedReader(new StringReader(new String(buffer)));

            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains(Integer.toString(year))) {
                    Double LE = Double.parseDouble(line.replace(Integer.toString(year) + " ", ""));

                    if (oldDif != 0)
                        LE -= oldDif * 0.25;
                    if (newDif != 0)
                        LE += newDif * 0.25;

                    return LE;
                }
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 65 - 0.25*(1970-year);
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