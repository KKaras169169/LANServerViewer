//TODO: call this class AFTER search for SAMBA devices on the network and pass arguments dynamically;
// add method onPostExecute to avoid halting IoThread
package pg.edu.lanserverviewer;

import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class SmbConnector extends AsyncTask {
    //TODO: fix logging to non root account
    private String smbPath = "smb://"; //change from hardcoded to dynamic based on search for shares result
    private String smbDir = "DYSK_SIECIOWY/"; //change from hardcoded to dynamic based on menu element selected
    private String list = "";
    private String status = "";
    private String username = "opi";
    private String pass = "opizero123";
    private InetAddress selfIp;

    public SmbConnector(String ip) throws UnknownHostException {
        this.selfIp = InetAddress.getByName(ip);
        this.smbPath = this.smbPath + ip + "/";
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, username, pass);
            final SmbFileExt smb = new SmbFileExt(smbPath + smbDir, auth);

                    SmbFile[] listFiles = new SmbFile[0];
                    try {
                        listFiles = smb.listFiles();
                    } catch (SmbException e) {
                        e.printStackTrace();
                    }

                    for (int element = 0; element < listFiles.length; element++) {
                        list = list + listFiles[element].toString();
                    }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Unable to connect to a SMB share");
            status = "Unable to connect";
        }
        return null;
    }

    public String getList() {
        return list;
    }

    public String getStatusState() {
        return status;
    }
}
