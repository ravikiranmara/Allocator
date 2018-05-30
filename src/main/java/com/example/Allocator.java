package com.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by wiz on 5/28/18.
 */
public class Allocator {
    public static void main(String[] args) {
        try {
            System.out.println("Enter file path : ");

            String filePath = new String("//home//wiz//junk//test.json");
            FileParser fileParser = new FileParser(filePath);

            fileParser.readTopology();

        } catch (IOException ex) {
            System.out.println("IO exception while reading filename");
        }
    }
}
