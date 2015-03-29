package com.example.chriscorscadden1.canadaapptest;

import java.util.ArrayList;

public class CanadaFacts {

    // title store the title for the activity bar
    private String title;
    // rows stores the facts to be loaded into the listview items
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
