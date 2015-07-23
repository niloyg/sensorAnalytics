package learning.age.utility;

import java.util.HashMap;

public class Lookup2D {
	public double[][] twoDlookup;
	public HashMap[] hms;

	public Lookup2D(int dimension, int length) {
		this.twoDlookup = new double[dimension][length];
		this.hms = new HashMap[dimension];
	}
}