package com.xz.bing;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ShareMyself extends AppCompatActivity {
    private Button shareLink;

    @Override
    protected void onDestroy() {
        shareLink = null;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_myself);


        shareLink =  findViewById(R.id.share_link);
        shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clip  = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clip.setText("http://www.xzlyf.club/");
                Toast.makeText(ShareMyself.this,"快去分享给好友吧！",Toast.LENGTH_LONG).show();
            }
        });

    }
}
