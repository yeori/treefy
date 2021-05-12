package github.yeori.treefy.sample;

import java.util.ArrayList;
import java.util.List;

public class Table {

    String name;
    Schema schema;
    List<Column> columns;

    public Table() {}
    public Table(String name) {
        this.name = name;
    }

    public Table(String name, Schema schema) {
        this.name = name;
        this.schema = schema;
    }

    public void addColumn(Column c) {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        columns.add(c);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns == null ? null : new ArrayList<>(columns);
    }
}
