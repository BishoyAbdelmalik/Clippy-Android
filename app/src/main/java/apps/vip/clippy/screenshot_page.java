package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class screenshot_page extends AppCompatActivity {
    TouchImageView img;
    public static String url=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot_page);
        img= new TouchImageView(this);
        img.setMaxZoom(4f);


        ForegroundService.getScreenshot(img);

        setContentView(img);


    }
    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.screenshot_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.refresh:
                ForegroundService.getScreenshot(img);
                break;
            case R.id.save:
                if (url !=null)
                    new downloadFile(url, MainActivity.context);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}