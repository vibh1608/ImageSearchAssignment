package com.example.vibhor.imageshow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import static android.database.sqlite.SQLiteDatabase.CREATE_IF_NECESSARY;

/**
 * Created by Vibhor on 20-04-2018.
 */

public class DataBaseHelper extends SQLiteOpenHelper
{
    //Database name
    private static final String DATABASE_NAME = "database_name";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    //table name
    public static final String TABLE_NAME = "Pictures";
    public String query = "";

    //column names
    private static final String KEY_IMAGE = "image_data";
    private static final String KEY_NAME = "image_name";

    //constructor
    public DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        query = "CREATE TABLE "+ TABLE_NAME+ " ( "+KEY_NAME+" TEXT , " +KEY_IMAGE+ " BLOB )";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

    //method to return number of rows in table
    public int rowcount()
    {
        int rowcount=0;

        //open connection to read database
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(false,TABLE_NAME,null,null,null,null,null,null,null);

        //moving cursor to first row of database
        cursor.moveToFirst();

        //counting number of rows and storing in row variable
        rowcount=cursor.getCount();

        cursor.close();

        //returning number of rows
        return rowcount;
    }

    //method to add pic in favoriate movielist
    public void addEntry( PicDetails picDetails) throws SQLiteException
    {
        //creating instance of SQLiteDatabase class
        SQLiteDatabase database = this.getWritableDatabase();

        //creating content values object to store details
        ContentValues cv = new  ContentValues();

        //Converting Bitmap image to byte array.
        Bitmap bitmap=picDetails.getStorepic();
        byte[] img;

        if(bitmap!=null)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            img = byteArrayOutputStream.toByteArray();
        }
        else
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            img = byteArrayOutputStream.toByteArray();
        }

        cv.put(KEY_NAME,picDetails.getPicName());

        //inserting image into content values
        cv.put(KEY_IMAGE,img);

        //inserting movie details into table thorugh contentvalues object
        database.insert( TABLE_NAME, null, cv );
    }

    //method to get the all favmovielist
    public ArrayList<PicDetails> getAllpics(String name)
    {
        //creating arraylist to get the stored favmovielist
        ArrayList<PicDetails> list = new ArrayList<>();

        //creating instance of SQLiteDatabase class
        SQLiteDatabase database = this.getReadableDatabase();

        //query to fetch the favmovielist from table
        String query = "select * from "+TABLE_NAME;

        //Creating the cursor by using the rawQuery method of db.
        Cursor cursor = database.rawQuery(query,null);

        //creating storingData object to get the details in Storing data form
        PicDetails picDetails = null;

        //checking if table is not empty
        if (cursor!=null)
        {
            //moving cursor to the first row of table
            cursor.moveToFirst();

            do
            {
                if(cursor.getString(0).equals(name))
                {
                    //initialising object storingData
                    picDetails = new PicDetails();

                    //Converting byte array into Bitmap Image.
                    byte[] img=cursor.getBlob(1);
                    picDetails.setStorepic(BitmapFactory.decodeByteArray(img,0,img.length));

                    //adding storingData object into arraylist of favmovielist
                    list.add(picDetails);
                }

            }while (cursor.moveToNext());
        }

        //closing cursor
        cursor.close();

        //returning arraylist of favmovielist
        return list;

    }//end of method

}
