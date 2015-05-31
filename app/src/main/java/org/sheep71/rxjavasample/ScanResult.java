package org.sheep71.rxjavasample;

public class ScanResult {
    private String name;
    private String path;
    private boolean isSafe;

    public ScanResult(String name, String path, boolean isSafe) {
        this.name = name;
        this.path = path;
        this.isSafe = isSafe;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isSafe() {
        return isSafe;
    }
}
