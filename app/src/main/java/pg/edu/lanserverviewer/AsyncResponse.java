package pg.edu.lanserverviewer;

import java.net.InetAddress;
import java.util.ArrayList;

public interface AsyncResponse {
    void processFinish(ArrayList<InetAddress> result);
}
