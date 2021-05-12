package github.yeori.treefy.sample;

public class Column {

    String name;
    String type;
    Integer order;
    Table table;

    public Column() {
    }

    public Column(String name, String type, Integer order, Table table) {
        this.name = name;
        this.type = type;
        this.order = order;
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}
