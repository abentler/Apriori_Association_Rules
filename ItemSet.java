import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ItemSet {
    ArrayList<Integer> items = new ArrayList<>();

    public ItemSet () {
        items = new ArrayList<>();
    }

    public ItemSet (int initialVal) {
        items = new ArrayList<>();
        items.add(initialVal);
    }

    public ItemSet(ArrayList<Integer> items) {
        this.items = items;
    }

    public void add(int n) {
        items.add(n);
    }

    public void addAt(int index, int n) {
        if(items.size() > index) items.remove(index);
        items.add(index, n);
    }

    public boolean contains(int n) {
        return items.contains(n);
    }

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
