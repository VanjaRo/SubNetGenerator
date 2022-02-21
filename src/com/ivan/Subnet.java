package com.ivan;

import com.ivan.exceptions.SubnetException;

import java.io.IOException;
import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.*;


public class Subnet {
    static final int BIT_NUMBER = 32;

    private final FileReadWriter fileReadWriter;
    private final RandomIntGenerator randomGenerator;

    public Subnet() {
        fileReadWriter = new FileReadWriter();
        randomGenerator = new RandomIntGenerator();
    }

    private static byte[] maskToByteIpV4(int mask) {
        byte[] maskByte = new byte[4];
        for (int i = 0; i < 4; i++) {
            byte binary = 0;
            for (int j = 0; j < 8; j++) {
                binary = (byte) (binary << 1);
                if (mask > 0) {
                    binary++;
                }
                mask -= 1;
            }
            maskByte[i] = binary;
        }
        return maskByte;
    }

    private static byte[] getNetPrefByteIpV4(byte[] ipByte, byte[] maskByte) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (maskByte[i] & ipByte[i]);
        }
        return result;
    }

    private byte[] generateNetPrefByteIpV4() {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) randomGenerator.getRandomNumber(0, 255);
        }
        return result;
    }

    private boolean equalNetPref(byte[] firstNetPref, byte[] secondNetPref) {
        for (int i = 0; i < 4; i++) {
            if (firstNetPref[i] != secondNetPref[i]) {
                return false;
            }
        }
        return true;
    }

    private static String subnetAddressString(byte[] ipPrefByte, int mask) {
        StringBuilder subNetAddress = new StringBuilder();
        subNetAddress.append(Byte.toUnsignedInt(ipPrefByte[0]));
        for (int i = 1; i < ipPrefByte.length; i++) {
            subNetAddress.append(".").append(Byte.toUnsignedInt(ipPrefByte[i]));
        }
        subNetAddress.append("/").append(mask);
        return subNetAddress.toString();
    }


    public void writeSubNetData() throws SubnetException {
        List<String> input;
        try {
            input = fileReadWriter.readSmallTextFile("/data/in.txt");
        } catch (IOException exception) {
            throw new SubnetException("Can't access the input ('in.txt') file", exception);
        }

//        get N
        var N = Integer.parseInt(input.get(0));
        if (N < 0) {
            throw new SubnetException("Can't be a negative number of trials!");
        }

//        get ipv4 address
        var address = input.get(1);

//        generate N random masks for the address
        byte[] bytesIp;
        try {
            bytesIp = InetAddress.getByName(address).getAddress();
        } catch (UnknownHostException exception) {
            throw new SubnetException("Error parsing the IPV4 address", exception);
        }

        int biggestMask = -1;
        int biggestInd = -1;

        int minBits = 0;

        List<String> generatedSubnetAddress = new ArrayList<>();
        List<String> biggestIpAddress = new ArrayList<>();

        biggestIpAddress.add(address);
        for (int i = 0; i < N; i++) {
            int generatedIntMask = randomGenerator.getRandomNumber(minBits, BIT_NUMBER);
            byte[] generatedIp = generateNetPrefByteIpV4();

            byte[] generatedBytesMask = maskToByteIpV4(generatedIntMask);
            byte[] generatedNetPref = getNetPrefByteIpV4(generatedIp, generatedBytesMask);

            byte[] givenNetPref = getNetPrefByteIpV4(bytesIp, generatedBytesMask);

            generatedSubnetAddress.add(subnetAddressString(generatedNetPref, generatedIntMask));

            if (equalNetPref(givenNetPref, generatedNetPref) && generatedIntMask > biggestMask) {
                biggestMask = generatedIntMask;
                biggestInd = generatedSubnetAddress.size() - 1;
            }

        }
        if (biggestInd == -1) {
//            if no subnet found –– show the widest subnet
            biggestIpAddress.add("0.0.0.0/0");
        } else {
            biggestIpAddress.add(generatedSubnetAddress.get(biggestInd));
        }

        try {
            fileReadWriter.writeSmallTextFile(generatedSubnetAddress, "/data/autogen.txt");
        } catch (IOException exception) {
            throw new SubnetException("Error writing to the autogen.txt file", exception);
        }

        try {
            fileReadWriter.writeSmallTextFile(biggestIpAddress, "/data/out.txt");
        } catch (IOException exception) {
            throw new SubnetException("Error writing to the out.txt file", exception);
        }


    }


}
