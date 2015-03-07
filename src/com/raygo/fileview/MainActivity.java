package com.raygo.fileview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt = (Button)findViewById(R.id.button1);
        final TextView tv = (TextView)findViewById(R.id.textView1);
        
        Context cxt = this.getBaseContext();
        final OpenFileDialog ofd = new OpenFileDialog(this, ".png;.jpg;", new CallbackBundle() {

			@Override
			public void callback(Bundle bundle) {
				// TODO Auto-generated method stub
				String filepath = bundle.getString("path");
				tv.setText(filepath);
			}
        	
        });
        
        bt.setOnClickListener(new OnClickListener()   
        {  
              
            @Override  
            public void onClick(View v)  
            {  
                ofd.ShowDialog();
            }  
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
