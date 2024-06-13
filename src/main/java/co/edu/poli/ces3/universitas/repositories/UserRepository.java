package co.edu.poli.ces3.universitas.repositories;

import co.edu.poli.ces3.universitas.dao.User;
import co.edu.poli.ces3.universitas.database.ConexionMySql;
import co.edu.poli.ces3.universitas.database.Crud;
import com.google.gson.Gson;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends ConexionMySql implements Crud { //Heredo de Crud para metodos

    //crear meTodo post, findByEmail, y uso de GSON para usuarios
    private Gson gson = new Gson();
    private Crud userDAO = this;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String json) {
        User user = gson.fromJson(json, User.class);
        try {
            User existingUser = userDAO.findByEmail(user.getEmail());
            if (existingUser != null) {
                return Response.status(Response.Status.OK).entity(gson.toJson(existingUser)).build();
            } else {
                userDAO.create(user);
                User newUser = userDAO.findByEmail(user.getEmail());
                return Response.status(Response.Status.CREATED).entity(gson.toJson(newUser)).build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson("Error: " + e.getMessage())).build();
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE mail = ?";
        try {
            this.createConexion();
            PreparedStatement pstmt = this.getCnn().prepareStatement(sql);
            pstmt.setString(1, email);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return new User(result.getInt("id"),
                        result.getString("name"),
                        result.getString("lastName"),
                        result.getString("mail"),
                        result.getString("password"),
                        result.getDate("createdAt"),
                        result.getDate("updatedAt"),
                        result.getDate("deletedAt"));
            }
            pstmt.close();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.getCnn() != null) {
                this.getCnn().close();
            }
        }
    }
    @Override
    public List<User> get() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> list = new ArrayList<>();
        try {
            this.createConexion();
            Statement stmt = this.getCnn().createStatement();
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()){
                list.add(new User(result.getInt("id"),
                        result.getString("name"),
                        result.getString("lastName"),
                        result.getString("mail"),
                        result.getString("password"),
                        result.getDate("createdAt"),
                        result.getDate("updatedAt"),
                        result.getDate("deletedAt")
                ));
            }
            stmt.close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(this.getCnn() != null)
                this.getCnn().close();
        }
    }

    @Override
    public User getOne(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            createConexion();
            PreparedStatement stm = super.getCnn().prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet result = stm.executeQuery();
            if(result.next())
                return new User(result.getString("name"), result.getString("lastName"));
        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            if (getCnn() != null)
                getCnn().close();
        }
        return null;
    }
    @Override
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO users (name, lastName, mail, password, createdAt, updatedAt, deletedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            this.createConexion();
            PreparedStatement pstmt = this.getCnn().prepareStatement(sql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setDate(5, new java.sql.Date(user.getCreatedAt().getTime()));
            pstmt.setDate(6, new java.sql.Date(user.getUpdatedAt().getTime()));
            if (user.getDeteledAt() != null) {
                pstmt.setDate(7, new java.sql.Date(user.getDeteledAt().getTime()));
            } else {
                pstmt.setNull(7, java.sql.Types.DATE);
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.getCnn() != null) {
                this.getCnn().close();
            }
        }
    }
    @Override
    public void insert(User x) {

    }

    @Override
    public void update(User x) {

    }
}
