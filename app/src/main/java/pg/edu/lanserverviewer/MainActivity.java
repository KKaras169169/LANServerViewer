//TODO: change main class to be responsible for the menu only (implement menu)
package pg.edu.lanserverviewer;

import androidx.appcompat.app.AppCompatActivity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
                //TODO: make below code run on a separate thread
                String ip = getIpAddress();
                ArrayList<InetAddress> ret;
                if(!ip.equals("not connected")) {
                    SmbSeeker smbSeeker = new SmbSeeker();
                    ret = smbSeeker.getDevices(ip);
                    System.out.println(ret.toString());
                }
                /*try {
                    StartTask(v);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    public void StartTask(View v) throws ExecutionException, InterruptedException {
        SmbConnect abt = new SmbConnect();
        abt.execute().get();

        list = abt.getList();
        status = abt.getStatusState();
        fileList.setText(list);
        statusTxt.setText(status);
    }

    public String getIpAddress() {
        WifiManager wm = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wi = null;
        if (wm != null) {
            wi = wm.getConnectionInfo();
        } else {
            System.out.println("WiFi Manager is Null");
        }
        int ipAddress = wi.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        if(ip.equals("0.0.0.0"))
            return "not connected";
        else
            return ip;
    }
}
