package api.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.github.javafaker.Faker;
import api.endpoints.UserEndPoints;
import api.payloads.User;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.equalTo;


public class UserTests {
	
	User userpayload;
	Faker faker;
	
	@BeforeClass
	public void setupData()
	{
		faker=new Faker();
		userpayload=new User();
		
		userpayload.setId(faker.idNumber().hashCode());
		userpayload.setUsername(faker.name().username());
		userpayload.setFirstName(faker.name().firstName());
		userpayload.setLastName(faker.name().lastName());
		userpayload.setEmail(faker.internet().safeEmailAddress());
		userpayload.setPassword(faker.internet().password(5, 10));
		userpayload.setPhone(faker.phoneNumber().cellPhone());
	}
	
	@Test(priority=1)
	public void testCreateUser()
	{
		Response response = UserEndPoints.createUser(userpayload);
		response.then()
	    .log().body()
	    .statusCode(200)
	    .assertThat()
	    .body("type", equalTo("unknown"))
	    .body("message", equalTo(userpayload.getId()));
	}
	
	@Test(priority=2)
	public void testGetUserByName()
	{
		Response response = UserEndPoints.getUser(this.userpayload.getUsername());
		response.then()
	    .log().body()
	    .statusCode(200)
	    .assertThat()
	    .body("id", equalTo(userpayload.getId()))
	    .body("username", equalTo(userpayload.getUsername()))
	    .body("firstName", equalTo(userpayload.getFirstName()))
	    .body("lastName", equalTo(userpayload.getLastName()))
	    .body("email", equalTo(userpayload.getEmail()))
	    .body("password", equalTo(userpayload.getPassword()))
	    .body("phone", equalTo(userpayload.getPhone()))
	    .body("userStatus", equalTo(userpayload.getUserStatus()));
	}
	
	@Test(priority=3)
	public void testUpdateUserByName()
	{
		userpayload.setFirstName(faker.name().firstName());
		userpayload.setLastName(faker.name().lastName());
		userpayload.setEmail(faker.internet().safeEmailAddress());
		
		Response response = UserEndPoints.updateUser(userpayload.getUsername(), this.userpayload);
		
		response.then().log().body().statusCode(200);
		
		Response responseAfterUpdate  = UserEndPoints.getUser(userpayload.getUsername());
		responseAfterUpdate.then().log().all();
		//Assert the Response Code
		Assert.assertEquals(responseAfterUpdate.getStatusCode(),200);
		
		String responseBody = responseAfterUpdate.asString();
		JsonPath path = new JsonPath(responseBody);
		//Assert that firstname has been  updated or not.
		String responsefirstname = path.getString("firstName");
		Assert.assertEquals(userpayload.getFirstName(), responsefirstname);	
		
		//Assert that lastName has been  updated or not.
		String responselastName = path.getString("lastName");
		Assert.assertEquals(userpayload.getLastName(), responselastName);	
		
		//Assert that email has been updated or not
		String responseemail = path.getString("email");
		Assert.assertEquals(userpayload.getEmail(), responseemail);	
	}
	
	@Test(priority=5)
	public void testDeleteUserByName()
	{
		Response response = UserEndPoints.deleteUser(userpayload.getUsername());
		response.then().log().body().statusCode(200);
		
		Response responseAfterDelete  = UserEndPoints.getUser(userpayload.getUsername());
		responseAfterDelete.then()
	    .log().body()
	    .statusCode(404)
	    .assertThat()
	    .body("code", equalTo(1))
	    .body("message", equalTo("User not found"));

	}
}
