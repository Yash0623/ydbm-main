package com.ydbm.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ydbm.R;


public class VersionUpgradeDialog extends AppCompatDialog implements View.OnClickListener {

    private Context mContext;
    Button btn_ok;



    public VersionUpgradeDialog(Context context) {

        super(context);
        this.mContext = context;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vesrion_upgrade_new_design);


        getData();
        findView();
        setData();
        setListner();

    }

    private void getData() {


    }


    private void setData() {
    }

    private void setListner() {
        btn_ok.setOnClickListener(this);
//        btn_cancel.setOnClickListener(this);

    }

    private void findView() {
        btn_ok = (Button) this.findViewById(R.id.btn_ok);
//        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_ok:

                //open playstore
               Intent intent = new Intent(Intent.ACTION_VIEW);
                //Copy App URL from Google Play Store.
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName()+ "&hl=en"));
                mContext.startActivity(intent);

              break;

        }

    }


}
