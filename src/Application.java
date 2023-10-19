package src;

import constants.ErrorMsg;
import src.http.HTTPServer;

import java.io.IOException;

public class Application {


    final private static int PCAP_PAQUET_HEADER_BYTES_LENGTH = 16;

    public static void printApplicationName(String message){
System.out.println("██╗  ██╗████████╗████████╗██████╗     ███████╗███████╗██████╗ ██╗   ██╗███████╗██████╗ ");
System.out.println("██║  ██║╚══██╔══╝╚══██╔══╝██╔══██╗    ██╔════╝██╔════╝██╔══██╗██║   ██║██╔════╝██╔══██╗");
System.out.println("███████║   ██║      ██║   ██████╔╝    ███████╗█████╗  ██████╔╝██║   ██║█████╗  ██████╔╝");
System.out.println("██╔══██║   ██║      ██║   ██╔═══╝     ╚════██║██╔══╝  ██╔══██╗╚██╗ ██╔╝██╔══╝  ██╔══██╗");
System.out.println("██║  ██║   ██║      ██║   ██║         ███████║███████╗██║  ██║ ╚████╔╝ ███████╗██║  ██║");
System.out.println("╚═╝  ╚═╝   ╚═╝      ╚═╝   ╚═╝         ╚══════╝╚══════╝╚═╝  ╚═╝  ╚═══╝  ╚══════╝╚═╝  ╚═╝");
        System.out.println(message + "\n\n");

    }

    public static void main(String[] args) throws IOException, InterruptedException {


        HTTPServer server;

        printApplicationName("Listening on port:" + 1111);

        new HTTPServer();


    }

}
