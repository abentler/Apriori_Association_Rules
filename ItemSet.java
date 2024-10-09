/*  Anna Bentler
    06/04/2024
    CSC 466
    Professor Stanchev

    ItemSet contains a list of items that were purchased together.
 */

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ItemSet {
    ArrayList<Integer> items = new ArrayList<>();

    // most basic, empty itemset
    public ItemSet () {
        items = new ArrayList<>();
    }

    // initialize item list with initial value
    public ItemSet (int initialVal) {
        items = new ArrayList<>();
        items.add(initialVal);
    }

    // initialize item list
    public ItemSet(ArrayList<Integer> items) {
        this.items = items;
    }

    // add an element to the item list
    public void add(int n) {
        items.add(n);
    }

    // add element at an index to the item list
    public void addAt(int index, int n) {
        if(items.size() > index) items.remove(index);
        items.add(index, n);
    }

    // check if item list contains an item
    public boolean contains(int n) {
        return items.contains(n);
    }

    // check if item list contains a subset of items
    public boolean contains(ArrayList<Integer> subset) {
        for(int i: subset) {
            if(!items.contains(i)) return false;
        }
        return true;
    }

    public ArrayList<Integer> getItems () { return items; }

    public boolean equals (Object o) {
        if (!o.getClass().equals(ItemSet.class)) return false;
        ItemSet itemSet = (ItemSet)o;
        for(Integer i: itemSet.getItems()) {
            if(!items.contains(i)) return false;
        }
        return true;
    }

    public int getItemAt (int index) {
        return items.get(index);
    }

    public void sortItems () {
        items.sort(Integer::compareTo);
    }

    @Override
    public String toString() {
        return items.toString();
    }

}
