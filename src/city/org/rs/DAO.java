package city.org.rs;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;


public final class DAO {
	private static DAO instance; 

	private static final String URL = "jdbc:mysql://localhost:3306/hospital";

    private static final String USER = "root";

    private static final String PASSWORD = "";

    private Connection connection;
	
	//Private constructor
	private DAO() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException | SQLException e) {

            e.printStackTrace(); 

        }
	}


	public static DAO getInstance() {
		if (instance == null) {
			instance = new DAO(); 
		}
		
		return instance;				
	}

	//Get all patient names
	//Returns patient names retrieved
	public List<String> getPatients() {
		//get the data from the bloodtests table for the given patient name
		List<String> patients = new ArrayList<>();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT patient_name FROM bloodtests");

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				patients.add(results.getString("patient_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return patients;
	}

	public List<BloodTest> get(String name) {
		//get the data from the bloodtests table for the given patient name
		List<BloodTest> bloodtests = new ArrayList<>();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM bloodtests WHERE patient_name = ?");
			statement.setString(1, name);
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				BloodTest bloodtest = new BloodTest();
				bloodtest.setTest_id(results.getInt("test_id"));
				bloodtest.setPatient_name(results.getString("patient_name"));
				bloodtest.setBlood_glucose_level(results.getDouble("blood_glucose_level"));
				bloodtest.setCarb_intake(results.getDouble("carb_intake"));
				bloodtest.setMedication_dose(results.getDouble("medication_dose"));
				bloodtests.add(bloodtest);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bloodtests;
	}
	
	//Remove bloodtest from data list
	//Returns true (on success) or false (on failure)
	public boolean delete(int id) {
		//delete the bloodtest from the bloodtests table
		try {

			PreparedStatement statement = connection.prepareStatement("DELETE FROM bloodtests WHERE test_id = ?");
			statement.setInt(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public boolean add(BloodTest test) {
		//add the bloodtest to the bloodtests table
		try {

			PreparedStatement statement = connection.prepareStatement("INSERT INTO bloodtests (patient_name, blood_glucose_level, carb_intake, medication_dose) VALUES (?, ?, ?, ?)");
			statement.setString(1, test.getPatient_name());
			statement.setDouble(2, test.getBlood_glucose_level());
			statement.setDouble(3, test.getCarb_intake());
			statement.setDouble(4, test.getMedication_dose());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public List<BloodTest> listAll() {

		//get the data from the bloodtests table
		List<BloodTest> bloodtests = new ArrayList<>();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM bloodtests");
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				BloodTest bloodtest = new BloodTest();
				bloodtest.setTest_id(results.getInt("test_id"));
				bloodtest.setPatient_name(results.getString("patient_name"));
				bloodtest.setBlood_glucose_level(results.getDouble("blood_glucose_level"));
				bloodtest.setCarb_intake(results.getDouble("carb_intake"));
				bloodtest.setMedication_dose(results.getDouble("medication_dose"));
				bloodtest.setCreated_at(results.getString("created_at"));
				bloodtests.add(bloodtest);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return bloodtests;
		
	}
	
	public List<User> listUsers() {
		
		//get the data from the bloodtests table
		List<User> users = new ArrayList<>();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				User user = new User();
				user.setUser_id(results.getInt("user_id"));
				user.setUsername(results.getString("username"));
				user.setPassword(results.getString("password"));
				user.setPower_level(results.getString("power_level"));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	public void insertUser(User user) {
		//add the bloodtest to the bloodtests table
		try {
			String hashedPassword = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
			PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password, power_level) VALUES (?, ?, ?)");
			statement.setString(1, user.getUsername());
			statement.setString(2, hashedPassword);
			statement.setString(3, user.getpower_level());
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

    public boolean login(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            statement.setString(1, user.getUsername());
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                // Retrieve the hashed password from the database
                String hashedPasswordFromDB = results.getString("password");

                // Check if the provided password matches the hashed password from the database
                boolean passwordMatches = BCrypt.verifyer().verify(user.getPassword().toCharArray(), hashedPasswordFromDB).verified;

                return passwordMatches;
            } else {
                // User not found
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

	public User getUser(int user_id) {
		//add the bloodtest to the bloodtests table
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE user_id = ?");
			statement.setInt(1, user_id);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				User user = new User();
				user.setUser_id(results.getInt("user_id"));
				user.setUsername(results.getString("username"));
				user.setPassword(results.getString("password"));
				user.setPower_level(results.getString("power_level"));
				return user;
			} else {
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;

	}

	public boolean deleteUser(int user_id) {
		//add the bloodtest to the bloodtests table
		try {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?");
			statement.setInt(1, user_id);
			statement.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public List<BloodTest> getPeriod(String name, String start, String end) {
		List<BloodTest> bloodtests = new ArrayList<>();
		
		try {
			// Assuming start and end are in the format "yyyy-MM-dd"
			Timestamp startDate = Timestamp.valueOf(start + " 00:00:00");
			Timestamp endDate = Timestamp.valueOf(end + " 23:59:59");

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM bloodtests WHERE patient_name = ? AND created_at BETWEEN ? AND ?");
			statement.setString(1, name);
			statement.setTimestamp(2, startDate);
			statement.setTimestamp(3, endDate);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				BloodTest bloodtest = new BloodTest();
				bloodtest.setTest_id(results.getInt("test_id"));
				bloodtest.setPatient_name(results.getString("patient_name"));
				bloodtest.setBlood_glucose_level(results.getDouble("blood_glucose_level"));
				bloodtest.setCarb_intake(results.getDouble("carb_intake"));
				bloodtest.setMedication_dose(results.getDouble("medication_dose"));
				bloodtest.setCreated_at(results.getString("created_at"));
				bloodtests.add(bloodtest);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bloodtests;
	}

	public double getGlucose(String name, String start, String end) {
		double glucose = 0;
		
		try {
			// Assuming start and end are in the format "yyyy-MM-dd"
			Timestamp startDate = Timestamp.valueOf(start + " 00:00:00");
			Timestamp endDate = Timestamp.valueOf(end + " 23:59:59");

			PreparedStatement statement = connection.prepareStatement("SELECT AVG(blood_glucose_level) FROM bloodtests WHERE patient_name = ? AND created_at BETWEEN ? AND ?");
			statement.setString(1, name);
			statement.setTimestamp(2, startDate);
			statement.setTimestamp(3, endDate);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				glucose = results.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return glucose;
	} 

	public double getCarbs(String name, String start, String end) {
		double carbs = 0;
		
		try {
			// Assuming start and end are in the format "yyyy-MM-dd"
			Timestamp startDate = Timestamp.valueOf(start + " 00:00:00");
			Timestamp endDate = Timestamp.valueOf(end + " 23:59:59");

			PreparedStatement statement = connection.prepareStatement("SELECT AVG(carb_intake) FROM bloodtests WHERE patient_name = ? AND created_at BETWEEN ? AND ?");
			statement.setString(1, name);
			statement.setTimestamp(2, startDate);
			statement.setTimestamp(3, endDate);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				carbs = results.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return carbs;
	}

	public boolean isValidUser(String username, String password) {
		//add the bloodtest to the bloodtests table
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
			statement.setString(1, username);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				String hashedPasswordFromDB = results.getString("password");
				boolean passwordMatches = BCrypt.verifyer().verify(password.toCharArray(), hashedPasswordFromDB).verified;
				return passwordMatches;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;

	}

	public String getRoleByUsername(String username) {
		//add the bloodtest to the bloodtests table
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
			statement.setString(1, username);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getString("power_level");
			} else {
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;

	}

	public boolean updateUser(User user) {
		//add the bloodtest to the bloodtests table
		try {
			PreparedStatement statement = connection.prepareStatement("UPDATE users SET username = ?, password = ?, power_level = ? WHERE user_id = ?");
			String hashedPassword = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
			statement.setString(1, user.getUsername());
			statement.setString(2, hashedPassword);
			statement.setString(3, user.getpower_level());
			statement.setInt(4, user.getUser_id());
			statement.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
		
	public boolean updateTest(BloodTest test) {
		//add the bloodtest to the bloodtests table
		try {
			PreparedStatement statement = connection.prepareStatement("UPDATE bloodtests SET patient_name = ?, blood_glucose_level = ?, carb_intake = ?, medication_dose = ? WHERE test_id = ?");
			statement.setString(1, test.getPatient_name());
			statement.setDouble(2, test.getBlood_glucose_level());
			statement.setDouble(3, test.getCarb_intake());
			statement.setDouble(4, test.getMedication_dose());
			statement.setInt(5, test.getTest_id());
			statement.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}

