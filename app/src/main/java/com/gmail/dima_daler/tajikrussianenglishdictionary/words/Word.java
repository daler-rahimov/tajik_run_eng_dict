package com.gmail.dima_daler.tajikrussianenglishdictionary.words;

public class Word {

	private int word_id;
	private String word;
	private String description;
	
	public Word(){
		word_id = 0;
		word = "";
		description = "";
	}
	public Word(int id, String w, String d){
		word_id = id ;
		word = w;
		description = d;
	}
	
	// setters
	public void setWordId(int id){
		word_id =id;
	}
	public void setWord(String w){
		word = w;
	}
	public void setDescription(String d){
		description = d;
	}
	
	// getters
	public int getWordId(){
		return word_id;
	}
	public String getWord(){
		return word;
	}
	public String getDescription(){
		return description;
	}
	
	public String toString(){
		return word;
	}
}
