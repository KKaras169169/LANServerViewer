//TODO: change the status TextView when no SAMBA devices are found / show appropriate notification
package pg.edu.lanserverviewer;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

class SmbSeeker extends AsyncTask<Void, Void, ArrayList<InetAddress>> {
    private String ip = "";
    private ArrayList<InetAddress> ret;
    private AsyncResponse delegate;

    SmbSeeker(String selfIp, ArrayList<InetAddress> mainRet, AsyncResponse listener) {
        ip = selfIp;
        ret = mainRet;
        this.delegate = listener;
    }

    private void getDevices(String selfIP) {
        int LoopCurrentIP = 0;
        int timeout = 50; //ms
        int port445 = 445;
        int port139 = 139;
        String[] ipAddressArray = selfIP.split("\\.");
        InetAddress currentPingAddress;

        for(int i = 0; i <= 255; i++) {
            try {
                currentPingAddress = InetAddress.getByName(ipAddressArray[0] + "." +
                        ipAddressArray[1] + "." +
                        ipAddressArray[2] + "." + LoopCurrentIP);

                if(currentPingAddress.isReachable(timeout)) {
                    String tempIP = currentPingAddress.toString();
                    tempIP = tempIP.substring(1);
                    if(isPortOpen(tempIP, port445, timeout)) {
                        ret.add(currentPingAddress);
                    } else if(isPortOpen(tempIP, port139, timeout)) {
                        ret.add(currentPingAddress);
                    }
                }
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
            ce.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    protected void onPostExecute(ArrayList<InetAddress> result) {
        delegate.processFinish(result);
    }

    protected ArrayList<InetAddress> doInBackground(Void... voids) {
        getDevices(ip);
        return ret;
    }
}

//Done: Searching the net for devices via IP addresses with open SAMBA ports (445)