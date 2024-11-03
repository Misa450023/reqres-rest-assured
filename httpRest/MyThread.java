package httpRest;

import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import interfaces.HttpMethod;

public class MyThread extends Thread{
	
HttpMethod httpMethod;
RequestSpecification rs;
	
public MyThread(HttpMethod httpMethod,RequestSpecification rs) {
	this.httpMethod=httpMethod;
	this.rs=rs;
}

public static List<Response> respList=new ArrayList<>();

@Override
public void run() {
	respList.add(httpMethod.execute(rs));
}

}
