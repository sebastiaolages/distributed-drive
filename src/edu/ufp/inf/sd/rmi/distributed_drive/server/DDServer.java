package edu.ufp.inf.sd.rmi.distributed_drive.server;

import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DDServer {

    private SetupContextRMI contextRMI;
    private DDServiceRI ddServiceRI;

    public static void main(String[] args) {
        DDServer server = new DDServer(args);
        server.rebindService();
    }

    public DDServer(String[] args) {
        try {
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            contextRMI = new SetupContextRMI(this.getClass(), args[0], args[1], new String[]{args[2]});
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private void rebindService() {
        try {
            Registry registry = contextRMI.getRegistry();
            if (registry != null) {
                ddServiceRI = new DDServiceImpl();
                String serviceUrl = contextRMI.getServicesUrl(0);
                registry.rebind(serviceUrl, ddServiceRI);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Service bound at {0}", serviceUrl);
            }
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }
}
