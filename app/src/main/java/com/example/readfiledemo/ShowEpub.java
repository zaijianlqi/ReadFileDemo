package com.example.readfiledemo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class ShowEpub extends AppCompatActivity  {
     TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_epub);
        try {
            EpubReader epubReader=new EpubReader();
            Book book=null;
            String line1=null;
            String line2=null;
            File SDPath= Environment.getExternalStorageDirectory();
            String filepath =SDPath.getPath()+"/Documents/1.epub";
           textview=(TextView) findViewById(R.id.show_text);
            textview.setMovementMethod(new ScrollingMovementMethod(){

            });
           InputStream in = new FileInputStream(filepath);
            book=epubReader.readEpub(in);
            List<Resource> resources=book.getContents();

            for(Resource resource:resources)
            {
                //Resource resource=book.getSpine().getResource(1);
                InputStreamReader inputStream = new InputStreamReader(resource.getInputStream(), "UTF-8");
                BufferedReader bufferedReader=new BufferedReader(inputStream);

                while ((line1=bufferedReader.readLine())!=null)
                {
                    line2=line2+line1;

                    Log.d("ShowEpub", line1);
                }
               // line2="<img src='https://www.google.com.hk/intl/zh-CN/images/logo_cn.png' />"+line2;
                final Html.ImageGetter imageGetter = new Html.ImageGetter() {
                     @Override
                    public Drawable getDrawable(String source) {
                                           Drawable drawable = null;
                                           drawable = Drawable.createFromPath(source);  // Or fetch it from the URL
                                          // Important
                                          drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                                                        .getIntrinsicHeight());
                                         return drawable;
                                    }

                };

                Spanned sp = Html.fromHtml(line2);

                textview.setText(sp);
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static String StripHT(String strHtml) {
        String txtcontent = strHtml.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
        txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
        return txtcontent;
    }
}
