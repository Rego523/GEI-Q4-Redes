package es.udc.redes.webserver;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;


public class ServerThread extends Thread {

    private final Socket socket;

    public ServerThread(Socket s) {
        // Store the socket s
        this.socket = s;
    }

    public void run() {
        try {
            // Set the input channel
            BufferedReader sInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //Pilla el outputstream del socket
            OutputStream out = socket.getOutputStream();
            // Set the output channel
            PrintWriter sOutput = new PrintWriter(socket.getOutputStream(), true);
            // Receive the message from the client
            String received = sInput.readLine();

            if(received != null) {
                String[] parts = received.split(" ");
                boolean line = false;

                while (received != null && !received.equals("")) {
                    if (received.startsWith("If-Modified-Since:")) {
                        line = true;
                        break;
                    }
                    received = sInput.readLine();
                }

                Date date = new Date();

                if (parts[0].equals("GET") || parts[0].equals("HEAD")) {

                    if ((parts[1].equals("/favicon.ico")) || (parts[1].equals("/fic.png")) || (parts[1].equals("/httptester.jar"))
                        || (parts[1].equals("/index.html")) || (parts[1].equals("/LICENSE.txt")) || (parts[1].equals("/udc.gif"))) {

                        File file = new File("p1-files/" + parts[1]);
                        boolean modified = true;
                        if (line) {
                            modified = !received.equals("If-Modified-Since: " + new Date(file.lastModified()));
                        }

                        if (modified) {
                            //200 OK
                            sOutput.println("HTTP/1.0 200 OK");
                        } else {
                            //304 Not Modified
                            sOutput.println("HTTP/1.0 304 Not Modified");
                        }

                        sOutput.println("Date: " + date);
                        sOutput.println("Server: WebServer");
                        sOutput.println("Content-Length: " + file.length());
                        sOutput.println("Content-Type: " + Files.probeContentType(Path.of(file.getAbsolutePath())));
                        sOutput.println("Last-Modified: " + new Date(file.lastModified())); //Last modified da un long, date lo transforma a una fecha.
                        sOutput.println("");

                        if (parts[0].equals("GET")) {
                            FileInputStream fileIn = new FileInputStream(file);
                            byte[] data = new byte[fileIn.available()];
                            fileIn.read(data);
                            fileIn.close();
                            out.write(data);
                            out.flush();
                        }
                    } else {
                        //404 Not Found
                        File file = new File("p1-files/error404.html");

                        sOutput.println("HTTP/1.0 404 Not Found");
                        sOutput.println("Date: " + date);
                        sOutput.println("Server: WebServer");
                        sOutput.println("Content-Length: " + file.length());
                        sOutput.println("Content-Type: " + Files.probeContentType(Path.of(file.getAbsolutePath())));
                        sOutput.println("Last-Modified: " + new Date(file.lastModified())); //Last modified da un long, date lo transforma a una fecha.
                        sOutput.println("");

                        if (parts[0].equals("GET")) {
                            FileInputStream fileIn = new FileInputStream(file);
                            byte[] data = new byte[fileIn.available()];
                            fileIn.read(data);
                            fileIn.close();
                            out.write(data);
                            out.flush();
                        }
                    }
                } else {
                    //400 Bad
                    File file = new File("p1-files/error400.html");

                    sOutput.println("HTTP/1.0 400 Bad Request");
                    sOutput.println("Date: " + date);
                    sOutput.println("Server: WebServer");
                    sOutput.println("Content-Length: " + file.length());
                    sOutput.println("Content-Type: " + Files.probeContentType(Path.of(file.getAbsolutePath())));
                    sOutput.println("Last-Modified: " + new Date(file.lastModified())); //Last modified da un long, date lo transforma a una fecha.
                    sOutput.println("");

                    FileInputStream fileIn = new FileInputStream(file);
                    byte[] data = new byte[fileIn.available()];
                    fileIn.read(data);
                    fileIn.close();
                    out.write(data);
                    out.flush();
                }
            }
          // This code processes HTTP requests and generates 
          // HTTP responses
          // Uncomment next catch clause after implementing the logic
          //
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Close the client socket
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
