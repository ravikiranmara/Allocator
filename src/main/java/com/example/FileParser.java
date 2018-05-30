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

    public TopologyConfig readTopology() throws IOException {
        // json stuff
        JsonParser jsonParser = new JsonParser();
        JsonObject rootObj = jsonParser.parse(this.fileReader).getAsJsonObject();

        // get topology name
        TopologyConfig topologyConfig = new TopologyConfig();
        String topologyName = rootObj.get("name").getAsString();
        System.out.println("The first name is: " + topologyName);

        // get unit (cpu per unit)
        int unit = rootObj.get("unit").getAsInt();
        System.out.println("Units : " + unit);

        // get numUnits (number of units/workers already allocated)
        int numUnits = rootObj.get("numUnits").getAsInt();
        System.out.println("Units : " + numUnits);

        // get an array from the JSON object
        JsonArray comp = (JsonArray) rootObj.get("components").getAsJsonArray();

        // take the elements of the json array
        for(int i=0; i<comp.size(); i++){
            System.out.println("The " + i + " element of the array: "+comp.get(i));
        }
        Iterator i = comp.iterator();

        // take each value from the json array separately
        while (i.hasNext()) {
            JsonObject innerObj = (JsonObject) i.next();
            System.out.println("language "+ innerObj.get("cname").getAsString() +
                    " with level");
        }

        return topologyConfig;
    }
}



