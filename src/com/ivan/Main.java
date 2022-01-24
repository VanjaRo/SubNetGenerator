package com.ivan;


import com.ivan.exceptions.SubnetException;

public class Main {

    public static void main(String[] args) {
        Subnet sb = new Subnet();
        try {
            sb.writeSubNetData();
        } catch (SubnetException exception) {
            System.out.println(exception);
        }

    }


}
