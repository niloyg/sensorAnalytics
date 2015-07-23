package learning.age.utility;

import java.util.ArrayList;
import java.util.List;

public class Regression { 

    public static void main(String[] args) { 
        int MAXN = 1000;
        int n = 0;
        double[] x = new double[MAXN];
        double[] y = new double[MAXN];
        double M = 6.2;
        double A = .00043;
        
        for (int i=0;i<MAXN;i++) {
        	x[i] = i;
        	y[i] = M*Math.exp(-A*x[i])*(1.0 - 0.0*Math.random());
        }
        
        double[] LogY = new double[MAXN];
        for (int i=0;i<MAXN;i++){
        	LogY[i] = Math.log(y[i]);
        	if (i/100*100 == i) {
        		System.out.println(String.format("Log(y)=%.4f, y=%.4f, %.3f", LogY[i], y[i], x[i]));
        	}
        }
        
        //System.out.println("LogY.length="+LogY.length);
        double[] results = linearRegression(x, LogY, MAXN);
        System.out.println(
        		"beta1="+String.format("%.5f", results[1])+
        		", beta0= "+String.format("%.5f", results[0])
        );
        System.out.println("Log(M)="+String.format("%.5f", Math.log(M)));
    }
    
    public static double[] linearRegression(double[] x, double[] y, int count) {
    	double[] results = new double[2];
    	
    	//int count = Math.min(x.length, y.length);
    	
        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for (int n=0;n<count;n++) {
            sumx  += x[n];
            sumx2 += x[n] * x[n];
            sumy  += y[n];
        }
    	
        double xbar = sumx / count;
        double ybar = sumy / count;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < count; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
    	
        //System.out.println("xbar="+xbar+", sumy="+sumy+", ybar="+ybar+", xxbar="+xxbar+", yybar="+yybar+", xybar="+xybar);
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;
        
        results[0] = beta0;
        results[1] = beta1;

        return results;
/*        // print results
        System.out.println("y   = " + beta1 + " * x + " + beta0);

        // analyze results
        int df = n - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < n; i++) {
            double fit = beta1*x[i] + beta0;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }
        double R2    = ssr / yybar;
        double svar  = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar/n + xbar*xbar*svar1;
        System.out.println("R^2                 = " + R2);
        System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
        System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
        svar0 = svar * sumx2 / (n * xxbar);
        System.out.println("std error of beta_0 = " + Math.sqrt(svar0));

        System.out.println("SSTO = " + yybar);
        System.out.println("SSE  = " + rss);
        System.out.println("SSR  = " + ssr);   	
*/    	
    }
}