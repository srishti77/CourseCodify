package com.srishti.srish.coursecodify_v1;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 05/12/2017.
 */

public class CreateDirectories {
    //static int count = 0;
    public void createNoMedia(String directory){

        try{

            Log.i("Nomedia","created");
            File mediaStorageDir = new File(directory );
            File nomediaFile = new File(mediaStorageDir.getPath()+"/.nomedia");
            Log.i("Path",nomediaFile.getPath());
            if(!nomediaFile.exists())
            nomediaFile.createNewFile();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public String createCourseCodifyFile(){
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CourseCodify");

        if(!mediaStorageDir.exists()){

            mediaStorageDir.mkdirs();
            if(mediaStorageDir.mkdirs()){
                Log.i("mainFile is created","CourseCodify");
            }
        }
        Log.i("MainDirectory","CourseCodify");
        return mediaStorageDir.getName();
    }

    public String createEventFolder(String FolderName){
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CourseCodify/"+ FolderName);

        if(!mediaStorageDir.exists()){

            mediaStorageDir.mkdirs();

        }
        Log.i("Event","-----");
        return mediaStorageDir.getName();
    }

    public String createSubFolder(String event, String typeOfMaterial){

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CourseCodify/"+ event + "/"+ typeOfMaterial);


        if(!mediaStorageDir.exists()){

            mediaStorageDir.mkdirs();
            if(!mediaStorageDir.mkdirs()){
                Log.i("subFolder is created","CourseCodify");
            }
        }
        Log.i("createSubFolder","Method");
        return mediaStorageDir.getName();
    }


    public File saveMaterial(String event, String typeOfMaterial, String fileName, String fileContent){

        File mediaStorageDir = null;
        try{
            Log.i("Save Material", "Called");
            Log.i("Save MaterialEvent"+event, typeOfMaterial +" "+ fileName);

             mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CourseCodify/"+ event + "/"+ typeOfMaterial +"/" + fileName);

            if(!mediaStorageDir.exists()){

                mediaStorageDir.createNewFile();

            }
            if(fileContent != null)
            writeContentIntoFile(fileContent, mediaStorageDir);

        }

        catch (Exception e){
            e.printStackTrace();
        }
        return mediaStorageDir;
    }

    public  void writeContentIntoFile( String notes, File file) {

        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);

                Log.i("NotesCC","");
                out.write(notes);
                out.flush();
                out.close();

            }
            Log.i("Written", "Into file");
        } catch (Exception e) {
            Log.i("Could Not", "Write");
            e.printStackTrace();

        }

    }

    public List readAllDirectoryName(String event, String materialType){
        List<String> listOfAllDirectories = new ArrayList<String>();
        File   mediaStorageDir = null;
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {


                if(event == null){
                      mediaStorageDir = new File(
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CourseCodify");

                }

                else if (event != null && materialType != null ) {
                    mediaStorageDir = new File(
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CourseCodify/"+event+"/"+materialType);


                }
                listOfAllDirectories.clear();
                listOfAllDirectories = readAllFiles(mediaStorageDir, listOfAllDirectories);

            }
        }
        catch (Exception e){
            e.printStackTrace();

        }

        return listOfAllDirectories;
    }

    public List readAllFiles(File mediaStorageDir, List listOfAllDirectories){
        listOfAllDirectories = new ArrayList();
        if(mediaStorageDir.exists()){

            for(File fileName : mediaStorageDir.listFiles()){


                listOfAllDirectories.add(fileName.getName());

            }

            Log.i( "Count of list: "+listOfAllDirectories.size(), "");
        }

        return listOfAllDirectories;
    }

    public StringBuffer readContentOfNotesFile(String file){
       // List<String> listOfContents = new ArrayList<String>();
       // List<String> listOfFiles = new ArrayList<String>();
        StringBuffer stringBuffer = null;
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {


                    Log.i("Full file Name",file);
                   // setFileName(listOfFiles.get(i));
                    File fileName = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                            .getAbsolutePath()+"/CourseCodify/"+ file);

                FileInputStream fileInputStream = new FileInputStream(fileName);
                     stringBuffer = new StringBuffer();
                    int ch;
                    while((ch = fileInputStream.read()) != -1){
                        stringBuffer.append((char)ch);
                    }
                  //  listOfContents.add(stringBuffer.toString());

                }
            } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return  stringBuffer;
    }


    File getCurrentFile(String fileDirection){

        File directory = null;
    try{
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), fileDirection);
    }

    catch(Exception e){
        e.printStackTrace();
    }
        return directory;
    }
}
