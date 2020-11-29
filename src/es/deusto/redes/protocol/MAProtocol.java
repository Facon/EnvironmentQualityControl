package es.deusto.redes.protocol;

import java.util.List;

public class MAProtocol {
	public static String getCode(int code, List<String> arg0) {
		String theOutput = null;
		
		switch (code) {
		case 112:
			theOutput = "112 OK Lista de sensores.";
			break;
		case 113:
			theOutput = "113 OK Lista de medidas.";
			break;
		case 114: // FECHA-HORA;COORDENADAS;VALOR
			theOutput = "114 OK " + arg0.get(0) + ";" + arg0.get(1) + ";" + arg0.get(2);
			break;
		case 115:
			theOutput = "115 OK " + arg0.get(0);
			break;
		case 201:
			theOutput = "201 OK Bienvenido " + arg0.get(0);
			break;
		case 202:
			theOutput = "202 OK Bienvenido al sistema.";
			break;
		case 203:
			theOutput = "203 OK Sensor activo.";
			break;
		case 204:
			theOutput = "204 OK Sensor desactivado.";
			break;
		case 205:
			theOutput = "205 OK GPS activado.";
			break;
		case 206:
			theOutput = "206 OK GPS desactivado.";
			break;
		case 207:
			// 207 OK 30564 bytes transmitiendo.\n…[30564 bytes]…
			theOutput = "207 OK " + arg0.get(0) + " bytes transmitiendo.\n";
			break;
		case 208:
			theOutput = "208 OK Adiós.";
			break;
		case 212:
			theOutput = "212 OK Lista finalizada.";
			break;
		case 401:
			theOutput = "401 ERR Falta el nombre de usuario";
			break;
		case 402:
			theOutput = "402 ERR La clave es incorrecta.";
			break;
		case 403:
			theOutput = "403 ERR Falta la clave.";
			break;
		case 414:
			theOutput = "414 ERR Sensor desconocido.";
			break;
		case 415:
			theOutput = "415 ERR Falta parámetro id_sensor.";
			break;
		case 416:
			theOutput = "416 ERR Sensor en OFF.";
			break;
		case 417:
			theOutput = "417 ERR Sensor no existe.";
			break;
		case 418:
			theOutput = "418 ERR Sensor en estado ON.";
			break;
		case 419:
			theOutput = "419 ERR Sensor en estado OFF.";
			break;
		case 420:
			theOutput = "420 ERR GPS en estado ON.";
			break;
		case 421:
			theOutput = "421 ERR GPS en estado OFF.";
			break;
		default:
			theOutput = "999 ERR Comando desconocido.";
			break;
		}

		return theOutput;
	}
}