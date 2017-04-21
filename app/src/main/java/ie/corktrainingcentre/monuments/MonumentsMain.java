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

    String[] monumentNames;
    String[] fileNames;

    int[] monumentImageIds;
    String[] monumentDetails;


    // these are the objects to hold the template controls
    ImageView monumentImageView;
    TextView monumentNameView;
    TextView monumentDetailView;

    ViewGroup monumentsLayout;  // container layout. Use ViewGroup to handle any layout Relative, Linear etc
    ViewGroup monumentLayout;   // template layout

    ViewGroup prevMonumentLayout;  // remember previous layout to add below it!


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monuments_main);

        loadMonumentData();

        // add all the monuments to the LinearLayout
        RelativeLayout monumentsLayout = (RelativeLayout) findViewById(R.id.monumentsLayout);

        for(int i = 0; i < monumentNames.length; i++) {

            ViewGroup templateLayout = createMonumentLayout(i);

            setMonumentViewData(i);

            monumentsLayout.addView(templateLayout);

        }

    }


    private void loadMonumentData() {

        // read static resource data
        Resources res = getResources();
        monumentNames = res.getStringArray(R.array.monuments);
        fileNames = res.getStringArray(R.array.files);
        monumentImageIds = new int[fileNames.length];
        monumentDetails=new String[fileNames.length];

        for (int i = 0; i < monumentNames.length; i++) {
            // get the image resource id
            monumentImageIds[i]= getImageResourceId(fileNames[i]);

            // get the descriptions
            monumentDetails[i] = getRawTextResource(fileNames[i]);
        }

    }

    private void setMonumentViewData(int index) {
        setMonumentImage(index);
        setMonumentName(index);
        setMonumentDetails(index);
    }
    /*
        create the layout that will hold the various controls
     */
    private RelativeLayout createMonumentLayout(int index) {

        monumentImageView = createMonumentImageView();
        monumentNameView = createMonumentNameView();
        monumentDetailView = createMonumentTextView();

        // the outer layout for the template
        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        layout.setLayoutParams(params);

        // monument image
        params = new RelativeLayout.LayoutParams(150, 150); // image width and height
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        layout.addView(createMonumentImageView(), params);

        // monument name
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, monumentImageView.getId()); // add to right of image

        layout.addView(monumentNameView, params);

        // monument details
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, monumentImageView.getId());
        layout.addView(monumentDetailView, params);

        // where ot add this layout is under tyhe previous Template RelativeLayout - if there is one
        if(prevMonumentLayout!=null) { // no prev
            RelativeLayout.LayoutParams relToPrevLayoutParams
                    = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
            // put this widget below the one above
            params.addRule(RelativeLayout.BELOW, prevMonumentLayout.getId() );
        }
        prevMonumentLayout = layout;
        return layout;
    }

    public ImageView createMonumentImageView() {

        monumentImageView = new ImageView( getApplicationContext() );
        // monumentImage.setImageResource(identifier);      // set later!!
        monumentImageView.setPadding(0, 10, 5, 5); // left, top, right, bottom
        monumentImageView.setBackgroundColor(Color.WHITE);
        monumentImageView.setId( getUniqueViewId() );
        return monumentImageView;
    }

    private void setMonumentImage(int index){
        monumentImageView.setImageResource(monumentImageIds[index]); // set later!!
    }

    private TextView createMonumentNameView() {
        TextView monumentName = new TextView(getApplicationContext());
        // monumentName.setText(monuments[monument]);
        monumentName.setTextColor(Color.BLACK);
        monumentName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        monumentName.setGravity(Gravity.CENTER_VERTICAL);
        monumentName.setBackgroundColor(Color.WHITE);
        monumentName.setMinHeight(150);
        monumentName.setId(getUniqueViewId());
        return monumentName;
    }

    private void setMonumentName(int index) {
        monumentNameView.setText(monumentNames[index]);
    }

    private TextView createMonumentTextView() {

        RelativeLayout.LayoutParams generalParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT );
        TextView monumentText = new TextView(getApplicationContext());
        monumentText.setLayoutParams(generalParams);
        monumentText.setTextColor(Color.DKGRAY);
        monumentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        monumentText.setId( getUniqueViewId() );
        return monumentText;

    }

    private void setMonumentDetails(int index) {
        monumentDetailView.setText( getRawTextResource(fileNames[index]));
    }



    private int getImageResourceId(String fileName) {

        String resourceType = "drawable";
        String packageName = getApplicationContext().getPackageName();
        Resources res = this.getResources();
        int identifier = res.getIdentifier( fileName, resourceType, packageName );
        return identifier;

    }

    private String getRawTextResource(String fileName) {
        String text="Error reading text";
        String resourceType = "raw";
        String packageName = getApplicationContext().getPackageName();
        Resources res = this.getResources();
        int identifier = res.getIdentifier( fileName, resourceType, packageName );
        InputStream file = null;
        try {
            file = res.openRawResource(identifier);
            byte[] buffer = new byte[file.available()];
            file.read(buffer, 0, buffer.length);
            text = new String(buffer, "UTF-8");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
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
     * Generate a value suitable for use in
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

}
