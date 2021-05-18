package github.yeori.treefy.sample;

import java.util.ArrayList;
import java.util.List;

public class Schema {

    String name;
    List<Table> tables;

    public Schema() {
    }

    public Schema(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables == null ? null : new ArrayList<>(tables);
    }
}
