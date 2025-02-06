package me.daniel1385.itemsign.objects;

import java.util.List;

public class SignatureData {
    private List<String> lines;
    private String date;
    private String player;
    private String uuid;

    public SignatureData(List<String> lines, String date, String player, String uuid) {
        this.lines = lines;
        this.date = date;
        this.player = player;
        this.uuid = uuid;
    }

    public String getDate() {
        return date;
    }

    public String getPlayerName() {
        return player;
    }

    public List<String> getLines() {
        return lines;
    }

    public String getPlayerUUID() {
        return uuid;
    }
}
