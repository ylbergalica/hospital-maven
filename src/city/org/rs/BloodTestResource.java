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


@Path("/tests")
public class BloodTestResource {
	
	private DAO dao = DAO.getInstance();
	
	//API to list all tests
	@GET
    @RolesAllowed({"ADMIN", "PHYSICIAN"})
	@Produces(MediaType.APPLICATION_JSON)
	public List<BloodTest> list() {
		return dao.listAll();
	}

	//API to get all unique patient names
	@GET
	@Path("/patients")
    @PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response get() {
        List<String> patients = dao.getPatients();
        if (patients != null) {
            return Response.ok(patients).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
	}

	@POST
    @RolesAllowed("ADMIN")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(BloodTest test) throws URISyntaxException {

        //add a new blood test
        if (dao.add(test)) {
            return Response.ok().build();
        }

        return Response.notModified().build();
	}

	//API to delete test
	@DELETE
    @RolesAllowed("ADMIN")
	@Path("{test_id}")
	public Response delete(@PathParam("test_id") int id) {
		if (dao.delete(id)) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
	//API to get all patient tests by name
	@GET
	@Path("{name}")
    @RolesAllowed({"ADMIN", "PHYSICIAN"})
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name) {
        List<BloodTest> tests = dao.get(name);
        if (tests != null) {
            return Response.ok(tests).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
	}

    //get the tests in specific time period
	@GET
	@Path("{name}/{start}/{end}")
    @RolesAllowed({"ADMIN", "PHYSICIAN"})
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name, @PathParam("start") String start, @PathParam("end") String end) {
		List<BloodTest> tests = dao.getPeriod(name, start, end);
		if (tests != null) {
			return Response.ok(tests).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
	}

	//get the average blood glucose level in specific time period
	@GET
	@Path("glucose/{name}/{start}/{end}")
    @RolesAllowed({"ADMIN", "PHYSICIAN"})
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGlucose(@PathParam("name") String name, @PathParam("start") String start, @PathParam("end") String end) {
		double glucose = dao.getGlucose(name, start, end);
		if (glucose != 0) {
			return Response.ok(glucose).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
	}

	//get the average carb intake in specific time period
	@GET
	@Path("carbs/{name}/{start}/{end}")
    @RolesAllowed({"ADMIN", "PHYSICIAN"})
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCarbs(@PathParam("name") String name, @PathParam("start") String start, @PathParam("end") String end) {
		double carb = dao.getCarbs(name, start, end);
		if (carb != 0) {
			return Response.ok(carb).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
	}
    
	@PUT
	@RolesAllowed("ADMIN")
	@Path("{test_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("test_id") int id, BloodTest test) {
		test.setTest_id(id);
		if (dao.updateTest(test)) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
}
