package info;

import java.util.*;

public class SortLists {
    // list: Favorites, doNotShow, or toExplore, itemToMove: name of item to move
    public static List<Info> moveItemUp(List<Info> list, Info i){
        // searches for index of itemToMove in list
        int targetToMove = list.indexOf(i);
        // item was not found in list
        if (targetToMove == -1) {
            return list;
        }
        // if item is at the top of list, it can't be moved further up
        else if (targetToMove == 0) {
            return list;
        }
        // if not at top of list, move up in list
        else {
            int aboveToMove = targetToMove - 1;
            // swaps items within list
            Collections.swap(list, aboveToMove, targetToMove);
            return list;
        }
    }

    // list: Favorites, doNotShow, or toExplore, itemToMove: name of item to move
    public static List<Info> moveItemDown(List<Info> list, Info i) {
        // searches for index of itemToMove in list
        int targetToMove = list.indexOf(i);
        // item was not found in list
        if (targetToMove == -1) {
            return list;
        }
        // if item is at the top of list, it can't be moved further down
        else if (targetToMove == list.size() - 1 ) {
            return list;
        } // if not at top of list, move up in list
        else {
            int aboveToMove = targetToMove + 1;
            // swaps items within list
            Collections.swap(list, aboveToMove, targetToMove);
            return list;
        }
    }
}

/*
public static List<Info> sortAlphabetically(List<Info> list) {
        if (list == null) return null;

        Collections.sort(list, new AlphabeticalComp());

        return list;
    }

    public static List<Info> sortByRating(List<Info> list) {
        if (list == null) return null;

        Collections.sort(list, new RatingComp());

        return list;
    }

    static class RatingComp implements Comparator<Info> {
        @Override
        public int compare(Info i1, Info i2) {
            int rating1 = i1.rating;
            int rating2 = i2.rating;
            if (rating1 < rating2) return 1;
            else return -1;
        }
    }

    static class AlphabeticalComp implements Comparator<Info> {
        @Override
        public int compare(Info i1, Info i2) {
            String name1 = i1.name;
            String name2 = i2.name;
            if (name1.compareTo(name2) > 0) return 1;
            else return -1;
        }
    }
 */

