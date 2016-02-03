package isse.mbr;

import java.io.File;
import java.io.FileNotFoundException;

import isse.mbr.parsing.MiniBrassParser;

public class MiniBrassTest {

	public static void main(String[] args) throws FileNotFoundException {
		MiniBrassParser parser = new MiniBrassParser(); 
		parser.parse(new File("test.mbr"));
	}

}
