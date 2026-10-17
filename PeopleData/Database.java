import java.sql.*;

public class Database {
    private Connection connection;
    private Statement statement;

    // Constructor
    public Database() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/db_people",
                    "root",
                    ""
            );

            statement = connection.createStatement();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Gagal menghubungkan ke database: " + e.getMessage(), e
            );
        }
    }

    // Mengambil data (SELECT)
    public ResultSet selectQuery(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Menambahkan, mengubah, atau menghapus data
    public int insertUpdateDeleteQuery(String sql) {
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Getter koneksi untuk PreparedStatement
    public Connection getConnection() {
        return connection;
    }

    // Getter Statement
    public Statement getStatement() {
        return statement;
    }
}
