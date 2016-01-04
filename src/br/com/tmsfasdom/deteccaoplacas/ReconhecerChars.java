package br.com.tmsfasdom.deteccaoplacas;


import java.awt.image.BufferedImage;

import org.opencv.core.Mat;


import net.sourceforge.tess4j.ITessAPI.TessPageSegMode;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ReconhecerChars {
	static final String TESSBASE_PATH = "libs/Tesseract/tessdata";
	static final String DEFAULT_DIC = "eng";

	public static void ReconhecerPlaca(PossivelPlaca possivelPlaca) {
		if (!possivelPlaca.getCharMats().isEmpty() && possivelPlaca.getCharMats().size() >= 7) {
			String letra = "";
			String numero = "";			
			for (int i = 0; i < possivelPlaca.getCharMats().size(); i++) {
				Mat m = possivelPlaca.getCharMats().get(i);
				BufferedImage bm = Util.toBufferedImage(m);
				if (i <= 2) {
					{
						Tesseract reconhecerLetra = getTessbaseforText();
						try {
							letra += reconhecerLetra.doOCR(bm);
						} catch (TesseractException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					{
						Tesseract reconhecerNumero = getTessbaseforNumber();
						try {
							numero += reconhecerNumero.doOCR(bm);
						} catch (TesseractException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			possivelPlaca.setPlateChars(letra + numero);//

		}

	}

	static Tesseract getTessbaseforText() {
		final Tesseract baseApi = new Tesseract();
		baseApi.setDatapath(TESSBASE_PATH);
		baseApi.setLanguage(DEFAULT_DIC);
		baseApi.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVXWYZ");
		//baseApi.setTessVariable("tessedit_char_blacklist", "1234567890");
		baseApi.setPageSegMode(TessPageSegMode.PSM_SINGLE_CHAR);
		//baseApi.setTessVariable("load_system_dawg", "false");
		//baseApi.setTessVariable("load_punc_dawg", "false");
		//baseApi.setTessVariable("load_number_dawg", "false");
		//baseApi.setTessVariable("load_unambig_dawg", "false");
		//baseApi.setTessVariable("load_bigram_dawg", "false");
		//baseApi.setTessVariable("load_fixed_length_dawgs", "false");
		//baseApi.setTessVariable("segment_penalty_garbage", "false");
		//baseApi.setTessVariable("segment_penalty_dict_nonword", "false");
		//baseApi.setTessVariable("segment_penalty_dict_frequent_word", "false");
		//baseApi.setTessVariable("segment_penalty_dict_case_ok", "false");
		//baseApi.setTessVariable("segment_penalty_dict_case_bad", "false");

		return baseApi;
	}

	static Tesseract getTessbaseforNumber() {
		final Tesseract baseApi = new Tesseract();
		baseApi.setDatapath(TESSBASE_PATH);
		baseApi.setLanguage(DEFAULT_DIC);
		baseApi.setTessVariable("tessedit_char_whitelist", "1234567890");
		//baseApi.setTessVariable("tessedit_char_blacklist", "ABCDEFGHIJKLMNOPQRSTUVXWYZ");
		baseApi.setPageSegMode(TessPageSegMode.PSM_SINGLE_CHAR);
		//baseApi.setTessVariable("load_system_dawg", "false");
		//baseApi.setTessVariable("load_punc_dawg", "false");
		//baseApi.setTessVariable("load_number_dawg", "false");
		//baseApi.setTessVariable("load_unambig_dawg", "false");
		//baseApi.setTessVariable("load_bigram_dawg", "false");
		//baseApi.setTessVariable("load_fixed_length_dawgs", "false");
		//baseApi.setTessVariable("segment_penalty_garbage", "false");
		//baseApi.setTessVariable("segment_penalty_dict_nonword", "false");
		//baseApi.setTessVariable("segment_penalty_dict_frequent_word", "false");
		//baseApi.setTessVariable("segment_penalty_dict_case_ok", "false");
		//baseApi.setTessVariable("segment_penalty_dict_case_bad", "false");
		return baseApi;

	}
	
	

}
