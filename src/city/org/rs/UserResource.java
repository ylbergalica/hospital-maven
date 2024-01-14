
package city.org.rs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

// import javax.json.Json;
// import javax.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/users")
public class UserResource {

    private DAO dao = DAO.getInstance();
    String secret = "secret";
    Algorithm algorithm = Algorithm.HMAC256(secret);

    @GET
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> list() {
        return dao.listUsers();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(User user) throws URISyntaxException {
        try {
            dao.insertUser(user);
        } catch (Exception e) {
            return Response.status(404).build();
        }

        return Response.ok().build();
    }

    @GET
    @RolesAllowed("ADMIN")
    @Path("/{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("user_id") int user_id) {
        User user = dao.getUser(user_id);
        if (user != null) {
            return Response.ok(user, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    //login
    @POST
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(User user) throws URISyntaxException {
        if (dao.login(user)) {
            
            // Create token for user
            String token = JWT.create()
                .withClaim("username", user.getUsername())
                .withClaim("password", user.getPassword())
                .sign(algorithm);

			// Create a JSON object to encapsulate the token using Jackson ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonResponse = objectMapper.createObjectNode().put("token", token);

            return Response.ok(jsonResponse.toString()).build();
        } else {
            return Response.status(400).build();
        }
    }

    @DELETE
    @Path("/{user_id}")
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam("user_id") int user_id) {
        if (dao.deleteUser(user_id)) {
            return Response.ok().build();
        } else {
            return Response.notModified().build();
        }
    }

    @PUT
    @Path("/{user_id}")
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("user_id") int user_id, User user) {
        user.setUser_id(user_id);
        if (dao.updateUser(user)) {
            return Response.ok().build();
        } else {
            return Response.notModified().build();
        }
    }

    //getRolebytoken
    @GET
    @Path("/role/{token}")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRolebytoken(@PathParam("token") String token) throws URISyntaxException {
        DecodedJWT jwt = JWT.decode(token);
        String username = jwt.getClaim("username").asString();

        if(dao.getRoleByUsername(username) != null){
            String role = dao.getRoleByUsername(username);
            ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonResponse = objectMapper.createObjectNode().put("role", role);

            return Response.ok(jsonResponse.toString(), MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}   