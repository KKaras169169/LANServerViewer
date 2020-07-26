//test class

package pg.edu.lanserverviewer;

import java.net.MalformedURLException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class SmbFileExt extends SmbFile {
    public SmbFileExt(String url, NtlmPasswordAuthentication auth) throws MalformedURLException {
        super(url, auth);
    }

    public boolean getConnectionStatus() {
        return this.connected;
    }
}
