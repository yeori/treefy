package github.yeori.treefy;

import github.yeori.treefy.sample.Column;
import github.yeori.treefy.sample.Schema;
import github.yeori.treefy.sample.Table;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static github.yeori.treefy.AssertExt.*;

public class PoJoTreefyTest {

    private Treefy treefy;

    @Before
    public void before(){
        treefy = new Treefy();
    }
    @Test
    public void treefy() {
        Column column = new Column("name", "VARCHAR", 0, null);
        Column clone = treefy.treefy(column, "type");
        assertNulls(clone.getType(), clone.getTable());
        assertNotNulls(clone.getName(), clone.getOrder());
    }

    @Test
    public void test_양방향참조() {
        /*
         *  +---------+         +----------+
         *  |  TABLE  | -----<> |  COLUMN  |
         *  +---------+         +----------+
         *       ^                   |
         *       |                   |
         *       +-------------------+
         */
        Table table = new Table("users", "utf-8");
        Column name = new Column("user_name", "VARCHAR(50)", 0, table);
        Column email = new Column("user_email", "VARCHAR(50)", 1, table);
        table.setColumns(Arrays.asList(name, email));
        /**
         * table에서 각각의 columns마다 table 참조는 제외함
         */
        Table clonedTable = treefy.treefy(table, "columns.table");
        assertNulls(clonedTable.getColumns(), (col) -> col.getTable());
        /**
         * column이 참조하는 table에서 columns 를 제외함
         */
        Column clonedEmail = treefy.treefy(email, "table.columns");
        Table tableRef = clonedEmail.getTable();
        assertNotNull(tableRef);
        assertNull(tableRef.getColumns());
        assertNotNulls(tableRef.getName());
    }
    @Test
    public void test_property_group() {
        Schema schema = new Schema("hello-db");

        Table table = new Table("users", "euc-kr");
        table.setSchema(schema);
        schema.setTables(Arrays.asList(table));

        Column name = new Column("user_name", "VARCHAR(50)", 0, table);
        Column email = new Column("user_email", "VARCHAR(50)", 1, table);
        table.setColumns(Arrays.asList(name, email));

        Column cloned = treefy.treefy(email, "table.(columns, schema.tables)");
//        Column cloned = treefy.treefy(email, "table.schema.tables", "table.columns");
        assertNulls(cloned.getTable().getSchema().getTables(), cloned.getTable().getColumns());
    }
}