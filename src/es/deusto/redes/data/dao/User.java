package es.deusto.redes.data.dao;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Unique;

@PersistenceCapable(detachable="true")
public class User {
	@Unique
	private String nick = null;
	private String password = null;
	private boolean type = false;
	
	public User(String nick, String password, boolean type) {
		super();
		this.nick = nick;
		this.password = password;
		this.type = type;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
