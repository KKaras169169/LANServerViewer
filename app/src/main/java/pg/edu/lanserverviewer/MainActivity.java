//TODO: add option to connect to chosen device, add user preference to store found devices, make dynamic layout
package pg.edu.lanserverviewer;

import androidx.appcompat.app.AppCompatActivity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    Button btnSearch;
    TextView fileList;
    TextView statusTxt;
    ListView serverList;
    ProgressBar searchProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        final ArrayList<InetAddress> serverArray = new ArrayList<>();
        final ArrayAdapter serverArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, serverArray);

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
                            serverArray.clear();
                            serverArray.addAll(result);
                            serverList.setAdapter(serverArrayAdapter);
                            //TODO: display menu with list of available devices (clickable tiles) and option to connect to them after tapping
                        }
                    }, MainActivity.this.searchProgress);
                    smbSeeker.execute();
                }
            }
        });

        serverList = findViewById(R.id.serverList);
        searchProgress = findViewById(R.id.searchProgress);
        searchProgress.setVisibility(View.GONE);

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
    public void processFinish(ArrayList<InetAddress> ret) {}
}
