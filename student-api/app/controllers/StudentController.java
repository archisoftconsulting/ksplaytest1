package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import models.Student;
import models.StudentStore;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.Util;

public class StudentController extends Controller {

	public Result create() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest(Util.createResponse("Expecting Json data", false));
		}
		System.out.println("json:::: " + json.toString());
		
		DBObject dbObject = (DBObject)JSON.parse(json.toString());
		
		/*MongoClient mongo = new MongoClient( Arrays.asList(new ServerAddress("localhost", 27017)) );
		DB db = mongo.getDB("persondb");*/
//		DBCollection table = db.getCollection("users");
//		table.insert(dbObject);
		
		/*Set<String> collectionNames = db.getCollectionNames();		
		for(String collectionName : collectionNames) {
			System.out.println("collectionName: " + collectionName);
		}*/
		
//		DBCollection table = db.getCollection("users");
		
		DBCollection table = getUsersTable();
		System.out.println("count: " + table.getCount());
		WriteResult addResult = table.insert(dbObject);
		System.out.println("addResult.getUpsertedId(): " + addResult.getN());
		
		Student student = StudentStore.getInstance().addStudent((Student) Json.fromJson(json, Student.class));
		JsonNode jsonObject = Json.toJson(student);
		return created(Util.createResponse(jsonObject, true));
	}

	public Result update() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest(Util.createResponse("Expecting Json data", false));
		}
		
		BasicDBObject updateDocument = new BasicDBObject();
		BasicDBObject searchQuery = new BasicDBObject();
				
		Iterator<String> fieldNames = json.fieldNames();
		System.out.println("=====================================");
		while(fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			if(fieldName.equals("id")) {
//				searchQuery.append("_id", new ObjectId(json.get(fieldName).asText()));
				searchQuery.put("_id", new ObjectId(json.get(fieldName).asText()));
			} else {
				updateDocument.put(fieldName, json.get(fieldName).asText());
			}
			
			System.out.println(fieldName + ": " + json.get(fieldName).asText());
		}
		
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", updateDocument);
		WriteResult updateResult = getUsersTable().update(searchQuery, updateObj);
		
//		WriteResult updateResult = getUsersTable().update(searchQuery, new BasicDBObject().append("$set", updateDocument));
		System.out.println(updateResult.toString());
		
		/*Student student = StudentStore.getInstance().updateStudent((Student) Json.fromJson(json, Student.class));
		if (student == null) {
			return notFound(Util.createResponse("Student not found", false));
		}

		JsonNode jsonObject = Json.toJson(student);
		return ok(Util.createResponse(jsonObject, true));*/
		return retrieve(json.get("id").asText());
	}

	public Result retrieve(String id) {
		/*Student student = StudentStore.getInstance().getStudent(id);
		if (student == null) {
			return notFound(Util.createResponse("Student with id:" + id + " not found", false));
		}
		JsonNode jsonObjects = Json.toJson(student);
		return ok(Util.createResponse(jsonObjects, true));*/
		System.out.println("id to retrieve: " + id);		
		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", new ObjectId(id));
		DBCursor cursor = getUsersTable().find(searchQuery);		
		System.out.println("student: " + cursor.next());
		JSON json =new JSON();
        String serialize = json.serialize(cursor);
        System.out.println(serialize);
				
//		JsonNode jsonData = mapper.convertValue(result, JsonNode.class);
		JsonNode jsonData = Json.parse(serialize);
		System.out.println("jsonData: " + jsonData);
//		return ok(Util.createResponse(jsonData, true));
		return ok(jsonData);
	}
	
	public Result retrieveBy() {
		
		String firstName = request().queryString().get("firstName")[0];
		String lastName = request().queryString().get("lastName")[0];		
		System.out.println("firstName: " + firstName);
		System.out.println("lastName: " + lastName);
//		System.out.println("lastName: " + request().queryString().get("age")[0]);
		
		BasicDBObject criteria = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		if(firstName != null && firstName.length() > 0) {
//			obj.add(new BasicDBObject("firstName", firstName));	
			obj.add(new BasicDBObject("firstName", java.util.regex.Pattern.compile(firstName)));
		}
		if(lastName != null && lastName.length() > 0) {
//			obj.add(new BasicDBObject("lastName", lastName));
			obj.add(new BasicDBObject("lastName", java.util.regex.Pattern.compile(lastName)));
		}		
		if(obj != null && obj.size() > 0) {
			criteria.put("$and", obj);			
		}
		
//		criteria.put("firstname", new BasicDBObject("$eq", lastName));
		System.out.println(criteria.toString());
		
		DBCursor cursor = getUsersTable().find(criteria);
		while (cursor.hasNext()) {
			System.out.println("query result: " + cursor.next());
		}		
		
		JSON json =new JSON();
        String serialize = json.serialize(cursor);
//        System.out.println(serialize);
				
//		JsonNode jsonData = mapper.convertValue(result, JsonNode.class);
		JsonNode jsonData = Json.parse(serialize);
		return ok(Util.createResponse(jsonData, true));
		
//		return listStudents();
	}

/*	public Result delete(int id) {
		boolean status = StudentStore.getInstance().deleteStudent(id);
		if (!status) {
			return notFound(Util.createResponse("Student with id:" + id + " not found", false));
		}
		return ok(Util.createResponse("Student with id:" + id + " deleted", true));
	}*/

	public Result delete(String id) {
		
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		getUsersTable().remove(query);		
		
		return ok(Util.createResponse("Student with id:" + id + " deleted", true));
	}
	
	public Result listStudents() {
		Set<Student> result = StudentStore.getInstance().getAllStudents();
		ObjectMapper mapper = new ObjectMapper();

		/*MongoClient mongo = new MongoClient( Arrays.asList(new ServerAddress("localhost", 27017)) );
		DB db = mongo.getDB("persondb");
		DBCollection table = db.getCollection("users");*/
		
		BasicDBObject searchQuery = new BasicDBObject();
		DBCursor cursor = getUsersTable().find(searchQuery);		
		
		JSON json =new JSON();
        String serialize = json.serialize(cursor);
//        System.out.println(serialize);
				
//		JsonNode jsonData = mapper.convertValue(result, JsonNode.class);
		JsonNode jsonData = Json.parse(serialize);
		return ok(Util.createResponse(jsonData, true));
	}

	
	private DBCollection getUsersTable() {
		MongoClient mongo = new MongoClient( Arrays.asList(new ServerAddress("localhost", 27017)) );
		DB db = mongo.getDB("persondb");
		DBCollection table = db.getCollection("users");
		
		return table;
	}
}
