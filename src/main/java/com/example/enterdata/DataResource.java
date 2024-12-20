package com.example.enterdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.jdbc.MysqlDataSource;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;

import javax.sql.DataSource;
import java.sql.*;

@Path("/data")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DataResource {
    private static boolean authorization;
    @Path("/auth")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(Credentials credentials) {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://auth:8080/Authentication/api/auth/auth");
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(credentials);
            try(Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(json))){
                if (response.getStatus() != 200) {
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            }
            client.close();
            authorization = true;
            return Response.status(Response.Status.ACCEPTED).header("Authorization",true).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.serverError().build();
        }

    }
    private DataSource getDataSource() throws SQLException {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("mysql");
        ds.setPort(3306);
        ds.setDatabaseName("data");
        ds.setUser("root");
        ds.setPassword("123456");
        ds.setUseSSL(false);
        ds.setAllowPublicKeyRetrieval(true);

        return ds;
    }


    @Path("/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addData(Data data) {
        // Validate user credentials
        if (!authorization) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Insert data into MySQL database
        try (Connection conn = getDataSource().getConnection()) {
            String sql = "INSERT INTO data (grade) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, data.getGrade());
                stmt.executeUpdate();
            }
            Client client = ClientBuilder.newClient();

            // Create a target for the API endpoint
            WebTarget target = client.target("http://ana:8080/Analytics/api/data/analyze");
            target.request().header("Authorization", authorization);
            conn.close();
            // Send the empty POST request
            Response response = target.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization",true)
                    .post(Entity.json(""));

            if (response.getStatus() != 200) {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.CREATED).build();
    }
}