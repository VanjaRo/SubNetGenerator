package com.company;

import java.io.IOException;
import java.net.InetAddress;

import java.util.*;


public class SubNet {
    static final int BIT_NUMBER = 32;

    private final FileRW fileRW;

    public SubNet () {
        fileRW = new FileRW();
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

    public static String subNetAddressString(byte[] ipPrefByte, int mask) {
        StringBuilder subNetAddress = new StringBuilder();
        subNetAddress.append(Integer.valueOf(ipPrefByte[0]));
        for (int i = 1; i < ipPrefByte.length; i++) {
            subNetAddress.append(".").append(Integer.valueOf(ipPrefByte[i]));
        }
        subNetAddress.append("/" + mask);
        return subNetAddress.toString();
    }


    public void writeSubNetData() throws IOException {
        List<String> input = fileRW.readSmallTextFile("/data/in.txt");

//        get N
        var N = Integer.parseInt(input.get(0));

//        get ipv4 address
        var address = input.get(1);

//        generate N random masks for the address
        byte[] bytesIp = InetAddress.getByName(address).getAddress();
        int biggestMask = -1;
        int maxBits = SubNet.BIT_NUMBER - 2;
        int minBits = 8;

        int biggestInd = 0;
        Set<Integer> masksSet = new HashSet<>();
        List<String> generatedSubNetAddress = new ArrayList<>();
        List<String> biggestIpAddress = new ArrayList<>();

        biggestIpAddress.add(address);
        for (int i = 0; i < N; i++) {
            int randomInt = (int) Math.floor(Math.random() * (maxBits - minBits + 1) + minBits);
            if (masksSet.contains(randomInt)) {
                continue;
            }
            masksSet.add(randomInt);
            byte[] bytesMask = SubNet.maskToByteIpV4(randomInt);
            byte[] netPref = SubNet.getNetPrefByteIpV4(bytesIp, bytesMask);

            generatedSubNetAddress.add(SubNet.subNetAddressString(netPref, randomInt));

            if (randomInt > biggestMask) {
                biggestMask = randomInt;
                biggestInd = generatedSubNetAddress.size()-1;
            }

        }
        biggestIpAddress.add(generatedSubNetAddress.get(biggestInd));

        fileRW.writeSmallTextFile(generatedSubNetAddress, "/data/autogen.txt");
        fileRW.writeSmallTextFile(biggestIpAddress, "/data/out.txt");
    }


}
