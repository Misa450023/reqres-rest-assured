package httpRest;

import com.github.javafaker.Faker;

import interfaces.HttpSpec;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import pojos.RegistrationData;
import util.MyKeys;
import util.UtilMethods;

public class RequestSpecifications {

	static RequestSpecification rs;
	public static String page = "";
	public static String userId;
	static Faker f = new Faker();

	public static HttpSpec<RequestSpecification> get_list_of_users = () -> {

		rs = RestAssured.given();
		rs.baseUri(MyKeys.baseURL + "/users");
		return rs;
	};
	public static HttpSpec<RequestSpecification> get_list_of_users_pag = () -> {

		rs = RestAssured.given();
		rs.baseUri(MyKeys.baseURL + "/users");
		rs.queryParams("page", page);
		return rs;
	};
	public static HttpSpec<RequestSpecification> get_user = () -> {

		rs = RestAssured.given();
		rs.baseUri(MyKeys.baseURL + "/users");
		if (userId != null) {
			rs.param("id", userId);
		}
		return rs;
	};

	public static HttpSpec<RequestSpecification> create_user = () -> {

		rs = RestAssured.given();
		rs.baseUri(MyKeys.baseURL + "/users");
		rs.body(UtilMethods.toJsonString.execute(new RegistrationData(f.name().firstName(), f.job().position())));
		rs.contentType(ContentType.JSON);
		return rs;
	};

	public static HttpSpec<RequestSpecification> register = () -> {

		rs = RestAssured.given();
		rs.baseUri(MyKeys.baseURL + "/register");
		rs.body(MyKeys.registrationData);
		rs.contentType(ContentType.JSON);
		return rs;
	};

//@reqres.in

}
