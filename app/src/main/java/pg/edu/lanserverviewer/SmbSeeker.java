//searching the net for SAMBA devices
package pg.edu.lanserverviewer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class SmbSeeker {
    private int LoopCurrentIP = 0;

    public ArrayList<InetAddress> getDevices(String selfIP) {
        ArrayList<InetAddress> ret = new ArrayList<InetAddress>();
        LoopCurrentIP = 0;
        String ipAddress = "";
        String[] ipAddressArray = selfIP.split("\\.");
        InetAddress currentPingAddress;

        for(int i = 0; i <= 255; i++) {
            try {
                currentPingAddress = InetAddress.getByName(ipAddressArray[0] + "." +
                        ipAddressArray[1] + "." +
                        ipAddressArray[2] + "." + Integer.toString(LoopCurrentIP));

                if(currentPingAddress.isReachable(50)) {
                    ret.add(currentPingAddress);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            LoopCurrentIP++;
        }
        
        //TODO: add port check here

        return ret;
    }
}
