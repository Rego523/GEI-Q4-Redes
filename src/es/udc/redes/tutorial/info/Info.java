package es.udc.redes.tutorial.info;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Info {
    public static void main(String[] args) throws IOException {
        File carpeta = new File(args[0]);

        System.out.println("File length is: \t" + carpeta.length());

        long time1 = carpeta.lastModified();
        DateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        System.out.println("File last modified date is: \t" + sdf.format(time1));

        System.out.println("File name is: \t\t" + carpeta.getName());

        String fileName = carpeta.toString();
        int index = fileName.lastIndexOf('.');
        if(index > 0) {
            String extension = fileName.substring(index + 1);
            System.out.println("File extension is: \t" + extension);
        }

        System.out.println("File type is: \t\t" + Files.probeContentType(Path.of(carpeta.getAbsolutePath())));

        System.out.println("File absolute path is: \t" + carpeta.getAbsolutePath());
    }
}