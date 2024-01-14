package city.org.rs;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest") // Optional, specify the base path for your RESTful resources
public class MyApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(AuthenticationFilter.class);
        classes.add(CORSFilter.class);
        // Add other resource classes if needed
        return classes;
    }
}