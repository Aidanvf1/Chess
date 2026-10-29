package dataaccess;

import dataaccess.sql.DatabaseInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializerTests {

    @Test
    public void initializeCreatesAllTables() throws DataAccessException, SQLException {
        DatabaseInitializer.initialize();

        List<String> tableNames = listTableNames();
        Assertions.assertTrue(tableNames.containsAll(List.of("user", "auth", "game")));
    }

    @Test
    public void initializeCanRunTwice() throws DataAccessException, SQLException {
        DatabaseInitializer.initialize();
        DatabaseInitializer.initialize();

        Assertions.assertTrue(listTableNames().contains("user"));
    }

    private List<String> listTableNames() throws DataAccessException, SQLException {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()";
        List<String> names = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                names.add(resultSet.getString(1).toLowerCase());
            }
        }
        return names;
    }
}
