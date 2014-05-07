package se.andreasottesen.epguidesviewer.app;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    public static final String URL = "http://epguides.com/";
    public static final String SELECTOR_MENU = ".center tr:nth-child(1) a";

    private ProgressDialog pDiag;
    private TextView txtResult;
    private ListView listMenu;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResult = (TextView)findViewById(R.id.txtResult);
        listMenu = (ListView)findViewById(R.id.listMenu);

        //test
        new GetDataAsyncTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetDataAsyncTask extends AsyncTask<Object, Elements, Elements>{
        @Override
        protected void onPostExecute(Elements s) {
            final ArrayList<String> menuArray = new ArrayList<String>();

            for (Element element : s){
                menuArray.add(element.attr("href"));
            }

            listAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, menuArray);
            listMenu.setAdapter(listAdapter);

            if (pDiag != null){
                pDiag.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pDiag = new ProgressDialog(MainActivity.this);
            pDiag.setMessage("Loading...");
            pDiag.setTitle("EpGuides");
            pDiag.setIndeterminate(false);
            pDiag.show();
        }

        @Override
        protected Elements doInBackground(Object... objects) {
            Elements menus = null;
            try {
                Document doc = Jsoup.connect(URL).get();
                menus = doc.select(SELECTOR_MENU);
            } catch (IOException e) {
                Log.d("JSOUP", e.getMessage());
                e.printStackTrace();
            }

            return menus;
        }
    }

}
