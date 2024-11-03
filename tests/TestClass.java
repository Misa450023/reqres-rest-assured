package tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import httpRest.Methods;
import httpRest.MyThread;
import httpRest.RequestSpecifications;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojos.RegistrationData;
import pojos.User;
import util.UtilMethods;

public class TestClass {

	long total_pages;
	List<String> userIDs = new ArrayList<>();

	@Test(priority = 1)
	public void test_getAllUsers() throws JsonMappingException, JsonProcessingException {

		Response resp = Methods.httpGet.execute(RequestSpecifications.get_list_of_users.create());

		total_pages = resp.jsonPath().getLong("total_pages");
		long page = resp.jsonPath().getLong("page");

		Assert.assertNotNull(page);
		Assert.assertFalse(total_pages == 0);

	};

	@Test(priority = 2)
	public void test_getAllUsers_paginated() throws JsonMappingException, JsonProcessingException {

		for (long a = 1; a <= total_pages; a++) {
			RequestSpecifications.page = String.valueOf(a);
			Response resp = Methods.httpGet.execute(RequestSpecifications.get_list_of_users_pag.create());
			List<User> userObjects = UtilMethods.userToJavaObj(resp);

			userIDs = (List) Stream.concat(userIDs.stream(), resp.jsonPath().getList("data.id").stream())
					.collect(Collectors.toList());

			Assert.assertTrue(userObjects.stream().allMatch(e -> e.getEmail().contains("@reqres.in")));
			Assert.assertTrue(userObjects.stream().allMatch(e -> e.getAvatar().contains("jpg")));

			Assert.assertEquals(resp.contentType(), "application/json; charset=utf-8");
			Assert.assertEquals(resp.getHeader("Server"), "cloudflare");
		}
	};

	@Test(priority = 3)
	public void test_get_user() {

		for (int a = 0; a <= userIDs.size(); a++) {

			RequestSpecifications.userId = String.valueOf(a);
			Response resp = Methods.httpGet.execute(RequestSpecifications.get_user.create());

			Assert.assertEquals(resp.statusCode(), 200);
			Assert.assertEquals(resp.header("Vary"), "Accept-Encoding");
			Assert.assertTrue(resp.jsonPath().get("data.email").toString().contains("@reqres.in"));
		}
	}

	@Test(priority = 4)
	public void test_get_user_not_found() {

		RequestSpecifications.userId = String.valueOf("584");
		Response resp = Methods.httpGet.execute(RequestSpecifications.get_user.create());

		Assert.assertEquals(resp.statusLine(), "HTTP/1.1 404 Not Found");
		Assert.assertTrue(resp.body().asString().equals("{}"));
		Assert.assertTrue(resp.getTimeIn(TimeUnit.SECONDS) < 7);
	}

	@Test(priority = 5)
	public void create_user() {

		Response resp = Methods.httpPost.execute(RequestSpecifications.create_user.create());

		resp.then().log().all();

		Assert.assertEquals(resp.statusCode(), 201);
		Assert.assertFalse(resp.jsonPath().get("name").toString().isEmpty());
		Assert.assertNotNull(resp.jsonPath().get("id").toString());
	}

	@Test(priority = 6)
	public void register() {

		Response resp = Methods.httpPost.execute(RequestSpecifications.register.create());

		resp.then().log().all();

		Assert.assertEquals(resp.statusCode(), 200);
		Assert.assertFalse(resp.jsonPath().get("token").toString().isEmpty());
		Assert.assertNotNull(resp.jsonPath().get("id").toString());
	}

	@Test(priority = 7)
	public void create_multipleUserAtSameTime() throws InterruptedException {

		MyThread t1 = new MyThread(Methods.httpPost, RequestSpecifications.create_user.create());
		MyThread t2 = new MyThread(Methods.httpPost, RequestSpecifications.create_user.create());
		MyThread t3 = new MyThread(Methods.httpPost, RequestSpecifications.create_user.create());

		t1.start();
		t2.start();
		t3.start();
		t1.join();
		t2.join();
		t3.join();

		List<Response> resps = MyThread.respList;

		Assert.assertTrue(resps.stream().allMatch(r -> r.statusCode() == 201));
		Assert.assertTrue(resps.stream().allMatch(r -> r.jsonPath().get("id").toString() != null));
		resps.stream().forEach(r -> System.out.println(r.jsonPath().get("name").toString()));
	}

	@Test(priority = 8)
	public void register_multipleUserAtSameTime() throws InterruptedException, ExecutionException {

		int number = 5;

		ExecutorService executor = Executors.newFixedThreadPool(number);
		List<Future<Response>> futures = new ArrayList<>();

		for (int i = 0; i < number; i++) {
			Callable<Response> task = () -> {
				return Methods.httpPost.execute(RequestSpecifications.create_user.create());
			};
			futures.add(executor.submit(task));
		}
		List<Response> resps = futures.stream().map(future -> {
			try {
				return future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		}).filter(r -> r != null).collect(Collectors.toList());

		executor.shutdown();

		Assert.assertTrue(resps.stream().allMatch(r -> r.statusCode() == 201));
		Assert.assertTrue(resps.stream().allMatch(r -> r.jsonPath().get("token").toString() != null));

	}

}
