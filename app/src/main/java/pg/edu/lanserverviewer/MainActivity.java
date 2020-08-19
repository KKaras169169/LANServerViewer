//TODO: add option to connect to chosen device, make dynamic layout, add scroll view to list servers properly
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
    Button btnSearch; //used to refresh server list
    TextView statusTxt; //for future use
    ListView serverList; //used to display servers
    ProgressBar searchProgress; //represents search progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);*/

        final ArrayList<InetAddress> serverArray = new ArrayList<>(); //for storing server search results
        final ArrayAdapter serverArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, serverArray); //adapter for the above

        statusTxt = findViewById(R.id.statusText);
        btnSearch = findViewById(R.id.btn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = getIpAddress(); //self IP
                serverArray.clear(); //clear previously stored server list
                serverList.setAdapter(serverArrayAdapter); //set adapter for serverList
                if(!ip.equals("not connected")) {
                    //if (self) connected to network
                    SmbSeeker smbSeeker = new SmbSeeker(ip, new AsyncResponse() {
                        // create new instance of SmbSeeker and prepare asynchronous response
                        @Override
                        public void processFinish(ArrayList<InetAddress> result) {
                            serverArray.clear(); //clear serverList
                            serverArray.addAll(result); //add all search results
                            serverList.setAdapter(serverArrayAdapter); // set adapter
                            saveAddressArray(serverArray);
                        }
                    }, MainActivity.this.searchProgress);
                    smbSeeker.execute(); //execute task
                }
            }
        });

        serverList = findViewById(R.id.serverList); //try to fill serverList with data stored in Shared Preference
        try {
            retrieveAddressArray();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        serverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //allows to click serverList items
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CharSequence toastText; // for use in toast (black popup message)
                String itemIpString = serverList.getItemAtPosition(position).toString(); // retrieve ip of the currently viewed item as String
                try {
                    InetAddress itemIp = (InetAddress.getByName(itemIpString.substring(1))); // get IP address of currently viewed item (device)
                    if(itemIp.isReachable(50)) {
                        // if reachable (pingable) then connect
                        SmbConnector smbConnector = new SmbConnector(serverList.getItemAtPosition(position).toString().substring(1));
                        smbConnector.execute();
                        //TODO: add new Activity here, use returned list.
                    }
                } catch (UnknownHostException e) {
                    // else catch exceptions
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
        searchProgress.setVisibility(View.GONE); // set progress bar's visibility to non-visible when unused
    }

    // method for getting IP address of (self) device
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

    // used to store list of currently known servers to a Shared Preference
    public void saveAddressArray(ArrayList<InetAddress> addresses) {
        if(addresses != null) {
            String preferenceName = "LANServerViewer PREFERENCES"; // Preference name
            SharedPreferences sharedPreferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            for(int i = 0; i < addresses.size(); i++) {
                editor.putString(String.valueOf(i), addresses.get(i).toString());
            }
            editor.commit();
        }
    }

    // used to retrieve list of servers from shared preference
    public ArrayList<InetAddress> retrieveAddressArray() throws UnknownHostException {
        String preferenceName = "LANServerViewer PREFERENCES";
        SharedPreferences sharedPreferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        Map<String,?> mappedAddresses = sharedPreferences.getAll(); // create a map to put into the contents of Shared Preference
        ArrayList<InetAddress> addresses = new ArrayList<>(); // create a list to be returned later
        for(Map.Entry<String,?> entry : mappedAddresses.entrySet()) {
            addresses.add(InetAddress.getByName(entry.getValue().toString().substring(1))); // add each address to the list
        }

        ArrayList<InetAddress> serverArray = new ArrayList<>(); //create those to display the contents of the list
        ArrayAdapter serverArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, serverArray);

        serverArray.clear();
        serverArray.addAll(addresses);
        serverList.setAdapter(serverArrayAdapter);
        return addresses;
    }
}
