package com.example;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public TopologyConfig readTopologyConfig() throws IOException {
        // json stuff
        JsonParser jsonParser = new JsonParser();
        JsonObject rootObj = jsonParser.parse(this.fileReader).getAsJsonObject();

        // get topology name
        TopologyConfig topologyConfig = new TopologyConfig();
        String topologyName = rootObj.get("name").getAsString();
        topologyConfig.setTopologyName(topologyName);

        // get unit (cpu per unit)
        int cpuPerUnit = rootObj.get("cpuPerUnit").getAsInt();
        topologyConfig.setCpuPerUnits(cpuPerUnit);

        // get numUnits (number of units/workers already allocated)
        int numUnits = rootObj.get("numUnits").getAsInt();
        topologyConfig.setNumUnits(numUnits);

        // get an array from the JSON object
        JsonArray comp = (JsonArray) rootObj.get("components").getAsJsonArray();
        Iterator i = comp.iterator();

        // take each value from the json array separately
        while (i.hasNext()) {
            ComponentConfig componentConfig = new ComponentConfig();
            componentConfig.setCpuPerUnit(cpuPerUnit);
            JsonObject innerObj = (JsonObject) i.next();

            // Component name
            String cname = innerObj.get("cname").getAsString();
            componentConfig.setName(cname);

            // allocated
            int numAllocated = innerObj.get("allocated").getAsInt();
            componentConfig.setResourceAllocated(numAllocated);

            // input
            int input = innerObj.get("input").getAsInt();
            componentConfig.setInput(input);

            // output
            int output = innerObj.get("output").getAsInt();
            componentConfig.setOutput(output);

            // cpu used
            int cpuUsed = innerObj.get("cpuUsed").getAsInt();
            componentConfig.setCpuUsed(cpuUsed);

            // inputPerUnit used
            int maxInputPerUnit = innerObj.get("maxInputPerUnit").getAsInt();
            componentConfig.setMaxInputPerUnit(maxInputPerUnit);

            // parent name
            JsonArray parents = innerObj.get("parents").getAsJsonArray();
            Iterator p = parents.iterator();
            while (p.hasNext()) {
                JsonObject parentObj = (JsonObject) p.next();

                String parentName = parentObj.get("parent").getAsString();
                if(false == parentName.equalsIgnoreCase("null"))
                    componentConfig.addParent(parentName);
            }

            // output distribution
            if(innerObj.get("children") != null) {
                JsonArray child = (JsonArray) innerObj.get("children").getAsJsonArray();

                // child handle
                Iterator j = child.iterator();
                while (j.hasNext()) {
                    JsonObject childObj = (JsonObject) j.next();

                    // Component name
                    String childName = childObj.get("child").getAsString();
                    double childRatio = childObj.get("childRatio").getAsDouble();
                    componentConfig.addChild(childName, new Double(childRatio));
                }
            }

            // System.out.println("Added component:" + componentConfig.getName());
            topologyConfig.addComponent(componentConfig);
        }

        return topologyConfig;
    }

    public void dumpTopology() throws IOException {
        // json stuff
        JsonParser jsonParser = new JsonParser();
        JsonObject rootObj = jsonParser.parse(this.fileReader).getAsJsonObject();

        // get topology name
        String topologyName = rootObj.get("name").getAsString();
        System.out.println("The first name is: " + topologyName);

        // get unit (cpu per unit)
        int cpuPerUnit = rootObj.get("cpuPerUnit").getAsInt();
        System.out.println("cpu per Units : " + cpuPerUnit);

        // get numUnits (number of units/workers already allocated)
        int numUnits = rootObj.get("numUnits").getAsInt();
        System.out.println("num Units : " + numUnits);

        // get an array from the JSON object
        JsonArray comp = (JsonArray) rootObj.get("components").getAsJsonArray();

        // take the elements of the json array
        for(int i=0; i<comp.size(); i++){
            System.out.println("The " + i + " element of the array: " + comp.get(i));
        }
        Iterator i = comp.iterator();

        // take each value from the json array separately
        while (i.hasNext()) {
            JsonObject innerObj = (JsonObject) i.next();

            // Component name
            String cname = innerObj.get("cname").getAsString();
            System.out.println("name: "+ cname);

            // allocated
            int numAllocated = innerObj.get("allocated").getAsInt();
            System.out.println("allocated: "+ numAllocated);

            // input
            int input = innerObj.get("input").getAsInt();
            System.out.println("input: "+ input);

            // output
            int output = innerObj.get("output").getAsInt();
            System.out.println("output: "+ output);

            // cpu used
            int cpuUsed = innerObj.get("cpuUsed").getAsInt();
            System.out.println("cpuUsed: "+ cpuUsed);

            // parent name
            String parent = innerObj.get("parent").getAsString();
            System.out.println("parent: "+ parent);

            // output distribution
            if(innerObj.get("children") == null) {
                System.out.println("No children");
                continue;
            }
            JsonArray child = (JsonArray) innerObj.get("children").getAsJsonArray();

            // child handle
            Iterator j = child.iterator();
            while (j.hasNext()) {
                JsonObject childObj = (JsonObject) j.next();

                // Component name
                String childName = childObj.get("child").getAsString();
                System.out.println("child: "+ childName);

                double childRatio = childObj.get("childRatio").getAsDouble();
                System.out.println("childRatio: "+ childRatio);
            }
        }
    }
}

