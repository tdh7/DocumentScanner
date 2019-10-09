package com.scanlibrary;

public class Image {
    private String path;
    private String name;
    private String dateTaken;
    private String album;
    private long size;
    private boolean check;

    public Image(String path) {
        this.path = path;
        this.name = name;
        this.dateTaken = dateTaken;
        this.album = album;
        this.size = size;
        this.check = false;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
