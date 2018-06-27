package Geoparser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GazetteerObject {
    public String uid;
    public List<Map<String,String>> data = new ArrayList<>();
    public List<Map<Integer,String>> dataInt = new ArrayList<>();
    public List<String> columnHeaders = new ArrayList<>();
    public File file;
    public String filepath;
    public String gazetteerName;
    public String lineDelimiter;
    public String columnDelimiter;
    public boolean hasColumnHeaders = true;
    public String locationName;
    public String latColumn;
    public String longColumn;
    public int locationNameInt;
    public int latColumnInt;
    public int longColumnInt;

    public GazetteerObject(File inFile, String inLocationName, String inLatColumn, String inLongColumn, List<Map<String, String>> inData, List<String> inColumnHeaders){
        file = inFile;
        locationName = inLocationName;
        latColumn = inLatColumn;
        longColumn = inLongColumn;
        data = inData;
        columnHeaders = inColumnHeaders;
    }

    public GazetteerObject(File inFile, int inLocationName, int inLatColumn, int inLongColumn, List<Map<Integer, String>> inData, List<String> inColumnHeaders){
        file = inFile;
        locationNameInt = inLocationName;
        latColumnInt = inLatColumn;
        longColumnInt = inLongColumn;
        dataInt = inData;
        columnHeaders = inColumnHeaders;
    }

    public GazetteerObject(){
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public List<String> getColumnHeaders() {
        return columnHeaders;
    }

    public File getFile() {
        return file;
    }

    public String getGazetteerName() {
        return gazetteerName;
    }

    public void setGazetteerName(String gazetteerName) {
        this.gazetteerName = gazetteerName;
    }

    public boolean isHasColumnHeaders() {
        return hasColumnHeaders;
    }

    public void setHasColumnHeaders(boolean hasColumnHeaders) {
        this.hasColumnHeaders = hasColumnHeaders;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLatColumn() {
        return latColumn;
    }

    public String getLongColumn() {
        return longColumn;
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    public void setColumnHeaders(List<String> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLatColumn(String latColumn) {
        this.latColumn = latColumn;
    }

    public void setLongColumn(String longColumn) {
        this.longColumn = longColumn;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getLineDelimiter() {
        return lineDelimiter;
    }

    public void setLineDelimiter(String lineDelimiter) {
        this.lineDelimiter = lineDelimiter;
    }

    public String getColumnDelimiter() {
        return columnDelimiter;
    }

    public void setColumnDelimiter(String columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }

    public int getLocationNameInt() {
        return locationNameInt;
    }

    public void setLocationNameInt(int locationNameInt) {
        this.locationNameInt = locationNameInt;
    }

    public int getLatColumnInt() {
        return latColumnInt;
    }

    public void setLatColumnInt(int latColumnInt) {
        this.latColumnInt = latColumnInt;
    }

    public int getLongColumnInt() {
        return longColumnInt;
    }

    public void setLongColumnInt(int longColumnInt) {
        this.longColumnInt = longColumnInt;
    }

    public List<Map<Integer, String>> getDataInt() {
        return dataInt;
    }

    public void setDataInt(List<Map<Integer, String>> dataInt) {
        this.dataInt = dataInt;
    }
}

