package com.mod.hypixelmegahud.core;

import static java.lang.String.join;
import static java.util.Collections.nCopies;

public class numberSystems {

    public static String base10toRoman(int x) {
        String XCOPYI = "";
        for(int i = 0; i < x; i++) {XCOPYI+="I";}
        return XCOPYI
                .replace("IIIII", "V")
                .replace("IIII", "IV")
                .replace("VV", "X")
                .replace("VIV", "IX")
                .replace("XXXXX", "L")
                .replace("XXXX", "XL")
                .replace("LL", "C")
                .replace("LXL", "XC")
                .replace("CCCCC", "D")
                .replace("CCCC", "CD")
                .replace("DD", "M")
                .replace("DCD", "CM");
    }
}
