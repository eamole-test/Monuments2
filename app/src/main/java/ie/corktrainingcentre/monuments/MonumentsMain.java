package ie.corktrainingcentre.monuments;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;


public class MonumentsMain extends ActionBarActivity {

    String[] monuments;
    String[] fileNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monuments_main);

        Resources res = getResources();
        monuments = res.getStringArray(R.array.monuments);
        fileNames = res.getStringArray(R.array.files);

        // add all the monuments to the LinearLayout
        RelativeLayout monumentsLayout = (RelativeLayout) findViewById(R.id.monumentsLayout);

        // create an array to store the monument RelativeLayouts
        RelativeLayout[] rlMonuments = new RelativeLayout[monuments.length];
        RelativeLayout.LayoutParams params = null;
        int[] viewIDs = new int[monuments.length];
        for(int i = 0; i < monuments.length; i++) {
            viewIDs[i] = getUniqueViewId();
            rlMonuments[i] = createMonumentItem(i);
            rlMonuments[i].setId( viewIDs[i] );
            params = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                      RelativeLayout.LayoutParams.WRAP_CONTENT);
            if( i > 0) {
                // put this widget below the one above
                params.addRule(RelativeLayout.BELOW, rlMonuments[i - 1].getId() );
            }
            monumentsLayout.addView( rlMonuments[i], params );
        }

     }

    private RelativeLayout createMonumentItem(int monument) {

        ImageView monumentImage = getMonumentImage(monument);
        TextView monumentName = getMonumentName(monument);
        TextView monumentText = getMonumentText(monument);

        // template
        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        layout.setLayoutParams(params);

        // monument image layout
        params = new RelativeLayout.LayoutParams(150, 150); // image width and height
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layout.addView(monumentImage, params);

        // monument name
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, monumentImage.getId());
        layout.addView(monumentName, params);

        // monument details
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, monumentImage.getId());
        layout.addView(monumentText, params);

        return layout;
    }

    private ImageView getMonumentImage(int monument) {
        ImageView monumentImage = new ImageView( getApplicationContext() );
        String fileName = fileNames[monument];
        String resourceType = "drawable";
        String packageName = getApplicationContext().getPackageName();
        Resources res = this.getResources();
        int identifier = res.getIdentifier( fileName, resourceType, packageName );
        monumentImage.setImageResource(identifier);
        monumentImage.setPadding(0, 10, 5, 5); // left, top, right, bottom
        monumentImage.setBackgroundColor(Color.WHITE);
        monumentImage.setId( getUniqueViewId() );
        return monumentImage;
    }

    private TextView getMonumentName(int monument) {
        TextView monumentName = new TextView(getApplicationContext());
        monumentName.setText(monuments[monument]);
        monumentName.setTextColor(Color.BLACK);
        monumentName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        monumentName.setGravity(Gravity.CENTER_VERTICAL);
        monumentName.setBackgroundColor(Color.WHITE);
        monumentName.setMinHeight(150);
        monumentName.setId(getUniqueViewId());
        return monumentName;
    }

    private TextView getMonumentText(int monument) {
        RelativeLayout.LayoutParams generalParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT );
        TextView monumentText = new TextView(getApplicationContext());
        monumentText.setLayoutParams(generalParams);
        monumentText.setTextColor(Color.DKGRAY);
        monumentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        monumentText.setId( getUniqueViewId() );

        String fileName = fileNames[monument];
        String resourceType = "raw";
        String packageName = getApplicationContext().getPackageName();
        Resources res = this.getResources();
        int identifier = res.getIdentifier( fileName, resourceType, packageName );
        InputStream file = null;
        try {
            file = res.openRawResource(identifier);
            byte[] buffer = new byte[file.available()];
            file.read(buffer, 0, buffer.length);
            monumentText.setText(new String(buffer, "UTF-8"));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return monumentText;
    }

    private static int getUniqueViewId() {
        int uniqueID = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            uniqueID = generateViewId();
        } else {
            uniqueID = View.generateViewId();
        }
        return uniqueID;
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_monuments_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
