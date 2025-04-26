package edu.ufp.inf.sd.rmi.distributed_drive.client;

import edu.ufp.inf.sd.rmi.distributed_drive.server.DDFactoryRI;
import edu.ufp.inf.sd.rmi.distributed_drive.server.DDSessionRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DDClient {

    private SetupContextRMI contextRMI;
    private DDFactoryRI ddFactoryRI;

    public static void main(String[] args) {
        DDClient client = new DDClient(args);
        client.lookupService();
        client.showMenu();
    }

    public DDClient(String[] args) {
        try {
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            contextRMI = new SetupContextRMI(this.getClass(), args[0], args[1], new String[]{args[2]});
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private void lookupService() {
        try {
            Registry registry = contextRMI.getRegistry();
            String serviceUrl = contextRMI.getServicesUrl(0);
            ddFactoryRI = (DDFactoryRI) registry.lookup(serviceUrl);
        } catch (RemoteException | NotBoundException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private void showMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("1 - Registar");
        System.out.println("2 - Login");
        int op = sc.nextInt();
        sc.nextLine();

        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        try {
            if (op == 1) {
                boolean success = ddFactoryRI.register(username, password);
                System.out.println(success ? "Registado com sucesso!" : "Username já existe.");
            } else {
                DDSessionRI session = ddFactoryRI.login(username, password);
                if (session != null) {
                    System.out.println("Login com sucesso! Bem-vindo " + session.getUsername());

                    System.out.println("Ficheiros locais:");
                    for (String file : session.listLocalFiles()) {
                        System.out.println(" - " + file);
                    }

                    // NOVO BLOCO ADICIONADO AQUI
                    System.out.println("Deseja criar um novo ficheiro? (s/n)");
                    String opcao = sc.nextLine();

                    if (opcao.equalsIgnoreCase("s")) {
                        System.out.print("Nome do ficheiro: ");
                        String nome = sc.nextLine();
                        System.out.print("Conteúdo: ");
                        String conteudo = sc.nextLine();
                        session.createFile(nome, conteudo);
                        System.out.println("Ficheiro criado e sincronizado com o servidor.");
                    }

                } else {
                    System.out.println("Credenciais inválidas.");
                }
            }
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

}
