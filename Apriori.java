/*  Anna Bentler
    06/04/2024
    CSC 466
    Professor Stanchev

    This program parses the file provided by the program arguments, such as 'data.txt,' and
    computes its frequent itemsets. It then compares them against the minimum confidence value
    to determine association rules. These rules can be used to assess customer behavior.

    Sample shopping data courtesy of Dr. Stanchev, CSC 466, Cal Poly SLO.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Apriori {

    public static double MIN_SUPPORT = 0.01;
    public static double MIN_CONFIDENCE = 0.99;
    public static ArrayList<ItemSet> transactions = new ArrayList<>();
    public static ArrayList<Integer> items = new ArrayList<>();
    public static HashMap<Integer, Integer> itemCounts = new HashMap<>();
    public static HashMap<Integer, ArrayList<ItemSet>> frequentItemSet = new HashMap<>();
    public static ArrayList<Rule> rules = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        // verify arguments
        if(args.length < 1) {
            System.err.println("Requires name of data file.");
            return;
        }
        File file = new File(args[0]);
        try {
            Scanner input = new Scanner(file);
            parseLists(input); // parse the file into transaction list
            getFrequentItemSets(); // run Apriori algorithm to get frequent itemsets
            // sort each itemset for readibility
            for(Rule rule: rules) {
                rule.sort();
            }
            // sort the rules from shortest to longest for readability
            rules.sort((Rule r1, Rule r2) -> {
                for(int i = 0; i < r1.getLeft().getItems().size() &&
                        i < r2.getLeft().getItems().size(); i++) {
                    if(r1.getLeft().getItemAt(i) > r2.getLeft().getItemAt(i)) return 1;
                    if(r1.getLeft().getItemAt(i) < r2.getLeft().getItemAt(i)) return -1;
                }
                return 0;
            });
            System.out.println(rules); // print

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return;
    }

    // parse the data file into transactions list
    private static void parseLists(Scanner input) {
        while(input.hasNextLine()) {
            String[] vals = input.nextLine().split(", ");
            ItemSet set = new ItemSet();
            for(int i = 1; i < vals.length; i++) {
                int n = Integer.parseInt(vals[i]);
                set.add(n);
                if(!items.contains(n)) items.add(n);
                if(itemCounts.containsKey(n)) {
                    itemCounts.replace(n, itemCounts.get(n) + 1);
                } else {
                    itemCounts.put(n, 1);
                }
            }
            transactions.add(set);
        }
    }

    // run the apriori algorithm to find frequent itemsets
    private static void getFrequentItemSets() {
        ArrayList<ItemSet> freqItems = new ArrayList<>();
        int numItems = items.size();
        int n = 1;

        // generate list for itemsets of size n=1, check min support
        for (Map.Entry<Integer, Integer> entry : itemCounts.entrySet()) {
            if ((double) entry.getValue() / transactions.size() > MIN_SUPPORT)
                freqItems.add(new ItemSet(entry.getKey()));
        }

        // iterate with increasing n until no new item sets are found
        while (!freqItems.isEmpty() && n < items.size()) {
            ItemSet itemset = new ItemSet();
            frequentItemSet.put(n++, freqItems);
            freqItems = new ArrayList<>();
            // initialize option with first n values
            for (int i = 0; i < n; i++) {
                itemset.addAt(i, items.get(i));
            }

            int[] indices = new int[n]; // array maps to which items we are using

            // check all combinations
            if (n <= items.size()) {
                for (int i = 0; (indices[i] = i) < n - 1; i++) ;
                ItemSet itemSet = getSubset(items, indices);
                if(isFrequent(itemSet)) {
                    freqItems.add(itemSet);
                    rules.addAll(split(itemSet));
                }
                while (true) {
                    int i;
                    // find position of item that can be incremented
                    for (i = n - 1; i >= 0 && indices[i] == items.size() - n + i; i--) ;
                    if (i < 0) {
                        break;
                    }
                    indices[i]++;
                    i++;
                    // shift indices up to get new permutation
                    for (; i < n; i++) {
                        indices[i] = indices[i - 1] + 1;
                    }
                    itemSet = getSubset(items, indices);
                    // validate new combination against min support
                    if(isFrequent(itemSet)) {
                        freqItems.add(itemSet);
                        rules.addAll(split(itemSet));
                    }
                }
            }

        }
    }

    // must verify if current itemset AND all subsets meet min support
    public static boolean isFrequent(ItemSet itemSet) {
        int count = 0;
        // counting how often this combination occurs in the transactions
        for(ItemSet trans: transactions) {
            boolean hasAll = true;
            for(int i: itemSet.getItems()) {
                if(!trans.contains(i)) hasAll = false;
            }
            if(hasAll) count++;
        }
        if(((double)count)/transactions.size() < MIN_SUPPORT) return false;
        if(!allSubsetsAreFrequent(itemSet)) return false;
        return true;
    }

    // helper function verify that ALL subsets are also frequent
    private static boolean allSubsetsAreFrequent(ItemSet itemSet) {
        int n = 1;
        int matches = 0;
        int[] indices = new int[n]; // array maps to which items we are using

        if (n <= itemSet.getItems().size()) {
            for (int i = 0; (indices[i] = i) < n - 1; i++) ;
            ItemSet subset = getSubset(itemSet.getItems(), indices);
            ItemSet finalSubset = subset;
            if(!frequentItemSet.values().stream().anyMatch((ArrayList<ItemSet> s) -> {
                return s.contains(finalSubset);
            })) return false;
            while (true) {
                int i;
                // find position of item that can be incremented
                for (i = n - 1; i >= 0 && indices[i] == itemSet.getItems().size() - n + i; i--) ;
                if (i < 0) {
                    break;
                }
                indices[i]++;
                i++;
                for (; i < n; i++) {
                    indices[i] = indices[i - 1] + 1;
                }
                subset = getSubset(itemSet.getItems(), indices);
                ItemSet finalSubset2 = subset;
                if(!frequentItemSet.values().stream().anyMatch((ArrayList<ItemSet> s) -> {
                    return s.contains(finalSubset2);
                })) return false;
            }
        }
        return true;
    }

    // return the corresponding itemset values for a subset of indices
    private static ItemSet getSubset(ArrayList<Integer> input, int[] subset) {
        ItemSet itemSet = new ItemSet();
        for (int i = 0; i < subset.length; i++)
            itemSet.addAt(i, input.get(subset[i]));
        return itemSet;
    }

    // split a frequent itemset into a list of possible Rule combinations
    public static ArrayList<Rule> split(ItemSet itemSet) {
        ArrayList<Rule> result = new ArrayList<Rule>();
        for (int k = 1; k < Math.pow(2, itemSet.getItems().size()) - 1; k++) {
            // distribute elements to subsets representing left and right of rule
            Rule rule = new Rule();
            int k2 = k;
            for (int i = 0; i < itemSet.getItems().size(); i++) {
                if (k2 % 2 == 0) {
                    rule.getLeft().add(itemSet.getItemAt(i));
                } else {
                    rule.getRight().add(itemSet.getItemAt(i));
                }
                k2 /= 2;
            }
            // check rule agains min confidence
            if(meetsMinConfidence(rule)) result.add(rule);
        }
        return result;
    }

    // calculate the rule confidence and verify against min confidence
    // conf(X -> Y) is freq(x,y)/freq(x)
    public static boolean meetsMinConfidence (Rule rule) {
        int leftFreq = 0;
        int bothFreq = 0;
        for(ItemSet trans: transactions) {
            if(trans.contains(rule.getLeft().getItems())) {
                leftFreq++;
                if(trans.contains(rule.getRight().getItems())) {
                    bothFreq++;
                }
            }
        }
        return ((double)bothFreq/leftFreq >= MIN_CONFIDENCE);
    }

}
