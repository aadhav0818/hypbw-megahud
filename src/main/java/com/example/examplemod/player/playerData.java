package com.example.examplemod.player;



public class playerData {

    private String name;
    private int stars;
    private String rank;
    private boolean isNicked;
    private double FKDR;

    public playerData(String name, int stars, String rank, boolean isNicked, double FKDR)
    {
        this.name = name;
        this.stars = stars;
        this.rank = rank;
        this.isNicked = isNicked;
        this.FKDR = FKDR;
    }

    public int getStars() {
        return this.stars;
    }

    public String getName() {
        return this.name;
    }

    public String getStarsAsString() {
        return String.valueOf(stars);
    }

    public String getRank()
    {
        return this.rank;
    }

    public double getFKDR() { return this.FKDR; }

    public String getFDKRAsString() { return String.valueOf(this.getFKDR()); }

    public int getHex()
    {
        if(this.rank.equals("\"UNRANKED\"")) { return 0xAAAAAA; }
        else if(this.rank.equals("\"VIP\"") || this.rank.equals("\"VIP_PLUS\"")) { return 0x55FF55; }
        else if(this.rank.equals("\"MVP\"") || this.rank.equals("\"MVP_PLUS\"")) { return 0x55FFFF; }
        else if(this.rank.equals("\"MVP_PLUS_PLUS\"")) { return 0xFFAA00; }
        return 0xFFFFFF;
    }

    public int getStarHex()
    {
        if(this.stars < 100) { return 0xAAAAAA; }
        else if(this.stars < 200) { return 0xFFFFFF; }
        else if(this.stars < 300) { return 0xFFAA00; }
        else if(this.stars < 400) { return 0x55FFFF; }
        else if(this.stars < 500) { return 0x00AA00; }
        else if(this.stars < 600) { return 0x00AAAA; }
        else if(this.stars < 700) { return 0xAA0000; }
        else if(this.stars < 800) { return 0xFF55FF; }
        else if(this.stars < 900) { return 0x5555FF; }
        else if(this.stars < 1000) { return 0xAA00AA; }
        return 0xFF5555;
    }

    public int getFKDRHex()
    {
        if(this.FKDR < 1) { return 0xfc7260; }
        if(this.FKDR < 3) { return 0xf0e962; }
        if(this.FKDR < 8) { return 0x77e84a; }
        return 0x33de9a;
    }

}
