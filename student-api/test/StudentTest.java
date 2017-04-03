import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.json.JSONObject;
import org.junit.Test;

//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.json.JSONConfiguration;

import models.Student;

/**
 * Simple (JUnit) tests that can call all parts of a play app.
 *
 * https://www.playframework.com/documentation/latest/JavaTest
 */
public class StudentTest {

	private static final String BASE_URL = "http://localhost:9000";

	@Test
	public void whenCreatesRecord_thenCorrect() throws Exception {
		Student student = new Student("ho5", "ks5", 35);
		JSONObject obj = new JSONObject(makeRequest(BASE_URL, "POST", new JSONObject(student)));

		assertTrue(obj.getBoolean("isSuccessfull"));

		JSONObject body = obj.getJSONObject("body");

		assertEquals(student.getAge(), body.getInt("age"));
		assertEquals(student.getFirstName(), body.getString("firstName"));
		assertEquals(student.getLastName(), body.getString("lastName"));
	}

	public static String makeRequest(String myUrl, String httpMethod, JSONObject parameters) throws Exception {
		URL url = null;
		url = new URL(myUrl);
		HttpURLConnection conn = null;
		conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		DataOutputStream dos = null;
		conn.setRequestMethod(httpMethod);

		if (Arrays.asList("POST", "PUT").contains(httpMethod)) {
			String params = parameters.toString();
			conn.setDoOutput(true);
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(params);
			dos.flush();
			dos.close();
		}

		int respCode = conn.getResponseCode();
		if (respCode != 200 && respCode != 201) {
			String error = inputStreamToString(conn.getErrorStream());
			return error;
		}
		String inputString = inputStreamToString(conn.getInputStream());

		return inputString;
	}

	public static String inputStreamToString(InputStream is) throws Exception {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}

	
	public static void main(String[] args) {
		
       /* // Create Jersey client
        ClientConfig clientConfig = new DefaultClientConfig();
        ((DefaultClientConfig) clientConfig).getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(clientConfig);
		
		String getListURL = BASE_URL + "all";
		WebResource webResourceGet = client.resource((String) getListURL);
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_XML);
		response.*/
		
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFilter.class ) );
		WebTarget webTarget = client.target(BASE_URL + "all");
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
	}
	
	/*
	 * @Test public void checkIndex() { JPAApi jpaApi = mock(JPAApi.class);
	 * FormFactory formFactory = mock(FormFactory.class); final PersonController
	 * controller = new PersonController(formFactory, jpaApi); final Result
	 * result = controller.index();
	 * 
	 * assertEquals(OK, result.status()); }
	 * 
	 * @Test public void checkTemplate() { Content html =
	 * views.html.index.render(); assertEquals("text/html", html.contentType());
	 * assertTrue(contentAsString(html).contains("Add Person")); }
	 */
}
