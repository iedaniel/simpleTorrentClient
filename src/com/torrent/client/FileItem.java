package com.torrent.client;

public class FileItem {

    private String name;
    private int progress;

    public FileItem(String name, int progress) {
        this.name = name;
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        if (progress == 0) {
            return name;
        }
        if (progress == 100) {
            return name + " ::::: Загружено: " + progress + "%";
        }
        return name + " ::::: Загрузка... " + progress + "%";
    }
}
