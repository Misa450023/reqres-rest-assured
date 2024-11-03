package interfaces;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@FunctionalInterface
public interface HttpMethod {

	Response execute(RequestSpecification rs);

}
