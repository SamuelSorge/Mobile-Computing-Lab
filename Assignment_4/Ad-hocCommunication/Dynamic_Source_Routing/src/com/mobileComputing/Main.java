package com.mobileComputing;

import java.util.Scanner;

public class Main {
    private static Boolean clientMode = false;

    private static String source = "";
    private static String target = "";

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\n Do you want to execute the Flooding including a client (true or false?) \n");
        clientMode = scanner.nextBoolean();

        System.out.print("Which Machine? \n 1: 129.69.210.77 \n 2: 129.69.210.78 \n 3: 129.69.210.1 \n" +
                " 4: 129.69.210.2 \n" +
                " 5: 129.69.210.3");
        String input = System.console().readLine();

        switch (input) {
            case "1": source ="192.168.132.11";
                break;
            case "2": source ="192.168.132.12";
                break;
            case "3": source ="192.168.132.1";
                break;
            case "4": source ="192.168.132.2";
                break;
            case "5": source ="192.168.132.3";
                break;
        }

        if(clientMode) {
            System.out.print("Select Destination! \n 1: 129.69.210.77 \n 2: 129.69.210.78 \n 3: 129.69.210.1 \n" +
                    " 4: 129.69.210.2 \n" +
                    " 5: 129.69.210.3");
            input = System.console().readLine();

            switch (input) {
                case "1": target ="192.168.132.11";
                    break;
                case "2": target ="192.168.132.12";
                    break;
                case "3": target ="192.168.132.1";
                    break;
                case "4": target ="192.168.132.2";
                    break;
                case "5": target ="192.168.132.3";
                    break;
            }
            Thread clientThread = new Thread(ClientThread.getInstance(source, target));
            clientThread.start();
        }
        Thread serverThread = new Thread(ServerThread.getInstance(clientMode, source));
        serverThread.start();

    }
}