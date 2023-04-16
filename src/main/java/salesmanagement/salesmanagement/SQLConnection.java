package salesmanagement.salesmanagement;

import java.sql.*;

/**
 * SQLConnection helps you connect to SQL server and database table.
 *
 * @author thanh_an
 * @since 1.0
 */
public class SQLConnection {
    private Connection connection;
    private String url;
    private String user;
    private String password;
    private boolean reconnectingNotification;
    private boolean reconnecting;

    public boolean isReconnecting() {
        return reconnectingNotification;
    }

    public void setReconnecting(boolean reconnectingNotification) {
        this.reconnectingNotification = reconnectingNotification;
    }

    public Connection getConnection() {
        return connection;
    }

    SQLConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        reconnecting = true;
    }

    /**
     * You need url to database server, username and password to log in database server.
     *
     * @see Connection
     * @since 1.0
     */
    public void connectServer() {
        if (reconnecting) {
            reconnecting= false;
            reconnectingNotification = false;
            while (connection == null) {
                try {
                    connection = DriverManager.getConnection(url, user, password);
                } catch (SQLException e) {
                    reconnectingNotification = true;
                    reconnecting =true;
                }

            }
        }
    }

    /**
     * Get result set of a SQL query.
     *
     * @since 1.0
     */
    public ResultSet getDataQuery(String query) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void updateQuery(String query) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Close connection to SQL Server.
     *
     * @since 1.0
     */
    public void addClosingWork() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }
}