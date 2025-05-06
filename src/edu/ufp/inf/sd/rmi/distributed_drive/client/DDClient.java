package edu.ufp.inf.sd.rmi.distributed_drive.client;

import edu.ufp.inf.sd.rmi.distributed_drive.server.DDFactoryRI;
import edu.ufp.inf.sd.rmi.distributed_drive.server.DDSessionRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;
import edu.ufp.inf.sd.rmi.distributed_drive.server.SubjectRI;
import edu.ufp.inf.sd.rmi.distributed_drive.server.ObserverRI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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

                    // 🟢 Attach do observer após login
                    ObserverRI myObserver = new ClientObserverImpl();
                    session.getSubject().attach(myObserver);

                    while (true) {
                        System.out.println("Escolha uma opção:");
                        System.out.println("1 - Criar ficheiro");
                        System.out.println("2 - Apagar ficheiro");
                        System.out.println("3 - Listar ficheiros locais");
                        System.out.println("4 - Renomear ficheiro");
                        System.out.println("5 - Partilhar ficheiro com outro utilizador");
                        System.out.println("6 - Criar pasta");
                        System.out.println("7 - Apagar pasta");
                        System.out.println("8 - Renomear pasta");
                        System.out.println("9 - Listar estrutura completa");
                        System.out.println("0 - Sair");

                        int choice = sc.nextInt();
                        sc.nextLine();

                        if (choice == 1) {
                            System.out.print("Nome do ficheiro: ");
                            String nome = sc.nextLine();
                            System.out.print("Conteúdo: ");
                            String conteudo = sc.nextLine();
                            session.createFile(nome, conteudo);
                            System.out.println("Ficheiro criado e sincronizado.");
                        } else if (choice == 2) {
                            System.out.print("Nome do ficheiro a apagar: ");
                            String nome = sc.nextLine();
                            session.deleteFile(nome);
                            System.out.println("Ficheiro apagado localmente e no servidor.");
                        } else if (choice == 3) {
                            System.out.println("Ficheiros locais:");
                            for (String file : session.listLocalFiles()) {
                                System.out.println(" - " + file);
                            }
                        } else if (choice == 4) {
                            System.out.print("Nome atual do ficheiro: ");
                            String oldName = sc.nextLine();
                            System.out.print("Novo nome do ficheiro: ");
                            String newName = sc.nextLine();
                            session.renameFile(oldName, newName);
                            System.out.println("Ficheiro renomeado localmente e no servidor.");
                        } else if (choice == 5) {
                            System.out.print("Nome do ficheiro a partilhar: ");
                            String nome = sc.nextLine();
                            System.out.print("Nome do utilizador destino: ");
                            String destino = sc.nextLine();
                            session.shareFile(nome, destino);
                            System.out.println("Ficheiro partilhado com sucesso!");
                        } else if (choice == 6) {
                            System.out.print("Nome da nova pasta: ");
                            String nome = sc.nextLine();
                            session.createFolder(nome);
                            System.out.println("Pasta criada com sucesso.");
                        } else if (choice == 7) {
                            System.out.print("Nome da pasta a apagar: ");
                            String nome = sc.nextLine();
                            session.deleteFolder(nome);
                            System.out.println("Pasta apagada com sucesso.");
                        } else if (choice == 8) {
                            System.out.print("Nome atual da pasta: ");
                            String oldName = sc.nextLine();
                            System.out.print("Novo nome da pasta: ");
                            String newName = sc.nextLine();
                            session.renameFolder(oldName, newName);
                            System.out.println("Pasta renomeada com sucesso.");
                        } else if (choice == 9) {
                            System.out.println("Conteúdo completo:");
                            for (String path : session.listAllLocalContent()) {
                                System.out.println(" - " + path);
                            }
                        } else if (choice == 0) {
                            System.out.println("A sair...");
                            break;
                        } else {
                            System.out.println("Opção inválida.");
                        }
                    }

                } else {
                    System.out.println("Credenciais inválidas.");
                }
            }
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    // Classe interna que implementa ObserverRI
    public class ClientObserverImpl extends UnicastRemoteObject implements ObserverRI {

        public ClientObserverImpl() throws RemoteException {
            super();
        }

        @Override
        public void update(String message) throws RemoteException {
            System.out.println("[NOTIFICAÇÃO] " + message);
        }
    }

}
