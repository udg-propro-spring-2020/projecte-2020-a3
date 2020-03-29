/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
/**
 *
 * @author cacer
 */
public class ConsoleGame {
    
    public static void juga(Chess c) throws IOException {
		Position origin = null;
		Position destiny = null;
		int rows = c.rows();
		int cols = c.cols();
		System.out.println("\nESCACS\n");
		//System.out.println(c);
		do{
			System.out.println();
			origin = readMovement("Coordenada origen (ex. a6): ", rows, cols);
			if (origin != null) {
				destiny = readMovement("Coordenada destí  (ex. a6): ", rows, cols);
				if (destiny != null) {
					Pair<Boolean,Position> r = c.checkMovement(origin,destiny);
					if (r.first) {
						c.applyMovement(origin,destiny,r.second);
						//System.out.println(c);
                                                System.out.println(c.showBoard());
					}
					else
						System.out.println("\nMoviment incorrecte!");
				}
			}
		}while (origin != null && destiny != null);
    }

    
    /** @brief Llegeix una coordenada
	@pre Cert
	@post Escriu el text  t  i llegeix cadenes del canal d'entrada, 
	fins trobar "X" o un string de la forma CF, on C és una lletra minúscula de
	l'abecedari que ocupa una posició inferior o igual a nColumnes, 
        i F és un enter entre 1 i nFiles; si s'ha trobat "X" es retorna null, 
        altrament es retorna la Posicio corresponent a CF.
    */
    private static Position readMovement(String t, int rows, int cols) throws IOException {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String c = "abcdefghijklmnopqrstuvwxyz";
		Position p = new Position(0,0);
		boolean valid = false;
		do {
			System.out.print(t);
			String s = br.readLine();
			if (s.equals("X")) {
				p = null;
				valid = true;
			}
			else if (s.length() >= 2) {
				p.col = c.indexOf(s.charAt(0));
				if (p.col != -1 && p.col < cols) { //dintre rang max
					try {
						p.row = Integer.parseInt(s.substring(1)) - 1;
                                                //System.out.println(p.row);
						if (p.row >= 0 && p.row < rows) 
							valid = true;
						else
							System.out.println("Fila fora de rang. Torna-hi...");
                                                System.out.println("Moviment llegit: "+p.toString());
					}
					catch (NumberFormatException e) {
						System.out.println("Format incorrecte. Torna-hi...");
					}
				}
				else
					System.out.println("Columna fora de rang. Torna-hi...");
			}
		} while (!valid);
		return p;
    }
}

