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

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    Button btnSearch;
    TextView fileList;
    TextView statusTxt;

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
                final ArrayList<InetAddress>[] ret = new ArrayList[]{new ArrayList<>()};
                if(!ip.equals("not connected")) {

                    SmbSeeker smbSeeker = new SmbSeeker(ip, ret[0], new AsyncResponse() {
                        @Override
                        public void processFinish(ArrayList<InetAddress> result) {
                            ret[0] = result;
                            //TODO: display menu with list of available devices (clickable tiles) and option to connect to them after tapping
                        }
                    });
                    smbSeeker.execute();
                }
            }
        });
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

    @Override
    public void processFinish(ArrayList<InetAddress> ret) {
        if(!ret.isEmpty()) {
            for(int i = 0; i < ret.size(); i++) {
                //TODO: display menu with list of available devices and option to connect to them after tapping
            }
        }
        System.out.println(ret.toString());
    }
}
