package org.colombiamovil.smartkpi.client.gears;

import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.GearsException;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.user.client.Window;

public class LocalStore {

	private Database db;

	public LocalStore() {
		try {
			db = Factory.getInstance().createDatabase();
			db.open("gears_db_nemesis");
			db.execute("create table if not exists vizdata (a varchar(10), b varchar(10), c varchar(100))");
		} catch (GearsException e) {
			Window.alert(e.toString());
			e.printStackTrace();
		}
		try {
			db.execute("insert into vizdata values (?, ?, ?)", new String[]{"0", "0", ""});
		} catch (DatabaseException e) {
			Window.alert(e.toString());
			e.printStackTrace();
		}
	}

	public void saveVizData() {
		
	}
}
