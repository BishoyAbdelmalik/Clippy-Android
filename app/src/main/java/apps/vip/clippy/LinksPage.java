package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.Arrays;

public class LinksPage extends AppCompatActivity {
    static String[] links=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links_page);

        System.out.println("Links in page "+Arrays.toString(links));
        LinearLayout linksView=findViewById(R.id.links);
        //set the properties for button
        for(String link:links) {

            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

//            btnTag.setText(link.substring(link.lastIndexOf("://")+"://".length()));
            btnTag.setText(link);
            btnTag.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            btnTag.setAllCaps(false);
            if(!link.startsWith("http")){
                link="http://"+link;
            }
            String finalLink = link;
            btnTag.setOnClickListener(v -> {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(finalLink));
            });
            //add button to the layout
            linksView.addView(btnTag);
        }
        Button openAll=findViewById(R.id.openAll);
        openAll.setOnClickListener(v -> {
            for(String link:links) {
                if(!link.startsWith("http")){
                    link="http://"+link;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            }
        });

    }
}