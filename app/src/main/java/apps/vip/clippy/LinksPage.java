package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class LinksPage extends AppCompatActivity {
    static ArrayList<String> links=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links_page);
        Intent intent = getIntent();

        System.out.println("Links in page "+Arrays.toString(links.toArray()));
        LinearLayout linksView=findViewById(R.id.links);
        //set the properties for button
        for(String link:links) {

            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            btnTag.setText(link.substring(link.lastIndexOf("://")+"://".length()));
            btnTag.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            btnTag.setAllCaps(false);

            btnTag.setOnClickListener(v -> {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(link));
            });
            //add button to the layout
            linksView.addView(btnTag);
        }

    }
}