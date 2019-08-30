package felix_h.de.increaseyourlifetime;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    int back_pressed_counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(getString(R.string.goals),
                getString(R.string.goals_desc), R.drawable.icon_mountain, Color.parseColor("#b15eff")));

        addSlide(AppIntroFragment.newInstance(getString(R.string.statistics),
                getString(R.string.statistics_desc), R.drawable.icon_analysis, Color.parseColor("#08a8eb")));

        addSlide(AppIntroFragment.newInstance(getString(R.string.diet),
                getString(R.string.diet_desc), R.drawable.icon_diet, Color.parseColor("#ff7043")));

        addSlide(AppIntroFragment.newInstance(getString(R.string.obstacles),
                getString(R.string.obstacles_desc), R.drawable.icon_trophy, Color.parseColor("#b15eff")));

        addSlide(AppIntroFragment.newInstance(getString(R.string.future),
                getString(R.string.future_desc), R.drawable.icon_beach, getResources().getColor(R.color.colorPrimary)));

        setDepthAnimation();
        setFadeAnimation();
        setZoomAnimation();
        setFlowAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(this, EditUserData.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(this, EditUserData.class);
        startActivity(intent);
    }


    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
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