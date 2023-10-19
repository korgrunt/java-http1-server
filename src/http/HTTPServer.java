package src.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HTTPServer {

    private static String buildContentType(String filename) {
        if(filename.contains(".jpeg") || filename.contains(".jpg")){
            return "image/jpeg";
        } else if (filename.contains(".pdf")){
            return "application/pdf";
        } else if (filename.contains(".png")) {
            return "image/png";
        }

        return "text/html";
    }

    private static void sendReponseWithoutContent(String status, String message, Socket socket) {
        try {

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), false);
            String content = "\r\n";
            int content_length = content.getBytes().length;
            writer.print("HTTP/1.1 " + status + " " + message + "\r\n");
            writer.print("Content-Type: text/html\r\n");
            writer.print("Connection: close\r\n");
            writer.print("Content-Length:" + content_length + "\r\n");
            writer.print("\r\n");
            writer.print(content);
            writer.flush();

        } catch (IOException err) {
            System.out.println("Error ioxception");
        }
    }

    private static void sendReponseWithContent(String status, String message, String contentType, String content, Socket socket) {
        try {

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), false);

            int content_length = content.getBytes().length;
            writer.print("HTTP/1.1 " + status + " " + message + "\r\n");
            writer.print("Content-Type: " + contentType + "\r\n");
            writer.print("Connection: close\r\n");
            writer.print("Content-Length:" + content_length + "\r\n");

            writer.print("\r\n");
            writer.print(content);
            writer.flush();
        } catch (IOException err) {
            System.out.println("Error ioxception");
        }
    }

    private static void sendReponseWithContentFile(String status, String message, String contentType, byte[] content, Socket socket) {
        try {

            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

            long content_length = content.length;
            dOut.writeBytes("HTTP/1.1 " + status + " " + message + "\r\n");
            dOut.writeBytes("Content-Type: " + contentType + "\r\n");
            dOut.writeBytes("Connection: close\r\n");
            dOut.writeBytes("Content-Length:" + content_length + "\r\n");
            dOut.writeBytes("\r\n");
            dOut.write(content);

            dOut.flush();

        } catch (IOException err) {
            System.out.println("Error ioxception");
            System.out.println(err);
        }
    }

    private static void sendResponseWithFile(String filename, Socket socket) {
        System.out.println(filename);
        Path filePath = Path.of("./resources" + filename);


        if (filename.contains(".html")) {
            try {

                String str = Files.readString(filePath);
                sendReponseWithContent("200", "ok", buildContentType(filename), str, socket);
            } catch (NoSuchFileException err) {
                System.out.println("NOT FOUND");
                sendReponseWithoutContent("404", "Ressource not found", socket);

            } catch (Exception err) {
                System.out.println("ERROR FOUND");
                System.out.println(err);
                sendReponseWithoutContent("500", "Internal server error", socket);

            }
        } else if (filename.contains(".jpeg") || filename.contains(".jpg") || filename.contains(".pdf") || filename.contains(".png")) {

            try {

                byte[] bytes = Files.readAllBytes(filePath);

                sendReponseWithContentFile("200", "ok", buildContentType(filename), bytes, socket);
            } catch (NoSuchFileException err) {
                System.out.println("NOT FOUND");
                sendReponseWithoutContent("404", "Ressource not found", socket);

            } catch (Exception err) {
                System.out.println("ERROR FOUND");
                System.out.println(err);
                sendReponseWithoutContent("500", "Internal server error", socket);

            }
        } else {
            sendReponseWithContent("400", "Bad request, we not support other file format than pdf, jpg, jpeg, png and html", "text/html", "", socket);

        }


    }

    private static void analyseRequest(String request, Socket socket) {

        List<String> firstLineHttp = Arrays.asList(request.split(" "));

        if (request.contains("/") && request.contains(".")) {

            sendResponseWithFile(firstLineHttp.get(1), socket);
        }
    }

    private static void manageRequest(Socket socket, String requestStr) {

        List<String> request;

        request = Arrays.asList(requestStr.split("\r"));

        if (request.get(0).contains("GET") && request.get(0).contains("HTTP/1.1")) {
            boolean hostIsPresent = false;
            System.out.println("Print request below:");
            for(int i = 0; i < request.size(); i++){
                System.out.println(request.get(i));
                if(request.get(i).toLowerCase().contains("host")){
                    hostIsPresent = true;
                }
            }
            if(hostIsPresent){
                analyseRequest(request.get(0), socket);
            } else {
                sendReponseWithoutContent("400", "BadRequest", socket);
            }
        } else {
            sendReponseWithoutContent("405", "Method not allowed", socket);
        }

    }

    public HTTPServer() throws IOException, InterruptedException {

        try {
            //On installe le combine sur le numero de telephone
            ServerSocket serversocket = new ServerSocket(1111);

            int i = 0;
            while (i < 2000000) {

                Thread.sleep(500);
                System.out.println("Waiting for the client request");
                //creating socket and waiting for client connection
                Socket socket = serversocket.accept();


                //read from socket to ObjectInputStream object
                InputStream inputStream = socket.getInputStream();


                try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                    int c = 0;

                    StringBuilder textBuilder = new StringBuilder();

                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char) c);

                        if (c == '\n') {
                            String requestStr = textBuilder.toString();

                            if (requestStr.endsWith("\r\n\r\n")) {
                                manageRequest(socket, requestStr);
                            }
                        }
                    }
                    socket.close();
                }
                i++;

            }

            serversocket.close();
            //terminate the server if client sends exit request

            //System.out.println("Shutting down Socket server!!");
            //close the ServerSocket object

            //On raccroche

        } catch (IOException ioErr) {

            System.out.println("Error io");
            System.out.println(ioErr);


        } catch (Exception err) {

            System.out.println("Error ");
            System.out.println(err);

        }


    }
}
