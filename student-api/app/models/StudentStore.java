package models;

import java.util.*;

public class StudentStore {
	private Map<Integer, Student> students = new HashMap<>();

	private static StudentStore instance = null;

	private StudentStore() {
	}

	public static StudentStore getInstance() {
		if (instance == null) {
			instance = new StudentStore();
		}
		return instance;
	}

	public Student addStudent(Student student) {
		int id = students.size();
		student.setId(id);
		students.put(id, student);
		return student;
	}

	public Student getStudent(int id) {
		return students.get(id);
	}

	public Set<Student> getAllStudents() {
		return new HashSet<>(students.values());
	}

	public Student updateStudent(Student student) {
		int id = student.getId();
		if (students.containsKey(id)) {
			students.put(id, student);
			return student;
		}
		return null;
	}

	public boolean deleteStudent(int id) {
		return students.remove(id) != null;
	}
}