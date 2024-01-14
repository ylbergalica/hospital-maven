package city.org.rs;

import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig 
{
	public Application() 
	{
		packages("city.org.rs");

		//Register Auth Filter here
		register(AuthenticationFilter.class);
		register(CORSFilter.class);
	}
}
