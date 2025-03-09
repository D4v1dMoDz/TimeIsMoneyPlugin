package spigot.plugin.dao;

import spigot.plugin.TimeIsMoney;
import spigot.plugin.database.DatabaseManager;
import spigot.plugin.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements GenericDao<User, Integer> {
    private static UserDAO instance;

    private UserDAO() {
    }

    public static UserDAO getInstance() {
        if(instance == null) {
            instance = new UserDAO();
        }

        return instance;
    }

    @Override
    public Optional<User> getById(Integer id) {
        String selectQuery = "SELECT * FROM users WHERE id = ?";
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, id);

            try(ResultSet resultSet = selectStatement.executeQuery();) {
                if(resultSet.next()) {
                    return Optional.of(mapResultSetToUser(resultSet));
                }
            }
        } catch(SQLException ex) {
            TimeIsMoney.getInstance().getLogger().severe("Database query error! " + ex.getMessage());
        }

        return Optional.empty();
    }

    public Optional<User> getByUsername(String username) {
        String selectQuery = "SELECT * FROM users WHERE username = ?";
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setString(1, username);

            try(ResultSet resultSet = selectStatement.executeQuery();) {
                if(resultSet.next()) {
                    return Optional.of(mapResultSetToUser(resultSet));
                }
            }
        } catch (SQLException ex) {
            TimeIsMoney.getInstance().getLogger().severe("Database query error! " + ex.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<User> getAll() {
        String selectQuery = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery()) {

            while(resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException ex) {
            TimeIsMoney.getInstance().getLogger().severe("Database query error! " + ex.getMessage());
        }

        return users;
    }

    @Override
    public User save(User entity) {
        String insertQuery = "INSERT INTO users(username, money, last_request_date) VALUES(?, ?, ?)";
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getUsername());
            preparedStatement.setInt(2, entity.getMoney());
            preparedStatement.setTimestamp(3, new Timestamp(entity.getLastRequestDate().getTime()));

            int result = preparedStatement.executeUpdate();
            if(result > 0) {
                try(ResultSet generatedKey = preparedStatement.getGeneratedKeys()) {
                    if(generatedKey.next()) {
                        return getById(generatedKey.getInt(1)).orElse(null);
                    }
                }
            }
        } catch (SQLException ex) {
            TimeIsMoney.getInstance().getLogger().severe("Database query error! " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void update(User entity) {
        if(entity.getId() != null) {
            String updateQuery = "UPDATE users SET money = ?, last_request_date = ? WHERE id = ?";
            try(Connection connection = DatabaseManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, entity.getMoney());
                preparedStatement.setTimestamp(2, new Timestamp(entity.getLastRequestDate().getTime()));
                preparedStatement.setInt(3, entity.getId());

                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                TimeIsMoney.getInstance().getLogger().severe("Database query error! " + ex.getMessage());
            }
        }
    }

    @Override
    public void delete(Integer id) {
        String deleteQuery = "DELETE FROM users WHERE id = ?";
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            TimeIsMoney.getInstance().getLogger().severe("Database query error! " + ex.getMessage());
        }
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(resultSet.getInt("id"), resultSet.getString("username"),
                resultSet.getInt("money"), resultSet.getTimestamp("last_request_date"));
    }
}
