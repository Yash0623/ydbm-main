package com.ydbm.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ydbm.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadDBFile {

    Context context;
    View v;
    File directory;
    private ProgressDialog pDialog;

    private static String imageName = "";
    SQLiteHandler sqLiteHandler1;
    private String path2, filderNm;

    public void downloadFile(Context c, String url, View v, SQLiteHandler sqLiteHandler) {
        context = c;
        sqLiteHandler1 = sqLiteHandler;

        try {
            URL myURL = new URL(url);
            imageName = getFileNameFromUrl(myURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d("IFFILE", imageName + " " + filderNm);
        if (!url.isEmpty()) {
            new DownloadFileFromURL().execute(url);
        } else {
            Toast.makeText(context, "Attachment not available", Toast.LENGTH_SHORT).show();
        }
        this.v = v;
    }

    public static String getFileNameFromUrl(URL url) {
        String urlPath = url.getPath();
        return urlPath.substring(urlPath.lastIndexOf('/') + 1);
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Downloading... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
             pDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            String path = null;
            try {
                URL url = new URL(f_url[0]);

                URLConnection conection = url.openConnection();
                conection.connect();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                Log.d("IFFILE", "doinbackground");
                // Output stream to write file


                directory = new File(Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.app_name));

                boolean success = true;
                if (!directory.exists())
                    success = directory.mkdirs();
                path2 = directory.getAbsolutePath();
                String nm = getFileNameFromUrl(url);
                Log.d("PATH", path2.toString());
                //     Log.d("PATH", path.toString() + " " + nm + " 12" + imageName + " 122");

                OutputStream output = new FileOutputStream(directory + "/" + nm);

                Log.d("PATHDIRECTORY", directory + "/" + nm);
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);

                }
                output.flush();

                output.close();
                input.close();
            } catch (Exception e) {
                // Log.d("IFFILE","ERROR");
                pDialog.dismiss();
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            Snackbar.make(v, "Backup File Downloaded Successfully!!", Snackbar.LENGTH_LONG).show();
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name));
            if (folder.exists()) {
                Log.d("KDHASJHDJDJHJDJD", "4");
                File file = folder.getAbsoluteFile();
                sqLiteHandler1.importDB(file.getAbsolutePath() + "/" + imageName, "");

            } else {
                Toast.makeText(context, "Backup folder not present.\nDo a backup before a restore!", Toast.LENGTH_SHORT).show();
            }
          /* File folder = new File(Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name));
            if (folder.exists()) {

                File file = folder.getAbsoluteFile();
                sqLiteHandler1.importDB(file.getAbsolutePath()+"/"+imageName,imageName);
            }
            else {
                Toast.makeText(context, "Backup folder not present.\nDo a backup before a restore!", Toast.LENGTH_SHORT).show();
            }*/

            //   Snackbar.make(v, "PDF Downloaded Successfully!!", Snackbar.LENGTH_LONG).show();
             pDialog.dismiss();
        }

        public void openFolder(String s, String imageName) {
            File file = new File(s + imageName);
            Log.d("GAGHG", file.getAbsolutePath() + "----");
            Uri apkURI = FileProvider.getUriForFile(
                    context,
                    context
                            .getPackageName() + ".share", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkURI, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            // c.startActivityForResult(intent, 10);
        }
    }
}
