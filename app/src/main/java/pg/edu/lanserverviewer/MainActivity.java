package pg.edu.lanserverviewer;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import jcifs.smb.*;
import static androidx.lifecycle.Lifecycle.State.RESUMED;


public class MainActivity extends AppCompatActivity {
    Button btnSearch;
    TextView fileList;
    TextView statusTxt;
    String smbPath = "smb://192.168.50.162/";
    String smbDir = "DYSK_SIECIOWY/";
    String list = "";
    String status = "";
    String username = "opi";
    String pass = "opizero123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        fileList = findViewById(R.id.textView);
        statusTxt = findViewById(R.id.statusText);
        btnSearch = findViewById(R.id.btn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StartTask(v);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void StartTask(View v) throws ExecutionException, InterruptedException {
        AsyncBackgroundTask abt = new AsyncBackgroundTask();
        abt.execute().get();

        list = abt.getList();
        status = abt.getStatusState();
        fileList.setText(list);
        statusTxt.setText(status);
    }
}
