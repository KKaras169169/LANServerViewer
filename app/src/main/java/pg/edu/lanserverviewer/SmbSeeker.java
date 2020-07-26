//searching the net for SAMBA devices
package pg.edu.lanserverviewer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

class SmbSeeker {
    private int LoopCurrentIP = 0;
    private int timeout = 50; //ms
    private int port = 445;

    ArrayList<InetAddress> getDevices(String selfIP) {
        ArrayList<InetAddress> ret = new ArrayList<>();
        LoopCurrentIP = 0;
        String ipAddress = "";
        String[] ipAddressArray = selfIP.split("\\.");
        InetAddress currentPingAddress;

        for(int i = 0; i <= 255; i++) {
            try {
                currentPingAddress = InetAddress.getByName(ipAddressArray[0] + "." +
                        ipAddressArray[1] + "." +
                        ipAddressArray[2] + "." + LoopCurrentIP);

                /*if(currentPingAddress.isReachable(50)) {
                    ret.add(currentPingAddress); }*/
                if(isPortOpen(currentPingAddress.toString(), port, timeout)) {
                    ret.add(currentPingAddress);
                } else {
                    System.out.println("No available Samba devices.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            LoopCurrentIP++;
        }
        return ret;
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
}

//Done: Searching the net for devices via IP addresses and open SAMBA ports (445)