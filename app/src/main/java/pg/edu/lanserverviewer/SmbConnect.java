//TODO: call this class AFTER search for SAMBA devices on the network and pass arguments dynamically;
// add method onPostExecute to avoid halting IoThread

package pg.edu.lanserverviewer;
import android.os.AsyncTask;
import java.net.MalformedURLException;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class SmbConnect extends AsyncTask {

    private String smbPath = "smb://192.168.50.162/"; //change from hardcoded to dynamic based on search for shares result
    private String smbDir = "DYSK_SIECIOWY/"; //change from hardcoded to dynamic based on menu element selected
    private String list = "";
    private String status = "";
    private String username = "opi"; //add user account on opi to access samba shares instead of using default account
    private String pass = "opizero123";

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
                        status = "Unable to list files";
                    }
                    status = "Connected";

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