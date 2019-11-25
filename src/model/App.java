package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import exception.AlreadyExistsException;
import exception.InvalidEmailException;
import exception.InvalidPasswordException;

/**
* <b>Description:</b> The class App in the package model.<br>
* @author VoodLyc & Esarac.
*/

public class App implements FileLoader<User>{//[TEST]
	
	//Constants
	public static final String APP_PATH="dat/Users.twc";
	public static final String ACTUAL_USER_PATH="dat/ActualUser.twc";
	
	//Attributes
	private String name;
	private User rootUser;
	
	//Constructor
	
	/**
	 * <b>Description:</b> Creates a new instance of App.<br>
	 * @param name The app name.
	 */
	
	public App(String name){
		this.name=name;
		loadUsers();
	}
	
	//Add
	
	/**
	 * <b>Description:</b> This method allows adding a client.<br>
	 * @param name The client name.
	 * @param email The client email.
	 * @param password The client password - password must be a minimum of eight (8) characters in length and contain at least one (1) character from two (2) of the following categories: uppercase letter (A-Z) and digit (0-9).
	 * @return True if the client could be added, false in otherwise.
	 */
	
	public void addClient(String name, String email, String password) throws InvalidEmailException, InvalidPasswordException, AlreadyExistsException {
		
		Client user=new Client(name, email, password);
		if(rootUser!=null){
			rootUser.addUser(user);
		}
		else {
			rootUser=user;
		}
		
	}
	
	/**
	 * <b>Description:</b> This method allows adding a owner.<br>
	 * @param name The owner name.
	 * @param email The owner email.
	 * @param password The owner password - password must be a minimum of eight (8) characters in length and contain at least one (1) character from two (2) of the following categories: uppercase letter (A-Z) and digit (0-9).
	 * @return True if the owner could be added, false in otherwise.
	 */
	
	public void addOwner(String name, String email, String password) throws InvalidEmailException, InvalidPasswordException, AlreadyExistsException{
		
		Owner user=new Owner(name, email, password);
		if(rootUser!=null){
			rootUser.addUser(user);
		}
		else {
			rootUser=user;
		}
		
	}
	
	//LogIn
	
	/**
	 * <b>Description:</b> This method allows logging a user.<br>
	 * @param email The user email.
	 * @param password The user password.
	 * @param keepLoged The keepLoged status, if is true, the user will keep logging.
	 * @return The user who logged in.
	 */
	
	public User logIn(String email, String password, boolean keepLoged){
		User actualUser=searchUser(email);
		if(actualUser!=null){
			if(!password.equals(actualUser.decrypt(actualUser.getPassword()))){
				actualUser=null;
			}
			if(keepLoged){
				saveActualUser(actualUser);
			}
		}
		return actualUser;
	}
	
	/**
	 * <b>Description:</b> This method allows loading the user logged from a file.<br>
	 * @return The user logged.
	 */
	
	public User automaticLogIn(){
		User actualUser=load(ACTUAL_USER_PATH);
		return actualUser;
	}
	
	//Search
	
	/**
	 * <b>Description:</b> This method allows searching for user by the email.<br>
	 * @param email The user email.
	 * @return The user if could be found, null in otherwise.
	 */
	
	public User searchUser(String email){
		User user=null;
		if(rootUser!=null){
			user=rootUser.searchUser(email);
		}
		return user;
	}
	
	//Read
	
	/**
	 * <b>Description:</b> This method allows reading a file.<br>
	 * @param path The file path.
	 * @return The file content if the file exists, null in otherwise.
	 * @throws IOException If an I/O error occurs.
	 */
	
	public String read(String path) throws IOException{//[FILE]
		String text="";
		
		File file=new File(path);
		if(file.exists()){
			file.createNewFile();
			FileReader fileReader=new FileReader(file);
			BufferedReader reader=new BufferedReader(fileReader);
			String actualLine;
			while((actualLine=reader.readLine())!=null){
				text+=actualLine+"\n";
			}
			reader.close();
		}
		else{
			text=null;
		}
		
		return text;
	}

	//Load
	
	/**
	 * <b>Description:</b> This method allows loading the user from a serialized file.<br>
	 * <b>Post:</b> The users were loaded.<br>
	 * @return True if the users could be load, false in otherwise.
	 */
	
	public boolean loadUsers() {//[FILE]
		boolean possible=true;
		try{
			FileInputStream file=new FileInputStream(APP_PATH);
			ObjectInputStream creator=new ObjectInputStream(file);
			this.rootUser=(User)creator.readObject();
			creator.close();
		}
		catch (IOException e) {saveUsers();} 
		catch (ClassNotFoundException e) {possible=false;}
		return possible;
	}
	
	/**
	 * <b>Description:</b> This method allows creating a User form a file.<br>
	 * @param path The file path.
	 * @return A user with the attributes given if the file exists and is a valid file, null in otherwise.
	 */
	
	public User load(String path) {//[FILE]
		User actualUser=null;
		try{
			String[] data=read(path).split("\n");
			actualUser=logIn(data[0], searchUser(data[0]).decrypt(data[1]), false);
		}
		catch (IOException | IndexOutOfBoundsException | NullPointerException e){}
		return actualUser;
	}
	
	//Save
	
	/**
	 * <b>Description:</b> This method allows saving the users in a serialized file.<br>
	 * @return True if the users could be saved, false in otherwise.
	 */
	
	public boolean saveUsers() {//[FILE]
		boolean possible=true;
		try {
			File dir=new File("dat//");
			dir.mkdir();
			FileOutputStream file=new FileOutputStream(APP_PATH);
			ObjectOutputStream creator=new ObjectOutputStream(file);
			creator.writeObject(rootUser);
			creator.close();
		}
		catch (IOException e) {possible=false;}
		return possible;
	}
	
	/**
	 * <b>Description:</b> This method allows saving the logged user in a file.<br>
	 * @param actualUser The user logged.
	 * @return True if the user could be saved, false in otherwise.
	 */
	
	public boolean saveActualUser(User actualUser){//[FILE]
		boolean possible=true;
		try {
			File dir=new File("dat//");
			dir.mkdir();
			File actual=new File(ACTUAL_USER_PATH);
			String text=actualUser.getEmail()+"\n"+actualUser.getPassword();
			PrintWriter writer=new PrintWriter(actual);
			writer.append(text);
			writer.close();
		}
		catch (IOException | NullPointerException e) {possible=false;}
		return possible;
	}
	
	//Get
	
	/**
	 * <b>Description:</b> Gets the value of the attribute rootUser.<br>
	 * @return The attribute rootUser.
	 */
	
	public User getRootUser() {
		return rootUser;
	}
	
}