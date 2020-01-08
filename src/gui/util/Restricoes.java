package gui.util;

import javafx.scene.control.TextField;

public class Restricoes {


	public static void validaTextFieldInteiro(TextField txt) {
		txt.textProperty().addListener((obs, valorVelho, valorNovo) -> {
			if (valorNovo != null && !valorNovo.matches("\\d*")) {
				txt.setText(valorVelho);
			}
		});
	}
	
	public static void validaTextFieldTamanhoMax(TextField txt, int max) {
		txt.textProperty().addListener((obs, valorVelho, valorNovo) -> {
			if (valorNovo != null && valorNovo.length() > max) {
				txt.setText(valorVelho);
			}
		});
	}

	public static void validaTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, valorVelho, valorNovo) -> {
			if (valorNovo != null && !valorNovo.matches("\\d*([\\.]\\d*)?")) {
				txt.setText(valorVelho);
			}
		});
	}

	
}
