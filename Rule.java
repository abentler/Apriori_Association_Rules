/*  Anna Bentler
    06/04/2024
    CSC 466
    Professor Stanchev

    Rule tracks the relationship between two ItemSets for determining which combinations of items
    correlate in transactions.
 */

import java.util.Objects;

public class Rule {
    private ItemSet left, right; // left -> right

    // initialize rule with values
    public Rule (ItemSet left, ItemSet right) {
        this.left = left;
        this.right = right;
    }

    // initialize empty rule
    public Rule (){
        this.left = new ItemSet();
        this.right = new ItemSet();
    }

    public ItemSet getLeft() {
        return left;
    }

    public ItemSet getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return left.equals(rule.getLeft()) && right.equals(rule.getRight());
    }

    public void sort() {
        left.sortItems();
        right.sortItems();
    }

    public String toString() {
        return left.toString() + "->" + right.toString();
    }
}
