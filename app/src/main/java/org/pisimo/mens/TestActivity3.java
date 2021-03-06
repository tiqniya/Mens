package org.pisimo.mens;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class TestActivity3 extends AppCompatActivity {
    volatile Handler  handler = new Handler();
    volatile ProgressBar pb = null;
    volatile ImageView imgv = null;
    volatile TextView t = null;
    volatile TextView testName = null;
    volatile TextView topText = null;
    volatile EditText nameInput = null;
    volatile Button checkName = null;

    volatile int sharedI = 0;
    volatile boolean testTime = true;
    volatile String answer = "";

    //hamming_distance not best implementation because it has problem with misalignment
    public static float matchNames(String a,String b){
        float diff = 0;
        if(a.length() < b.length())b = b.substring(0,a.length());
        while(a.length() > b.length())b += "7";

        for(int i = 0;i != a.length();i++) if(a.charAt(i) != b.charAt(i)) diff+=1;
        diff = diff / a.length();
        return (diff == 1)? 0 : (float) 1-(diff);
    }

    public void goToNextTest(float differenceScore)  {
        //Create scores file because this is the first test
        try {
            FileOutputStream fos = openFileOutput("scores.txt", Context.MODE_APPEND);
            String out = differenceScore +"\n";
            fos.write(out.getBytes());
            Toast.makeText(TestActivity3.this,differenceScore*100+"%",Toast.LENGTH_SHORT).show();
            fos.close();
            Intent test2 = new Intent(TestActivity3.this, TestActivity6.class);
            startActivity(test2);
        }catch (Exception e){
        }
        testTime = true;
        finish();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);

        //Load image
        String[] imgList = {"curie.png","dirac.jpg","heisenberg.jpg","mileva.jpg","noether.jpg","planck.jpg"};
        String[] names   = {"Marie Curie","Paul Dirac","Werner Heisenberg","Mileva Maric","Emmy Noether","Max Planck"};
        imgv = (ImageView)findViewById(R.id.imageView); //Show image
        t = (TextView)findViewById(R.id.textView25);
        topText = (TextView)findViewById(R.id.textView24);
        nameInput = (EditText)findViewById(R.id.nameInput);
        checkName = (Button)findViewById(R.id.checkName);
        testName = (TextView)findViewById(R.id.textView23);


        Bitmap b = Bitmap.createBitmap(165,250, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Paint p = new Paint();



        AssetManager am = getAssets(); //Load icons from asset
        Random r = new Random();
        int pick = r.nextInt(6);   //Pick a random scientist

        answer = names[pick].toLowerCase().replace(" ",""); //remove uppercase chars and space


        try {
            InputStream f = am.open("people/"+imgList[pick]);
            Bitmap exp = BitmapFactory.decodeStream(f);
            c.drawBitmap(exp, 1, 1, p);
            t.setText(names[pick]);
        } catch (IOException e) {
            System.err.println(e);
        }

        imgv.setImageBitmap(b);

        checkName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(testTime) {
                    String inputText = nameInput.getText().toString().toLowerCase().replaceAll(" ", "");
                    goToNextTest(matchNames(answer, inputText));
                }
            }
        });

        //Progress bar time keeper:
        pb = (ProgressBar)findViewById(R.id.timeProgress);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 6;i != -1;i--){
                    sharedI = i;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(Build.VERSION.SDK_INT > 24) {
                                pb.setProgress(sharedI * 20,true);
                            }
                            else pb.setProgress(sharedI * 20);
                            //Removed due to compatibility issues
                            // pb.setProgress((int)(sharedI*20),true);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    }catch (Exception e){
                        System.err.println(e);
                    }
                }
                Intent test4 = new Intent(TestActivity3.this,TestActivity4.class);
                startActivity(test4);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.INVISIBLE);
                        t.setVisibility(View.INVISIBLE);
                        testName.setVisibility(View.VISIBLE);
                        checkName.setVisibility(View.VISIBLE);
                        nameInput.setVisibility(View.VISIBLE);
                        topText.setText("What is the name?");

                        testTime = true;

                    }
                });

            }
        }).start();
    }
}
