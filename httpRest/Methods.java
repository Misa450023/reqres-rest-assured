package httpRest;

import interfaces.HttpMethod;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Methods {

	public static HttpMethod httpGet = (RequestSpecification rs) -> {
		return rs.get();
	};
	public static HttpMethod httpPost = (RequestSpecification rs) -> {
		return rs.post();
	};

}
