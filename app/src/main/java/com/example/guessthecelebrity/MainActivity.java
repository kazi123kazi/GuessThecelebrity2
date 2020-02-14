package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    int celebChosen=0;
    ImageView imageView;
    int locationOfAnswer=0;
    String[] answers=new String[4];
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    public void celebChoosen(View view)
    {
        if(view.getTag().toString().equals(Integer.toString(locationOfAnswer)))
        {
            Toast.makeText(getApplicationContext(), "Correct !!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Incorrec,It was "+celebNames.get(celebChosen), Toast.LENGTH_LONG).show();
        }
        createNewQuestions();
    }

    public class ImageDownloader extends AsyncTask<String ,Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                InputStream inputStream =connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return myBitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
   //this is for download of web content
    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);

                int data=reader.read();
                while(data!=-1)
                {
                    char current=(char)data;
                    result+=current;
                    data=reader.read();

                }

                return result;

            }

            catch (Exception e)
            {
                e.printStackTrace();
            }







            return null;
        }
    }

    public void createNewQuestions()
    {
        Random random=new Random();
        celebChosen=random.nextInt(celebUrls.size());

        ImageDownloader imageTask=new ImageDownloader();
        Bitmap celebImage;

        try {
            celebImage=imageTask.execute(celebUrls.get(celebChosen)).get();
            imageView.setImageBitmap(celebImage);
            locationOfAnswer=random.nextInt(4);
            int incorrectLocation;

            for (int i=0;i<4;i++)
            {
                if(i==locationOfAnswer)
                {
                    answers[i]=celebNames.get(celebChosen);

                }
                else
                {
                    incorrectLocation=random.nextInt(celebUrls.size());
                    while(incorrectLocation==celebChosen)
                    {
                        incorrectLocation=random.nextInt(celebUrls.size());
                    }
                    answers[i]=celebNames.get(incorrectLocation);

                }
            }
            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.imageView);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        button4=(Button)findViewById(R.id.button4);


        DownloadTask task=new DownloadTask();
        String result=null;

        try {
            result=task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult;
            splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p=Pattern.compile("<img src=\"(.*?)\"");
            Matcher m=p.matcher(splitResult[0]);

            while(m.find())
            {
                celebUrls.add(m.group(1));
            }
            p=Pattern.compile("alt=\"(.*?)\"");
            m=p.matcher(splitResult[0]);

            while(m.find())
            {
                celebNames.add(m.group(1));
            }







        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        createNewQuestions();
    }
}
