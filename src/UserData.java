import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/*
 * Author: Ivan Mykolenko
 * Date: 27.03.2017
 */
public class UserData {
	private Properties config = new Properties();
	private OutputStream output;
	private InputStream input;
	private List<String> favourites = new ArrayList<String>();
	private Navigation navigation;
	
	public void setNavigation(Navigation navigation){ //Assigns value of the Navigation object created in the "Main" class to local variable.
	this.navigation = navigation;	
	}
	
	public void clearHistory() { //Method  to erase history
		editConfigFile("history", "");
	}

	public void clearBookmarks() { //Method to erase history
		editConfigFile("bookmarks", "");
	}
	
	private void copyToList (String key){ //Copies the elements of chosen property to the "favourites" list. 
		favourites.clear();
		for (String s : getValue(key).split("(?=http)")) {
			favourites.add(s);
		}	
	}
	
	public void removeElement(String key, String url) { //Removes a single item within defined property.
		copyToList(key);
		favourites.remove(url);
		editConfigFile(key, favourites.toString());
	}
	
	public void removeByIndex(String key, int index) { //Removes a URL within defined property //In some cases it is more appropriate to search URLs by index within a collection.
		copyToList(key);
		favourites.remove(index);
		editConfigFile(key, favourites.toString());
	}
	
	public void addTo(String key, String url) {//Adds a new element to a property.
		copyToList(key);
		favourites.add(url);
		editConfigFile(key, favourites.toString());
	}

	public List<String> deriveFrom(String key) {//Returns the "favourites" list with all the elements of a defined property.
		copyToList(key);
		return favourites;
		
	}

	private void createConfigFile() {//Creates the browser's configuration file, which stores user's data.
		try {
			output = new FileOutputStream("config.properties");
			// Set the properties value
			config.setProperty("homepage", "http://www.google.co.uk");
			config.setProperty("bookmarks", "");
			config.setProperty("history", "");
			// Save properties to project root folder
			config.store(output, "Ragnarock");
		} catch (IOException io) {
			io.printStackTrace();
			navigation.showError("Could not create configuration file!");
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void editConfigFile(String key, String value) { //Used for operations with the configuration file
		try {
			output = new FileOutputStream("config.properties");
			config.put(key, value.replaceAll("[\\[\\]\\s,]", ""));
			config.store(output, "Ragnarock");
		} catch (Exception e) {
			//e.printStackTrace();
			if (e.getClass().getCanonicalName() == "java.io.FileNotFoundException") {//Creates new configuration file if it does not exist in the system.
				createConfigFile();
				editConfigFile(key, value);
			}
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getValue(String property) { //Derives a specific property from the configuration file. 
		try {
			input = new FileInputStream("config.properties");
			// Load a properties file
			config.load(input);
		} catch (Exception e) {
			//e.printStackTrace();
			if (e.getClass().getCanonicalName() == "java.io.FileNotFoundException") {//Creates new configuration file if it does not exist in the system.
				createConfigFile();
			}
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return config.getProperty(property);
	}
}