package jogo;

import java.io.IOException;
import java.util.Scanner;

public class Jogo {
	
	private char[][] gerarTabuleiro() {
	    char[][] tabuleiro = new char[8][8];
	    
	    // Fill the board with the desired initial values
	    // For example, filling it with '-' to represent empty spaces
	    
	    for (int i = 0; i < tabuleiro.length; i++) {
	        for (int j = 0; j < tabuleiro[i].length; j++) {
	            tabuleiro[i][j] = '-';
	        }
	    }
	    
	    return tabuleiro;
	}
	
	private char[][] tabuleiro = gerarTabuleiro();
	
	private double jogoInicio;
	private float jogoDuracao;
	
	public Jogo() {
		jogoInicio = System.currentTimeMillis();
	}
	
	public float duracao(double jogoInicio) {
		return (float)((System.currentTimeMillis() - jogoInicio)/1000);
	}

	public float getDuracao() {
		return jogoDuracao;
	}
	
	public boolean vitoria(char simbolo) {
	    // Check for horizontal win
	    for (int row = 0; row < tabuleiro.length; row++) {
	        for (int col = 0; col <= tabuleiro[row].length - 4; col++) {
	            if (tabuleiro[row][col] == simbolo && tabuleiro[row][col + 1] == simbolo &&
	                tabuleiro[row][col + 2] == simbolo && tabuleiro[row][col + 3] == simbolo) {
	            	jogoDuracao = duracao(jogoInicio);
	                return true;
	            }
	        }
	    }

	    // Check for vertical win
	    for (int col = 0; col < tabuleiro[0].length; col++) {
	        for (int row = 0; row <= tabuleiro.length - 4; row++) {
	            if (tabuleiro[row][col] == simbolo && tabuleiro[row + 1][col] == simbolo &&
	                tabuleiro[row + 2][col] == simbolo && tabuleiro[row + 3][col] == simbolo) {
	            	jogoDuracao = duracao(jogoInicio);
	                return true;
	            }
	        }
	    }

	    // Check for diagonal win (top-left to bottom-right)
	    for (int row = 0; row <= tabuleiro.length - 4; row++) {
	        for (int col = 0; col <= tabuleiro[row].length - 4; col++) {
	            if (tabuleiro[row][col] == simbolo && tabuleiro[row + 1][col + 1] == simbolo &&
	                tabuleiro[row + 2][col + 2] == simbolo && tabuleiro[row + 3][col + 3] == simbolo) {
	            	jogoDuracao = duracao(jogoInicio);
	                return true;
	            }
	        }
	    }

	    // Check for diagonal win (top-right to bottom-left)
	    for (int row = 0; row <= tabuleiro.length - 4; row++) {
	        for (int col = 3; col < tabuleiro[row].length; col++) {
	            if (tabuleiro[row][col] == simbolo && tabuleiro[row + 1][col - 1] == simbolo &&
	                tabuleiro[row + 2][col - 2] == simbolo && tabuleiro[row + 3][col - 3] == simbolo) {
	            	jogoDuracao = duracao(jogoInicio);
	                return true;
	            }
	        }
	    }

	    // No winning condition found
	    return false;
	}


	public String JogoToTXT() {
	    StringBuilder out = new StringBuilder();
	    
	    for (int i = 0; i < 8; i++) {
	        for (int j = 0; j < 8; j++) {
	            out.append(tabuleiro[i][j]);
	            if (j < 7)
	                out.append('|');
	        }
	        out.append("\n");
	        if (i < 7)
	            out.append("---------------\n");
	    }
	    
	    return out.toString();
	}

	// deteta se o tabuleiro está preenchido
	public boolean empate() {
	    for (int row = 0; row < tabuleiro.length; row++) {
	        for (int col = 0; col < tabuleiro[row].length; col++) {

	            if (tabuleiro[row][col] == '-') {
	                return false;
	            }
	        }
	    }
	    
	    jogoDuracao = duracao(jogoInicio);
	    return true;
	}


	// mostra o jogo no ecrã
	public void mostrar() {
		System.out.println(JogoToTXT());
	}
	
	public boolean jogar(int coluna, char simbolo) {
		
	    if (coluna == 9)
	        return true; // Invalid column, skip the play

	    for (int linha = 7; linha >= 0; linha--) {
	        if (tabuleiro[linha][coluna] == '-') {
	            tabuleiro[linha][coluna] = simbolo;
	            return true;
	        }
	    }

	    return false;
	}
}
