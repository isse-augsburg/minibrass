package isse.mbr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import isse.mbr.parsing.MiniBrassLexer;
import isse.mbr.parsing.MiniBrassParser;

public class MiniBrassTest {

	public static void main(String[] args) throws FileNotFoundException {
		MiniBrassParser parser = new MiniBrassParser();
		parser.parse(new File("test.mbr"));
	}

}
