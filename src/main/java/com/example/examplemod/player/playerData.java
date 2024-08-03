package com.example.examplemod.player;



public class playerData {

    private String name;
    private int stars;
    private String rank;
    private boolean isNicked;

    public playerData(String name, int stars, String rank, boolean isNicked)
    {
        this.name = name;
        this.stars = stars;
        this.rank = rank;
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

    public int getHex()
    {
        if(this.rank.equals("\"UNRANKED\"")) {
            return 0xAAAAAA;
        }
        else if(this.rank.equals("\"VIP\"") || this.rank.equals("\"VIP_PLUS\"")) {
            return 0x55FF55;
        }
        else if(this.rank.equals("\"MVP\"") || this.rank.equals("\"MVP_PLUS\"")) {
            return 0x55FFFF;
        }
        else if(this.rank.equals("\"MVP_PLUS_PLUS\"")) {
            return 0xFFAA00;
        }
        return 0xFFFFFF;
    }

}
