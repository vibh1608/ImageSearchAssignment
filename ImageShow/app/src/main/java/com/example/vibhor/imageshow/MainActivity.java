package com.example.vibhor.imageshow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HandleJsonListener
{

    EditText searchText;
    Button search_btn;
    GridView gridView;
    String text="";
    String url="";
    public ArrayList<PicDetails> arrayList = new ArrayList<>();
    public ArrayList<String> onlinePics = new ArrayList<>();
    public GridViewAdapter gridViewAdapter;
    public DBAdapter dbAdapter;
    int numofcoloums=2;
    public DataBaseHelper dataBaseHelper1,dataBaseHelper2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchText = findViewById(R.id.searchText);
        search_btn = findViewById(R.id.search_btn);
        gridView = findViewById(R.id.all_images);

        gridView.setNumColumns(numofcoloums);

        arrayList = new ArrayList<>();
        onlinePics = new ArrayList<>();

        search_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                text = searchText.getText().toString();

                if(!text.equals(""))
                {
                    dataBaseHelper1 = new DataBaseHelper(MainActivity.this);

                    if(isIntenetConnected())
                    {
                       // url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=1897b92eff5568083a3ec0b1349385f4" +
                         //       "&text="+text+"&format=json&nojsoncallback=1";  a7b3f96429ff2aec3773ab195ab07c17

                        url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=9a21a84144332a8eaa5549f67b9d1412" +
                                "&text="+text+"&format=json&nojsoncallback=1";

                        searchImages(url);
                    }
                    else
                    {
                        try
                        {
                            if(dataBaseHelper1.rowcount()!=0)
                            {
                                arrayList = dataBaseHelper1.getAllpics(text);
                                if(!arrayList.isEmpty())
                                {
                                    showOfflineImages();
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "No Result Found", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }catch (Exception e)
                        {
                            Toast.makeText(MainActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    searchText.setError("Empty!!!");
                    searchText.requestFocus();
                    return;
                }
            }
        });

        gridViewAdapter = new GridViewAdapter(MainActivity.this,onlinePics);

        if(isIntenetConnected())
        {
            if(!onlinePics.isEmpty())
            {
                gridView.setAdapter(gridViewAdapter);
            }
        }
        else
        {
            if(!arrayList.isEmpty())
            {
                dbAdapter = new DBAdapter(MainActivity.this,arrayList);
                gridView.setAdapter(dbAdapter);
            }
        }

        gridView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i)
            {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2)
            {
                if(i+i1>i2)
                {
                    searchImages(url);
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if(isIntenetConnected())
                {
                    // HERE I WANT TO GIVE THE INTENT TO DISPLAY IMAGE IN FULL SCREEN
                    Intent i = new Intent(MainActivity.this, FullScreenActivity.class);
                    i.putExtra("fullimagepath", onlinePics.get(position));
                    MainActivity.this.startActivity(i);
                }
            }
        });
    }

    private void searchImages(String url)
    {
        //creating interface object
        NetworkRequestTask networkRequestTask = new NetworkRequestTask(MainActivity.this,url,this);

        //executing the interface
        networkRequestTask.execute();
    }

    @Override
    public void updateData(String json)
    {
        arrayList.clear();

        onlinePics.clear();

        try
        {
            JSONObject jsonObject = new JSONObject(json);

            JSONObject object = jsonObject.getJSONObject("photos");
            JSONArray array = object.getJSONArray("photo");

            for(int i=0;i<array.length();i++)
            {
                JSONObject childObject = array.getJSONObject(i);

                String farm=childObject.getString("farm");
                String server=childObject.getString("server");
                String id=childObject.getString("id");
                String secret=childObject.getString("secret");

                String picURL = String.format("http://farm%s.static.flickr.com/%s/%s_%s_b.jpg", farm, server, id, secret);

                PicDetails picDetails = new PicDetails();
                picDetails.setPicName(text);

                if(i==0 || i==1)
                {
                    try
                    {
                        URL url = new URL(picURL);
                        PicAsync picAsync = new PicAsync();
                        picAsync.execute(url);

                    } catch (MalformedURLException e)
                    {
                        e.printStackTrace();
                    }

                    arrayList.add(picDetails);
                }
                onlinePics.add(picURL);
            }
            showList();

            gridView.setOnScrollListener(new AbsListView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i)
                {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2)
                {
                    if(i+i1>i2)
                    {
                        searchImages(url);
                    }
                }
            });

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id= item.getItemId();
        switch (id)
        {
            case R.id.col_two: {
                numofcoloums = 2;
                if(isIntenetConnected())
                {
                    showList();
                }
                else
                {
                    showOfflineImages();
                }

                break;
            }
            case R.id.col_three:
            {
                numofcoloums = 3;
                if(isIntenetConnected())
                {
                    showList();
                }
                else
                {
                    showOfflineImages();
                }

                break;
            }
            case R.id.col_four:
            {
                numofcoloums = 4;
                if(isIntenetConnected())
                {
                    showList();
                }
                else
                {
                    showOfflineImages();
                }

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showList()
    {
        gridView.setNumColumns(numofcoloums);

        gridViewAdapter = new GridViewAdapter(MainActivity.this,onlinePics);

        gridView.setAdapter(gridViewAdapter);
    }

    public void showOfflineImages()
    {
        gridView.setNumColumns(numofcoloums);

        dbAdapter = new DBAdapter(MainActivity.this,arrayList);

        gridView.setAdapter(dbAdapter);
    }

    //checking connection status of the device
    private boolean isIntenetConnected()
    {
        //initialising status of the device whether is connected to internet or not
        boolean isConnected = false;

        //creating connectivityManager object to check connection status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //getting network information
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //if connected to internet then changing its value
        if (networkInfo != null)
        {
            isConnected = true;
        }

        //returning connection status
        return isConnected;
    }

    //class FavAsync to store the movie details in favoriate movie list table in database
    public class PicAsync extends AsyncTask<URL, Void, Bitmap>
    {

        //doInBackground method
        @Override
        protected Bitmap doInBackground(URL... urls)
        {
            URLConnection connection = null;

            try
            {
                //getting url connection
                connection = urls[0].openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connection.setDoInput(true);

            try
            {
                //connecting with server
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            InputStream inputStream = null;

            try
            {
                //getting image inputStream from server
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BufferedInputStream bufferedInputStream = null;
            if (inputStream != null) {
                bufferedInputStream = new BufferedInputStream(inputStream);
            }

            //getting bitmap image from server
            Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

            return bitmap;

        }//end of doInBackground method

        //onPostExecute method
        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);

            dataBaseHelper2 = new DataBaseHelper(MainActivity.this);

            //creating storingData object to store information in database
            PicDetails picDetails = new PicDetails();
            picDetails.setPicName(text);
            picDetails.setStorepic(bitmap);

            dataBaseHelper2.addEntry(picDetails);

        }//end of onPostExecute method

    }//end of FavAsync class
}
