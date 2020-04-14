package com.example.comicsbangla;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Read_PDF extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read__p_d_f);
        final PDFView readpdf=findViewById(R.id.pdfView);
        Uri pdfuri=getIntent().getData();
        class RetrivePdfStream extends AsyncTask<String, Void, InputStream> {

            @Override
            protected InputStream doInBackground(String... strings) {
                InputStream inputStream = null;
                try {
                    URL uri = new URL(strings[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
                    if (urlConnection.getResponseCode() == 200) {
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    }
                } catch (IOException e) {
                    return null;
                }
                return inputStream;
            }

            @Override
            protected void onPostExecute(InputStream inputStream) {
                super.onPostExecute(inputStream);
                readpdf.fromStream(inputStream).load();
            }
        }
        new RetrivePdfStream().execute(pdfuri.toString());
    }
}



