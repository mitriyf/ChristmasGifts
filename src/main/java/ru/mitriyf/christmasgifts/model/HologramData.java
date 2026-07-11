package ru.mitriyf.christmasgifts.model;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class HologramData {
    private final Map<Integer, String> timeLines;
    private final List<String> lines;

    public HologramData(List<String> lines, Map<Integer, String> timeLines) {
        this.lines = lines;
        this.timeLines = timeLines;
    }
}
