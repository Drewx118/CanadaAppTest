package com.example.chriscorscadden1.canadaapptest;

import java.util.ArrayList;

/**
 * Created by chriscorscadden1 on 25/03/2015.
 */


public class CanadaFacts {

    private String title;
    private ArrayList<Fact> rows;

    public CanadaFacts(){
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Fact> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Fact> rows) {
        this.rows = rows;
    }

    // Removes facts with no title from rows arraylist
    public void RemoveInvalidFacts(){
        int x=0;
        while(x < rows.size()){
            if(rows.get(x).getTitle() != null && !rows.get(x).getTitle().isEmpty())
                x++;
            else
                rows.remove(x);
        }

    }

}
