package Components;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Created by serch on 11/01/15.
 */
public class Action implements Serializable {
	// Variable that store the SQL instruction to execute
	private String querySQL;
	// Variable that stores the name of place in the form command_table_place
	private String action;

	public Action(String description) throws ActionWrongException {
		this.querySQL = description;
		try {
			this.action = actionName(description);
		} catch(Exception e) {
			throw new ActionWrongException();
		}
	}

	// Returns the name for the place
	public static String actionName(String s) throws Exception {
		StringTokenizer stringTokenizer;
		String sst, ssta, sstc, sstv, field, value;
		s = s.concat(";");
		/*
		if(s.toLowerCase().startsWith("then "))
			s = s.substring(5, s.length().trim())
		*/
		String n1 = s.substring(0,6).trim().toLowerCase();
		String tableName;
		try {
			stringTokenizer = new StringTokenizer(s);
			if (n1.equals("insert")) {
				stringTokenizer.nextToken();
				stringTokenizer.nextToken();
				tableName = stringTokenizer.nextToken().toUpperCase();
			} else if (n1.equals("update")) {
				stringTokenizer.nextToken();
				tableName = stringTokenizer.nextToken().toUpperCase();
				sst = stringTokenizer.nextToken(); // SET
				int posSet = s.toUpperCase().indexOf("SET");
				int posValue = s.toUpperCase().indexOf("VALUE");
				int posWhere = s.toUpperCase().indexOf("WHERE");
				String asigs;
				if (posWhere != -1)
					asigs = s.substring(posValue + 5, posWhere).trim();
					// asigs = s.substring(posSet + 3, posWhere).trim();
				else 
					asigs = s.substring(posValue + 5, s.length()).trim();
				StringTokenizer ste;
				boolean quoteClosed = true;
				String elem = new String();
				for (int i = 0; i < asigs.length(); i++) {
					String caracter = asigs.substring(i, i + 1);
					if (quoteClosed) {
						if (caracter.equalsIgnoreCase(",")) {
							ste = new StringTokenizer(elem, "=");
							field = ste.nextToken().trim();
							value = ste.nextToken().trim();
							elem = new String();
						} else {
							elem = elem.concat(caracter);
						}
					} else {
						elem = elem.concat(caracter);
					}

					if (caracter.equalsIgnoreCase("'")) {
						quoteClosed = (quoteClosed) ? false : true;
					}
				}

				ste = new StringTokenizer(elem, "=");
				field = ste.nextToken().trim();
				value = ste.nextToken().trim();
				
				String resto;
				if (posWhere != -1) {
					resto = s.substring(posWhere, s.length()).trim();
					stringTokenizer = new StringTokenizer(resto);
					sst = stringTokenizer.nextToken(" "); // WHERE
					sst = stringTokenizer.nextToken(";").trim(); //Condition

				} else resto = "";

				tableName = tableName.concat("_").concat(field);
			} else if (n1.equals("delete")) {
				stringTokenizer = new StringTokenizer(s);
				stringTokenizer.nextToken();
				stringTokenizer.nextToken();
				tableName = stringTokenizer.nextToken().toUpperCase();
			} else if (n1.equals("select")) {
				String ss = s.toUpperCase().substring(s.toUpperCase().indexOf("FROM"), s.length());
				stringTokenizer = new StringTokenizer(ss);
				stringTokenizer.nextToken();
				tableName = stringTokenizer.nextToken().toUpperCase();
			} else throw new Exception();

			return n1.concat("_").concat(tableName);
		} catch (Exception e) {
			throw new Exception();
		}
	}

	public String getQuery() {
		return querySQL;
	}

	public String getAction() {
		return action;
	}
}
