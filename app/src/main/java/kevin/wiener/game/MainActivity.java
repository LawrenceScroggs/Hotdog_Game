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

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView score_board;
    private TextView to_start;
    private ImageView kevin;
    private ImageView wiener;

    //Size of frame & kevin
    private int frameHeight;
    private int kevin_size;
    private int screen_width;
    private int screen_height;

    // Speed of Kevin & Wieners
    private int kevin_spd;
    private int wiener_spd;

    // Position of Kevin & Wieners
    private int kevin_Y;
    private int wiener_Y;
    private int wiener_X;

    // SCOREBOARD
    private int score;

    // Initialize Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    // Status of Kevin & Wiener
    private boolean action = false;
    private boolean start = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // moves off screen
        wiener.setX(-80);
        wiener.setY(-80);

        score_board.setText("Score: 0");
    }

    public void hitCheck() {

        // if wiener hits mouth
        if (wiener_X == (kevin_Y / 2) || wiener_X == (kevin_Y / 3) || wiener_Y == (kevin_Y/4)) {
            score += 30;
            wiener_X = -10;
            sound.playHitSound();
        }
    }

    public void changePos() {
        hitCheck();

        wiener_X -= wiener_spd;

        //wiener movement
        if (wiener_X < 0) {
            wiener_X = screen_width+20;
            wiener_Y = (int) Math.floor(Math.random() * (frameHeight - wiener.getHeight()));
        }
        wiener.setX(wiener_X);
        wiener.setY(wiener_Y);

        //kevin movement
        if (action == true) {
            kevin_Y -= 15;
        }  //no touching
        else {
            kevin_Y += 30;
        } //touch

        if (kevin_Y < 0 || kevin_Y > frameHeight) {
            kevin_Y = 0;

            timer.cancel();
            timer = null;

            // show results
            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("SCORE: ",score);
            startActivity(intent);
        }
        kevin.setY(kevin_Y);

        score_board.setText("Score: " + score);


    }


    public boolean onTouchEvent(MotionEvent user) {

        if (start == false) {
            start = true;

            FrameLayout frame = findViewById(R.id.frameLayout);
            frameHeight = frame.getHeight();

            kevin_Y = 100;

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

            return true;
        }
        public boolean dispatchKeyEvent(KeyEvent event){

            if(event.getAction() == KeyEvent.ACTION_DOWN){
                switch(event.getKeyCode()){
                    case KeyEvent.KEYCODE_BACK:
                        return true;
                }
            }

            return super.dispatchKeyEvent((event));
        }
}
