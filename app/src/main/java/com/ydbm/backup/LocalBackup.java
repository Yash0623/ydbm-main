package com.ydbm.backup;


import android.content.DialogInterface;
import android.os.Environment;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.ydbm.R;
import com.ydbm.utils.SQLiteHandler;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocalBackup {

  /*  private MainActivity activity;

    public LocalBackup(MainActivity activity) {
        this.activity = activity;
    }

    //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    public void performBackup(final SQLiteHandler db, final String outFileName, final String uid, final String username) {

        Permissions.verifyStoragePermissions(activity);

        final File folder = new File(Environment.getExternalStorageDirectory() + File.separator + activity.getResources().getString(R.string.app_name));

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {

/*            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Backup Name");
            final EditText input = new EditText(activity);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {*/

           // String out = outFileName + uid + ".db";

           // db.backup(out, new File(out), uid, uid, username);

          //  Log.d("APICALL123", folder.getAbsolutePath() + "==" + out);
            //uploadFileToServer(newFolder, uid, uid, username);
            //}
         /*   });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        } else
            Toast.makeText(activity, "Unable to create directory. Retry", Toast.LENGTH_SHORT).show();
    }


    //ask to the user what backup to restore
    public void performRestore(final SQLiteHandler db) {

        Permissions.verifyStoragePermissions(activity);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + activity.getResources().getString(R.string.app_name));
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item);
            for (File file : files)
                arrayAdapter.add(file.getName());

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
            builderSingle.setTitle("Restore:");
            builderSingle.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                db.importDB(files[which].getPath(),"");
                            } catch (Exception e) {
                                Toast.makeText(activity, "Unable to restore. Retry", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            builderSingle.show();
        } else
            Toast.makeText(activity, "Backup folder not present.\nDo a backup before a restore!", Toast.LENGTH_SHORT).show();
    }

    private void uploadFileToServer(File folder, String out, String uid, String username) {
        Log.d("APICALL", folder.getAbsolutePath() + "==" + out + "====" + uid);
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", folder.getName(),
                            RequestBody.create(folder, MediaType.parse("text")))
                    .addFormDataPart("fid", uid)
                    .addFormDataPart("mobile", username)
                    .addFormDataPart("is_mobile", "APP")
                    .build();

            Request request = new Request.Builder()
                    .url("http://yatradham.org/demosite/restapi.php?apiname=uploaddbfile")
                    .post(requestBody)
                    .build();


            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(final Call call, final IOException e) {
                    e.printStackTrace();
                    // Handle the error
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
                    Log.d("ADKSJKDHADJADHDJ", response.body().string() + "===");
                    if (!response.isSuccessful()) {
                        // Handle the error
                    }
                    // Upload successful
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle the error
        }
        //return false;
    }
*/
}
