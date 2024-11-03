package util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import interfaces.MyHelper;
import io.restassured.response.Response;
import pojos.User;

public class UtilMethods {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static List<User> userToJavaObj(String json) throws JsonMappingException, JsonProcessingException {

		return objectMapper.readValue(json, new TypeReference<List<User>>() {
		});
	};

	public static List<User> userToJavaObj(Response resp) throws JsonMappingException, JsonProcessingException {

		Map<String, Object> responseMap = objectMapper.readValue(resp.asString(), Map.class);
		List<Map<String, Object>> usersList = (List<Map<String, Object>>) responseMap.get("data");
		List<User> users = objectMapper.convertValue(usersList,
				objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
		return users;
	};

	public static MyHelper<String, String> createMail = (String sufix) -> {
		return String.valueOf(System.currentTimeMillis()) + sufix;
	};

	public static MyHelper<Object, String> toJsonString = (Object obj) -> {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "failed";
		}
	};

}
