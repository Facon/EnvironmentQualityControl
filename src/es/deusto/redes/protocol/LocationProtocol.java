package es.deusto.redes.protocol;

import java.util.List;

public class LocationProtocol {

	public static String getCode(int code, List<String> arg0) {
		String theOutput = null;
		
		switch (code) {
		case 114: // COORDENADAS
			theOutput = "114 OK " + arg0.get(0);
			break;
		case 201:
			theOutput = "201 OK Bienvenido " + arg0.get(0) + ".";
			break;
		case 202:
			theOutput = "202 OK Bienvenido al sistema.";
			break;
		case 208:
			theOutput = "208 OK Adiós.";
			break;
		case 401:
			theOutput = "401 ERR Falta el nombre de usuario.";
			break;
		case 402:
			theOutput = "402 ERR La clave es incorrecta.";
			break;
		case 417:
			theOutput = "417 ERR Celda desconocida.";
			break;
		case 418:
			theOutput = "418 ERR Falta parámetro cell_id.";
			break;
		default:
			theOutput = "999 ERR Comando desconocido.";
			break;
		}

		return theOutput;
	}
}
