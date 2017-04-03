package controllers;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.Student;
import models.StudentStore;
import play.data.FormFactory;
//import play.api.libs.ws.WSClient;
//import play.api.libs.ws.WSRequest;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import util.Util;

public class ApiGatewayController extends Controller {

	private static final String HOST_URL = "http://localhost:9000/";
	
	private final FormFactory formFactory;
	private final WSClient ws;

	@Inject
	public ApiGatewayController(FormFactory formFactory, WSClient ws) {
		this.formFactory = formFactory;
		this.ws = ws;
	}
	
	public Result create() {
		System.out.println("json: " + request().body().asJson());
		
/*		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest(Util.createResponse("Expecting Json data", false));
		}
		System.out.println("json: " + json);
		WSRequest request = ws.url("http://localhost:9000/");		
		
		CompletionStage<WSResponse> wsresponse = request.setContentType("application/json").post(json);
//		JsonNode jsonObject = Json.toJson(response);
		
		wsresponse.thenApply(response -> {
			return Util.createResponse(response.asJson(), true); 
		});
		return null;*/
		
//		Student student = StudentStore.getInstance().addStudent((Student) Json.fromJson(json, Student.class));
//		JsonNode jsonObject = Json.toJson(student);
//		return created(Util.createResponse(jsonObject, true));
		
		Map<String, String> params = formFactory.form().bindFromRequest().data();
		System.out.println("params: " + params);
		ws.url(HOST_URL + "").post(Json.toJson(params));
		
		return ok();
	}

	public CompletionStage<Result> update() {
		
		JsonNode json = request().body().asJson();
		System.out.println("update json: " + json);
		Map<String, String> params = formFactory.form().bindFromRequest().data();
		System.out.println("params: " + params);
		CompletionStage<WSResponse> updateResponse = ws.url(HOST_URL + "").put(json);
		
		return updateResponse.thenApply(response -> ok(response.asJson()));
		
		
		/*if (json == null) {
			return badRequest(Util.createResponse("Expecting Json data", false));
		}
		Student student = StudentStore.getInstance().updateStudent((Student) Json.fromJson(json, Student.class));
		if (student == null) {
			return notFound(Util.createResponse("Student not found", false));
		}

		JsonNode jsonObject = Json.toJson(student);
		return ok(Util.createResponse(jsonObject, true));*/
	}

	public CompletionStage<Result> retrieve(String id) {
		
		/*Student student = StudentStore.getInstance().getStudent(id);
		if (student == null) {
			return notFound(Util.createResponse("Student with id:" + id + " not found", false));
		}
		JsonNode jsonObjects = Json.toJson(student);
		return ok(Util.createResponse(jsonObjects, true));*/
		
		System.out.println("play as api gateway to retrieve student: " + id);
		CompletionStage<WSResponse> retrieveResponse = ws.url(HOST_URL + "find/" + id).get();
		return retrieveResponse.thenApply(response -> ok(response.asJson()));
	}

	public CompletionStage<Result> retrieveBy() {
		
		System.out.println("play as api gateway to retrieveBy");
		Map<String, String> params = formFactory.form().bindFromRequest().data();
		System.out.println("params: " + params);
		JsonNode json = request().body().asJson();
		System.out.println("update json: " + json);
		
		WSRequest request = ws.url(HOST_URL + "retrieveBy");
		for(String paramName : params.keySet()) {
			System.out.println(paramName + ": " + params.get(paramName));
			request.setQueryParameter(paramName, params.get(paramName));
		}
		
//		CompletionStage<WSResponse> responsePromise = ws.url(HOST_URL + "all").get();
		CompletionStage<WSResponse> responsePromise = request.get();
		return responsePromise.thenApply(response -> ok(response.asJson()));
	}
	
/*	public Result deleteStudent(String id) {
		System.out.println(id);
		boolean status = StudentStore.getInstance().deleteStudent(Integer.parseInt(id));
		if (!status) {
			return notFound(Util.createResponse("Student with id:" + id + " not found", false));
		}
		return ok(Util.createResponse("Student with id:" + id + " deleted", true));
	}*/

	public Result deleteStudent(String id) {
		
		System.out.println("json: " + request().body().asJson());
		
		Map<String, String> params = formFactory.form().bindFromRequest().data();
		System.out.println("params: " + params);
//		ws.url(HOST_URL + id).post(Json.toJson(params));
		ws.url(HOST_URL + id).delete();
		
		/*boolean status = StudentStore.getInstance().deleteStudent(Integer.parseInt(id));
		if (!status) {
			return notFound(Util.createResponse("Student with id:" + id + " not found", false));
		}*/
		return ok(Util.createResponse("Student with id:" + id + " deleted", true));
	}
	
	public CompletionStage<Result> listStudents() {
		System.out.println("play as api gateway to list students");
		CompletionStage<WSResponse> responsePromise = ws.url(HOST_URL + "all").get();
		return responsePromise.thenApply(response -> ok(response.asJson()));
	}

}
