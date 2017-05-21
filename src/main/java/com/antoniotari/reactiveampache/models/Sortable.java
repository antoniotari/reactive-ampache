package com.antoniotari.reactiveampache.models;

/**
 * Created by antoniotari on 2017-05-21.
 */

public interface Sortable {

    enum SortOption {
        NAME("Name"),
        YEAR("Year"),
        TAG("");

        String name;

        SortOption(String name) {
            this.name = name;
        }

        public int getId() {
            return ordinal();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    String getSortName();
    String getSortYear();
    String getSortTag();
}
