package com.cdac.bajaj.bajaj;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <Path to JSON file>");
            return;
        }

        String prnNumber = args[0].toLowerCase().replaceAll("\\s+", "");
        String jsonFilePath = args[1];

        try {
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));
            String destinationValue = findDestinationValue(rootNode);

            if (destinationValue == null) {
                System.out.println("Destination key not found in JSON.");
                return;
            }

            
            String randomString = generateRandomString(8);

            
            String concatenatedString = prnNumber + destinationValue + randomString;

            
            String md5Hash = generateMD5Hash(concatenatedString);

            
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText();
                }
                String result = findDestinationValue(field.getValue());
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                String result = findDestinationValue(arrayItem);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

