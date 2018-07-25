package maxandalex.peertopeer;

import android.os.Environment;
import android.util.Log;
import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVParser {

    private static String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    private static String fileName = "PairList.csv";
    private static String filePath = baseDir + File.separator + fileName;

    //FROM http://blog.icodejava.com/tag/how-to-convert-arraylist-to-csv-format/
    public static void ExportListToCSV(ArrayList<Contact> sampleList, boolean writeToConsole, boolean writeToFile) {
        String commaSeparatedValues = "Id,Name,Ip" + "\n";

        if (sampleList != null) {
            for(int i = 0; i < sampleList.size(); i++){
                commaSeparatedValues += sampleList.get(i).getId() + ","
                        + sampleList.get(i).getName() + ","
                        + sampleList.get(i).getIp()+ "\n";
            }
        }
        if (writeToConsole) {
            Log.i("CSVParser", "convertAndPrint: " + commaSeparatedValues);
        }
        if (writeToFile) {
            try {
                FileWriter fstream = new FileWriter(filePath, false);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(commaSeparatedValues);
                out.close();
                System.out.println("*** Also wrote this information to file: " + filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void ImportCSVToList(){
        try{
            String inputLine;
            String[] stringParser;
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            br.readLine(); //TODO KEEP IT SI ON GARDE LA LISTE AVEC LES NOMS DE COLONNES!
            while ((inputLine = br.readLine()) != null) {
                Log.i("ImportCSVToList", "ImportCSVToList: " + inputLine);
                stringParser = inputLine.split(",");
                Contact newContact = new Contact(stringParser[0], stringParser[1], stringParser[2]);
                //TODO AJOUTER LE NOUVEAU LIVRE À LA LIST!
                Contact.contactList.add(newContact);
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
            //Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            e.printStackTrace();
            //Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }

}

