package kevin.wiener.game;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.lang.*;

public class MainActivity extends AppCompatActivity {

    // text used within the game
    private TextView score_board;
    private TextView to_start;

    // images used within the game
    private ImageView kevin;
    private ImageView wiener;
    private ImageView bg1;
    private ImageView bg2;


    //Size of frame & kevin
    private int frameHeight;
    //private int kevin_size;
    private int screen_width;
    private int screen_height;
    private int img;

    // Speed of Kevin & Wieners
    private int kevin_spd;
    private int wiener_spd;
    // Position of Kevin & Wieners
    private int kevin_Y;
    private int kevin_X;
    private int wiener_Y;
    private int wiener_X;

    // Background for parallax
    private int back_X1;
    private int back_X2;

    // SCOREBOARD
    private int score;

    // Initialize handler/timer/soundplayer
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    // Status of Kevin & Wiener
    private boolean action = false;
    private boolean start = false;
    private boolean end = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);

        //Screen Size
        WindowManager win_man = getWindowManager();
        Display disp = win_man.getDefaultDisplay();
        Point point = new Point();
        disp.getSize(point);

        screen_width = point.x;
        screen_height = point.y;

        kevin_spd = Math.round(screen_height / 60);
        wiener_spd = Math.round(screen_width / 60);

        score_board = findViewById(R.id.score_board);
        to_start = findViewById(R.id.to_start);
        kevin = findViewById(R.id.kevin);
        wiener = findViewById(R.id.wiener);
        bg1 = findViewById(R.id.bg1);
        bg2 = findViewById(R.id.bg2);

        img = bg1.getWidth();
        kevin_Y = 100;

       // initial position
        kevin.setX(40);
        kevin.setY(100);

        // moves off screen
        wiener.setX(-80);
        wiener.setY(-80);

        back_X1 = 0;
        bg1.setX(back_X1);

        back_X2 = 3018;
        bg2.setX(back_X2);

        //score_board.setText("Score: " + score);
        score_board.setText(getString(R.string.score, score));
    }

    public void parallax(boolean end){

        if(end == false){
            back_X1 -= 20;
            back_X2 -= 20;
            img = bg1.getWidth();
            String screen = "Screen W: " + screen_width + " img: " + img;
            System.out.println(screen);
            String bg_1 = "bg1: " + back_X1;
            System.out.println(bg_1);
            String bg_2 = "bg2: " + back_X2;
            System.out.println(bg_2);
            System.out.println();

            bg1.setX(back_X1);
            bg2.setX(back_X2);

            if(back_X1 <= -(img - 38)){
                back_X1 = bg1.getWidth();
                bg1.setX(back_X1 - 1);
                String R1 = "b1 reset" + back_X1;
                System.out.println(R1);
            }
            if(back_X2 <= -(img - 38)){
                back_X2 = bg2.getWidth();
                bg2.setX(back_X2 - 1);
                String R2 = "b2 reset" + back_X2;
                System.out.println(R2);
            }
        }
        return;

    }
    public void changePos() {

        hitCheck();
        parallax(end);

        wiener_X -= wiener_spd;

        //wiener movement
        if (wiener_X < 0) {
            wiener_X = screen_width+30;
            wiener_Y = (int) Math.floor(Math.random() * (frameHeight - wiener.getHeight()));
        }
        wiener.setX(wiener_X);
        wiener.setY(wiener_Y);

        //kevin movement
        if (action == true) {
            kevin_Y -= 20;
        }  //no touching
        else {
            kevin_Y += 30;
        } //touch

        kevin.setY(kevin_Y);
        kevin.setX(kevin_X);

        score_board.setText(getString(R.string.score, score));

    }

    public void hitCheck() {
        String printKevin = "Kevin Y " + kevin_Y;
        System.out.println(printKevin);
        System.out.println();
        /*
        String printWiener = "Wiener Y " + wiener_Y;
        System.out.println();
        System.out.println(printWiener);

      */
        if (kevin_Y < 10 || kevin_Y > frameHeight - 400) {

            parallax(false);

            kevin_Y = 200;

            timer.cancel();
            timer = null;

            // show results
            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }

        // if wiener hits mouth
        if (wiener_X >= 20 && wiener_X <= 50){

/**
 * Set range of values in which wiener Y can be between +/- kevin_Y
 * for range of hits
 */
            if(wiener_Y >= kevin_Y - 200 && wiener_Y <= kevin_Y + 50) {
                score += 30;
                wiener_X = -100;
                sound.playHitSound();
              /*  printKevin = "HIT: Kevin Y " + kevin_Y;
                System.out.println(printKevin);
                printWiener = "HIT: Wiener Y " + wiener_Y;
                System.out.println(printWiener);

               */

            }

            }

            score_board.setText(getString(R.string.score, score));
     }
    @Override
    public boolean onTouchEvent(MotionEvent user) {

        if (start == false) {
            start = true;

            FrameLayout frame = findViewById(R.id.frameLayout);
            frameHeight = frame.getHeight();

            kevin.setY(80);
            kevin.setX(150);

            to_start.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            changePos();
                        }
                    });
                }
            },0,20);
        } else {
            if (user.getAction() == MotionEvent.ACTION_DOWN) {
                action = true;
            }
            else if(user.getAction() == MotionEvent.ACTION_UP)
                action = false;
        }

            return super.onTouchEvent(user);
        }
        public boolean dispatchKeyEvent(KeyEvent event){

            if(event.getAction() == KeyEvent.ACTION_DOWN){
                switch(event.getKeyCode()){
                    case KeyEvent.KEYCODE_BACK:
                        return true;
                }
            }

            return super.dispatchKeyEvent(event);
        }

}
