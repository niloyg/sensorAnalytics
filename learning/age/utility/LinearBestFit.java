package learning.age.utility;

public class LinearBestFit {
	double[] x;
	double[] y;
	double c;
	double m;
	
	public LinearBestFit (double[] x, double[] y) throws Exception {
		if (x.length != y.length) throw new Exception("Array length mismatch between x-data & y-data");
		
		this.x = x;
		this.y = y;
		double[] cm = Regression.linearRegression(x, y, x.length);
		this.c = cm[0];
		this.m = cm[1];
		System.out.println("cm combo is: "+ this.c+" "+this.m);
	}
	
	public double getXwhileGivenY(double y) {
		double result = (y - this.c)/this.m;
		return result;
	}
	
	public static void main(String[] args) {
		double[] x = new double[10];
		double[] y = new double[10];
		for (int i=0;i<10;i++){
			x[i] = i;
			y[i] = 3*(1-0.4*Math.random())*x[i] + 5*(1-0.2*Math.random());
		}
		LinearBestFit lbf = null;
		try {
			lbf = new LinearBestFit(x, y);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("m & c are "+lbf.m+", "+lbf.c);
		for (int i=10;i<20;i++) {
			System.out.println("x="+String.format("%.2f", lbf.getXwhileGivenY(i)) +" for y="+i);
		}
	}
}