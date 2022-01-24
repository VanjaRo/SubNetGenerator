package com.ivan;

import com.ivan.exceptions.SubnetException;

import java.io.IOException;
import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.*;


public class Subnet {
    static final int BIT_NUMBER = 32;

    private final FileReadWriter fileReadWriter;

    public Subnet() {
        fileReadWriter = new FileReadWriter();
    }

    public static byte[] maskToByteIpV4(int mask) {
        byte[] maskByte = new byte[4];
        for (int i = 0; i < 4; i++) {
            StringBuilder binary = new StringBuilder(8);
            for (int j = 0; j < 8; j++) {
                if (mask > 0) {
                    binary.append("1");
                } else {
                    binary.append("0");
                }
                mask -= 1;
            }
            maskByte[i] = Integer.valueOf(Integer.parseInt(binary.toString(), 2)).byteValue();
        }
        return maskByte;
    }

    public static byte[] getNetPrefByteIpV4(byte[] ipByte, byte[] maskByte) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (maskByte[i] & ipByte[i]);
        }
        return result;
    }

    public static String subnetAddressString(byte[] ipPrefByte, int mask) {
        StringBuilder subNetAddress = new StringBuilder();
        subNetAddress.append(Integer.valueOf(ipPrefByte[0]));
        for (int i = 1; i < ipPrefByte.length; i++) {
            subNetAddress.append(".").append(Integer.valueOf(ipPrefByte[i]));
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
        int maxBits = Subnet.BIT_NUMBER - 2;
        int minBits = 8;

        int biggestInd = 0;
        Set<Integer> masksSet = new HashSet<>();
        List<String> generatedSubnetAddress = new ArrayList<>();
        List<String> biggestIpAddress = new ArrayList<>();

        biggestIpAddress.add(address);
        for (int i = 0; i < N; i++) {
            int randomInt = (int) Math.floor(Math.random() * (maxBits - minBits + 1) + minBits);
            if (masksSet.contains(randomInt)) {
                continue;
            }
            masksSet.add(randomInt);
            byte[] bytesMask = Subnet.maskToByteIpV4(randomInt);
            byte[] netPref = Subnet.getNetPrefByteIpV4(bytesIp, bytesMask);

            generatedSubnetAddress.add(Subnet.subnetAddressString(netPref, randomInt));

            if (randomInt > biggestMask) {
                biggestMask = randomInt;
                biggestInd = generatedSubnetAddress.size() - 1;
            }

        }
        biggestIpAddress.add(generatedSubnetAddress.get(biggestInd));
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
