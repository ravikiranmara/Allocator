package com.example;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by wiz on 5/28/18.
 */
public class FileParser {
    private File file;
    private FileReader fileReader;
    JsonReader jsonReader;

    public FileParser(String filePath) throws IOException {
        this.file = new File(filePath);

        // check file exists
        if (false == this.file.exists() || this.file.isDirectory()) {
            throw new FileNotFoundException("The file does not exist");
        }

        // open file
        this.fileReader = new FileReader(this.file);

        return;
    }

    public void readComponent() throws IOException {
        JsonParser jsonParser = new JsonParser();

        // jsonReader.beginObject();
        JsonObject rootObj = jsonParser.parse(this.fileReader).getAsJsonObject();
        JsonArray componentArray = rootObj.getAsJsonArray("components");

        String firstName = (String) rootObj.get("firstname").getAsString();
        System.out.println("The first name is: " + firstName);

        // get an array from the JSON object
        JsonArray comp = (JsonArray) rootObj.get("components");

        // take the elements of the json array
        for(int i=0; i<comp.size(); i++){
            System.out.println("The " + i + " element of the array: "+comp.get(i));
        }
        Iterator i = comp.iterator();

        // take each value from the json array separately
        while (i.hasNext()) {
            JsonObject innerObj = (JsonObject) i.next();
            System.out.println("language "+ innerObj.get("name").getAsString() +
                    " with level");
        }
    }
}



