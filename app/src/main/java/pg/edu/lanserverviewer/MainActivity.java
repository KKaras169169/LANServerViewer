//TODO: add option to connect to chosen device, make dynamic layout, COMMENT EVERYTHING WHILE YOU STILL REMEMBER
package pg.edu.lanserverviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    Button btnSearch;
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

        statusTxt = findViewById(R.id.statusText);
        btnSearch = findViewById(R.id.btn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: make below code run on a separate thread
                String ip = getIpAddress();
                serverArray.clear();
                serverList.setAdapter(serverArrayAdapter);
                if(!ip.equals("not connected")) {
                    SmbSeeker smbSeeker = new SmbSeeker(ip, new AsyncResponse() {
                        @Override
                        public void processFinish(ArrayList<InetAddress> result) {
                            serverArray.clear();
                            serverArray.addAll(result);
                            serverList.setAdapter(serverArrayAdapter);
                            saveAddressArray(serverArray);
                            //TODO: display menu with list of available devices (clickable tiles) and option to connect to them after tapping
                        }
                    }, MainActivity.this.searchProgress);
                    smbSeeker.execute();
                }
            }
        });

        serverList = findViewById(R.id.serverList);
        try {
            retrieveAddressArray();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        serverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CharSequence toastText = "test";
                String selfIpString = serverList.getItemAtPosition(position).toString();
                try {
                    InetAddress selfIp = (InetAddress.getByName(selfIpString.substring(1)));
                    if(selfIp.isReachable(50)) {
                        //add new Activity here.
                        toastText = serverList.getItemAtPosition(position).toString() + " is reachable.";
                        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (UnknownHostException e) {
                    toastText = serverList.getItemAtPosition(position).toString() + " is unreachable.";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    e.printStackTrace();
                    toast.show();
                } catch (IOException e) {
                    toastText = "unable to ping the address.";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    e.printStackTrace();
                    toast.show();
                }
            }
        });

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

    public void saveAddressArray(ArrayList<InetAddress> addresses) {
        if(addresses != null) {
            String PREFERENCES = "LANServerViewer PREFERENCES";
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            for(int i = 0; i < addresses.size(); i++) {
                editor.putString(String.valueOf(i), addresses.get(i).toString());
            }
            editor.commit();
        }
    }

    public ArrayList<InetAddress> retrieveAddressArray() throws UnknownHostException {
        String PREFERENCES = "LANServerViewer PREFERENCES";
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Map<String,?> mappedAddresses = sharedPreferences.getAll();
        ArrayList<InetAddress> addresses = new ArrayList<>();
        for(Map.Entry<String,?> entry : mappedAddresses.entrySet()) {
            addresses.add(InetAddress.getByName(entry.getValue().toString().substring(1)));
        }

        ArrayList<InetAddress> serverArray = new ArrayList<>();
        ArrayAdapter serverArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, serverArray);

        serverArray.clear();
        serverArray.addAll(addresses);
        serverList.setAdapter(serverArrayAdapter);
        return addresses;
    }
}
