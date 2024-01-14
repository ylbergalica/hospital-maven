package city.org.rs;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;


// import org.glassfish.jersey.internal.util.Base64;

/**
 * This filter verify the access permissions for a user
 * based on username and passowrd provided in request
 * */
@Provider
public class AuthenticationFilter implements jakarta.ws.rs.container.ContainerRequestFilter
{
	
	@Context
    private ResourceInfo resourceInfo;
	
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "JWT";
    private static final String secret = "secret";
    private static DAO dao = DAO.getInstance();


     
    @Override
    public void filter(ContainerRequestContext requestContext)
    {
        Method method = resourceInfo.getResourceMethod();
        //Access allowed for all
        if( ! method.isAnnotationPresent(PermitAll.class))
        {
             

            if(method.isAnnotationPresent(DenyAll.class))
            {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Access blocked for all users !!").build());
                return;
            }

            
            try 
            {


            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
             
            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            

            //Get encoded username and password
            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
            System.out.println("rolesSet: " + rolesSet);

            if(authorization == null || authorization.isEmpty())
            {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("You cannot access this resource").build());
                return;
            }

            String token = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            String username = jwt.getClaim("username").asString();
            String password = jwt.getClaim("password").asString();

            //Verify user access
            if( ! isUserAllowed(token, rolesSet))
            {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("You cannot access this resource").build());
                return;
            }
            
            }catch (Exception e) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("You cannot access this resource").build());
                return;
            }


            

        }
    }

    private boolean isUserAllowed(final String token, final Set<String> rolesSet)
    {
        boolean isAllowed = false;
         
        String username = JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getClaim("username").asString();
        String password = JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getClaim("password").asString();
        String power_level = dao.getRoleByUsername(username);



        if(dao.isValidUser(username, password) && rolesSet.contains(power_level)){
            isAllowed = true;
        }

        

        return isAllowed;
    }
}