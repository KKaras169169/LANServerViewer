//TODO: change the status TextView when no SAMBA devices are found / show appropriate notification
package pg.edu.lanserverviewer;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

class SmbSeeker extends AsyncTask<Void, Integer, ArrayList<InetAddress>> {
    private String ip = "";
    private ArrayList<InetAddress> ret;
    private AsyncResponse delegate;
    private ProgressBar progressBar;

    SmbSeeker(String selfIp, AsyncResponse listener, ProgressBar mainProgressBar) {
        ip = selfIp;
        ret = new ArrayList<>();
        this.delegate = listener;
        progressBar = mainProgressBar;
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void getDevices(String selfIP) {
        int LoopCurrentIP = 2;
        int timeout = 50; //ms
        int port445 = 445;
        String[] ipAddressArray = selfIP.split("\\.");
        InetAddress currentPingAddress;

        for(int i = 2; i <= 255; i++) {
            try {
                currentPingAddress = InetAddress.getByName(ipAddressArray[0] + "." +
                        ipAddressArray[1] + "." +
                        ipAddressArray[2] + "." + LoopCurrentIP);

                if(currentPingAddress.isReachable(timeout)) {
                    String tempIP = currentPingAddress.toString();
                    tempIP = tempIP.substring(1);
                    if(isPortOpen(tempIP, port445, timeout)) {
                        ret.add(currentPingAddress);
                    }
                }
                publishProgress(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LoopCurrentIP++;
        }
    }

    private static boolean isPortOpen(String ip, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();
            return true;
        } catch(ConnectException ce){
            System.out.println("Connection refused: " + ip + " PORT: " + port);
            return false;
        } catch (Exception ex) {
            System.out.println("Unable to connect to: " + ip + " PORT: " + port);
            return false;
        }
    }

    protected void onPostExecute(ArrayList<InetAddress> result) {
        progressBar.setVisibility(View.GONE);
        delegate.processFinish(result);
    }

    protected ArrayList<InetAddress> doInBackground(Void... voids) {
        getDevices(ip);
        return ret;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if(this.progressBar != null) {
            progressBar.setProgress(progress[0]);
        }
    }
}

//Done: Searching the net for devices via IP addresses with open SAMBA ports (445)