package ru.itis.classifier;

import ru.itis.classifier.utils.Utils;

/**
 * 08.06.2022
 *
 * @author Azat Yamanaev
 */
public class Test {

    public static void main(String[] args) {
        String s1 = "dev-latest: digest: sha256:617a202df9b1f8d3f2f81c0cf0fe99801664266a2bd0b3bcd5075b7471a93e75 size: 1998";
        String s2 = "dev-latest: digest: sha256:37590b2cfde408473b67fec4a62eba5db05be12d83ef082dc10a7f4fa5d15245 size: 1998";
        System.out.println(s1.length() + " " + s2.length());
        System.out.println(Utils.calculate(s1, s2));
    }
}
