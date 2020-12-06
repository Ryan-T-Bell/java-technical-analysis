package level1_IndicatorScores;

import java.util.ArrayList;
import level0_TechnicalData.TechnicalData;
import model.Model;
import model.NeuralNetworkTraining;
import model.Read;
import model.Variables;
import model.Write;

/**
 * Layer 1 of Neural Network: Technical Analysis
 * Input: TechnicalData(open, high, low, close, volume)
 * Output: All indicator scores
 *
 * @author Ryan Bell
 */
public class IndicatorScores extends Thread {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Thread Implementation
	////////////////////////////////////////////////////////////////////////////////////////////////


	private String mSymbol;
	private String mStart;
	private String mEnd;


	/** Constructor Wrapper: Call technical analysis for all symbols */
	public static void level_1_Training(String start, String end) {

		// Create ArrayList<String> containing parsed symbols list
		ArrayList<String[]> symbolsList = NeuralNetworkTraining.parseSymbolsList();

		// Run each thread group in symbols list
		for (String[] threadGroup : symbolsList) runThreadGroup(threadGroup, start, end);

		// Ensure all files write properly (recall if NullPointerException)
		authenticate(start, end);

		// Change range of all symbol-indicator combinations so all indicator results have same max and min
		normalizeIndicators();
	}


	/** Constructor: Creates thread and calculates level 1 values for neural network */
	public IndicatorScores(String symbol, String start, String end) {
		mSymbol = symbol;
		mStart = start;
		mEnd = end;
		this.start();
	}


	/** Code that runs when constructor is called */
	public void run() {

		if (mSymbol != null) {
			// Converts stored text file to usable object
			TechnicalData data = new TechnicalData(mSymbol);
			int startIndex = data.getIndexByTimeStamp(mStart);
			int endIndex = data.getIndexByTimeStamp(mEnd);

			// Run technical analysis on 
			technicalAnalysis(data, mSymbol, startIndex, endIndex, true);
		}
	}


	/** 
	 * Start and run thread for each String symbol in parameter
	 * @param threadGroup Array containing list of symbols that technical analysis will be calculated for in parallel
	 * @param start Start date of technical analysis calculation
	 * @param end Last date of technical analysis calculation
	 */
	public static void runThreadGroup(String[] threadGroup, String start, String end) {


		// 1. Begin threads for all symbols except first in thread group array /////////////////////


		for (int i = 1; i < threadGroup.length; i++)
			new IndicatorScores(threadGroup[i], start, end);


		// 2. Run first symbol in group not as new thread to prevent method from returning until finished. 
		//    This prevents all symbols being analyzed at same time (too many threads running and error out)


		TechnicalData data = new TechnicalData(threadGroup[0]);
		int startIndex = data.getIndexByTimeStamp(start);
		int endIndex = data.getIndexByTimeStamp(end);

		// Run technical analysis on first symbol in list (not as new thread)
		technicalAnalysis(data, threadGroup[0], startIndex, endIndex, true);

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Neural Network Level 1: Technical Analysis
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Wrapper method for indicators package that calls all technical analysis methods and saves each indicator value to .txt file
	 * @param data Technical data to complete technical analysis for
	 * @param start Start date of technical analysis calculation
	 * @param end Last date of technical analysis calculation
	 * @return All technical analysis data in list of arrays
	 */
	private static void technicalAnalysis(TechnicalData data, String symbol, int start, int end, boolean erase) {


		// Determine directory to store technical analysis results in
		String directory = Variables.LEVEL_1 + symbol + "/";


		try {
			System.out.println(symbol + " Level 1 Technical Analysis: BollingerBands");
			bollingerBandIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Bottom Trend Line");
			bottomTrendLineIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Candlestick");
			candlestickIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Derivative Indicators");
			derivativeIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Exponential Moving Averages");
			exponentialMovingAverageScoreIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: EMA Cross");
			exponentialMovingAverageCrossIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Fibonacci Retracement");
			fibonacciRetracementIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Moving Average Score");
			movingAverageScoreIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Moving Average Cross");
			movingAverageCrossIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: MACD");
			movingAverageConvergenceDivergenceIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Price Correlation");
			marketPriceCorrelationIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Volume Correlation");
			marketVolumeCorrelationIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: On Balance Volume");
			onBalanceVolumeIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: On Balance Volume MA");
			onBalanceVolumeMovingAverageIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: RSI");
			relativeStrengthIndexIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: RSI MA");
			relativeStrengthIndexMovingAverageIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Top Trend Line");
			topTrendLineIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Stochastic Momentum Indicator");
			stochasticMomentumIndexIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Volatility");
			volatilityIndicators(directory, data, start, end, erase);
			System.out.println(symbol + " Level 1 Technical Analysis: Volume Strength");
			volumeStrengthIndicators(directory, data, start, end, erase);


			System.out.println("///////////////////////////////////////////////////////////////////////////////////////");
			System.out.println(directory + " Complete");
			Model.printTime();

		} catch (NullPointerException e) {

			// Clear all TechnicalAnalysis indicator scores (full analysis cannot be done)
			for (String indicator : Variables.INDICATORS)
				Write.writeToFile(directory+indicator+".txt", "", true);

			// Error message and return to end execution
			System.out.println(symbol + " Does not contain data set required ");
			return;
		}

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Technical Analysis Indicator Helper Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	private static void bollingerBandIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"bollingerBand0.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 10, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand1.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 50, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand2.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 75, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand3.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 100, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand4.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 150, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand5.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 200, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand6.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 10*Variables.PIM, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand7.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 21*Variables.PIM, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand8.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 50*Variables.PIM, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand9.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 75*Variables.PIM, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand10.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 100*Variables.PIM, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand11.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 150*Variables.PIM, 2), erase);
		Write.doubleArrayList(directory+"bollingerBand12.txt",BollingerBands.bollingerBands(data, startIndex, endIndex, 200*Variables.PIM, 2), erase);

	}


	private static void candlestickIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.double2DimArrayList(directory+"candlestick0.txt",Candlestick.candlestickAnalysis(data, startIndex, endIndex, 1, 2), erase);
		Write.double2DimArrayList(directory+"candlestick1.txt",Candlestick.candlestickAnalysis(data, startIndex, endIndex, Variables.PIM, 2), erase);
		Write.double2DimArrayList(directory+"candlestick2.txt",Candlestick.candlestickAnalysis(data, startIndex, endIndex, 5*Variables.PIM, 2), erase);
		Write.double2DimArrayList(directory+"candlestick3.txt",Candlestick.candlestickAnalysis(data, startIndex, endIndex, 10*Variables.PIM, 2), erase);
		Write.double2DimArrayList(directory+"candlestick4.txt",Candlestick.candlestickAnalysis(data, startIndex, endIndex, 50*Variables.PIM, 2), erase);

	}


	private static void derivativeIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"dydx.txt", Trend.derivative(data, startIndex, endIndex, true), erase);
		Write.doubleArrayList(directory+"dydx2.txt", Trend.derivative(Trend.derivative(data, startIndex, endIndex, true), startIndex, endIndex), erase);

	}


	private static void exponentialMovingAverageCrossIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"emaCross0.txt",EMA.emaCross(data, startIndex, endIndex, 10, 75), erase);
		Write.doubleArrayList(directory+"emaCross1.txt",EMA.emaCross(data, startIndex, endIndex, 10, 50), erase);
		Write.doubleArrayList(directory+"emaCross2.txt",EMA.emaCross(data, startIndex, endIndex, 10, 100), erase);
		Write.doubleArrayList(directory+"emaCross3.txt",EMA.emaCross(data, startIndex, endIndex, 50, 100), erase);
		Write.doubleArrayList(directory+"emaCross4.txt",EMA.emaCross(data, startIndex, endIndex, 50, 200), erase);
		Write.doubleArrayList(directory+"emaCross5.txt",EMA.emaCross(data, startIndex, endIndex, 10*Variables.PIM, 21*Variables.PIM), erase);
		Write.doubleArrayList(directory+"emaCross6.txt",EMA.emaCross(data, startIndex, endIndex, 10*Variables.PIM, 75*Variables.PIM), erase);
		Write.doubleArrayList(directory+"emaCross7.txt",EMA.emaCross(data, startIndex, endIndex, 21*Variables.PIM, 100*Variables.PIM), erase);
		Write.doubleArrayList(directory+"emaCross8.txt",EMA.emaCross(data, startIndex, endIndex, 21*Variables.PIM, 150*Variables.PIM), erase);
		Write.doubleArrayList(directory+"emaCross9.txt",EMA.emaCross(data, startIndex, endIndex, 50*Variables.PIM, 150*Variables.PIM), erase);
		Write.doubleArrayList(directory+"emaCross10.txt",EMA.emaCross(data, startIndex, endIndex, 50*Variables.PIM, 200*Variables.PIM), erase);

	}


	private static void exponentialMovingAverageScoreIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"ema0.txt",EMA.emaScores(data, startIndex, endIndex, 10, 3), erase);
		Write.doubleArrayList(directory+"ema1.txt",EMA.emaScores(data, startIndex, endIndex, 50, 3), erase);
		Write.doubleArrayList(directory+"ema2.txt",EMA.emaScores(data, startIndex, endIndex, 75, 3), erase);
		Write.doubleArrayList(directory+"ema3.txt",EMA.emaScores(data, startIndex, endIndex, 100, 3), erase);
		Write.doubleArrayList(directory+"ema4.txt",EMA.emaScores(data, startIndex, endIndex, 150, 3), erase);
		Write.doubleArrayList(directory+"ema5.txt",EMA.emaScores(data, startIndex, endIndex, 200, 3), erase);
		Write.doubleArrayList(directory+"ema6.txt",EMA.emaScores(data, startIndex, endIndex, 10*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ema7.txt",EMA.emaScores(data, startIndex, endIndex, 21*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ema8.txt",EMA.emaScores(data, startIndex, endIndex, 50*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ema9.txt",EMA.emaScores(data, startIndex, endIndex, 75*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ema10.txt",EMA.emaScores(data, startIndex, endIndex, 100*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ema11.txt",EMA.emaScores(data, startIndex, endIndex, 150*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ema12.txt",EMA.emaScores(data, startIndex, endIndex, 200*Variables.PIM, 3), erase);

	}


	private static void fibonacciRetracementIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"fr0.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 10), erase);
		Write.doubleArrayList(directory+"fr1.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 50), erase);
		Write.doubleArrayList(directory+"fr2.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 75), erase);
		Write.doubleArrayList(directory+"fr3.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 100), erase);
		Write.doubleArrayList(directory+"fr4.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 150), erase);
		Write.doubleArrayList(directory+"fr5.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 200), erase);
		Write.doubleArrayList(directory+"fr6.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 10*Variables.PIM), erase);
		Write.doubleArrayList(directory+"fr7.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 21*Variables.PIM), erase);
		Write.doubleArrayList(directory+"fr8.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 50*Variables.PIM), erase);
		Write.doubleArrayList(directory+"fr9.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 75*Variables.PIM), erase);
		Write.doubleArrayList(directory+"fr9.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 100*Variables.PIM), erase);
		Write.doubleArrayList(directory+"fr10.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 150*Variables.PIM), erase);
		Write.doubleArrayList(directory+"fr11.txt",FR.fibonacciRetracement(data, startIndex, endIndex, 200*Variables.PIM), erase);

	}


	private static void movingAverageCrossIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"maCross0.txt",MA.maCross(data, startIndex, endIndex, 10, 75), erase);
		Write.doubleArrayList(directory+"maCross1.txt",MA.maCross(data, startIndex, endIndex, 10, 50), erase);
		Write.doubleArrayList(directory+"maCross2.txt",MA.maCross(data, startIndex, endIndex, 10, 100), erase);
		Write.doubleArrayList(directory+"maCross3.txt",MA.maCross(data, startIndex, endIndex, 50, 100), erase);
		Write.doubleArrayList(directory+"maCross4.txt",MA.maCross(data, startIndex, endIndex, 50, 200), erase);
		Write.doubleArrayList(directory+"maCross5.txt",MA.maCross(data, startIndex, endIndex, 10*Variables.PIM, 21*Variables.PIM), erase);
		Write.doubleArrayList(directory+"maCross6.txt",MA.maCross(data, startIndex, endIndex, 10*Variables.PIM, 75*Variables.PIM), erase);
		Write.doubleArrayList(directory+"maCross7.txt",MA.maCross(data, startIndex, endIndex, 21*Variables.PIM, 100*Variables.PIM), erase);
		Write.doubleArrayList(directory+"maCross8.txt",MA.maCross(data, startIndex, endIndex, 21*Variables.PIM, 150*Variables.PIM), erase);
		Write.doubleArrayList(directory+"maCross9.txt",MA.maCross(data, startIndex, endIndex, 50*Variables.PIM, 150*Variables.PIM), erase);
		Write.doubleArrayList(directory+"maCross10.txt",MA.maCross(data, startIndex, endIndex, 50*Variables.PIM, 200*Variables.PIM), erase);

	}


	private static void movingAverageScoreIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"ma0.txt",MA.maScores(data, startIndex, endIndex, 10, 3), erase);
		Write.doubleArrayList(directory+"ma1.txt",MA.maScores(data, startIndex, endIndex, 50, 3), erase);
		Write.doubleArrayList(directory+"ma2.txt",MA.maScores(data, startIndex, endIndex, 75, 3), erase);
		Write.doubleArrayList(directory+"ma3.txt",MA.maScores(data, startIndex, endIndex, 100, 3), erase);
		Write.doubleArrayList(directory+"ma4.txt",MA.maScores(data, startIndex, endIndex, 150, 3), erase);
		Write.doubleArrayList(directory+"ma5.txt",MA.maScores(data, startIndex, endIndex, 200, 3), erase);
		Write.doubleArrayList(directory+"ma6.txt",MA.maScores(data, startIndex, endIndex, 10*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ma7.txt",MA.maScores(data, startIndex, endIndex, 21*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ma8.txt",MA.maScores(data, startIndex, endIndex, 50*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ma9.txt",MA.maScores(data, startIndex, endIndex, 75*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ma10.txt",MA.maScores(data, startIndex, endIndex, 100*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ma11.txt",MA.maScores(data, startIndex, endIndex, 110*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"ma12.txt",MA.maScores(data, startIndex, endIndex, 125*Variables.PIM, 3), erase);

	}


	private static void movingAverageConvergenceDivergenceIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		ArrayList<Double> ema12a = EMA.exponentialMovingAverages(data, startIndex, endIndex, 12);
		ArrayList<Double> ema26a = EMA.exponentialMovingAverages(data, startIndex, endIndex, 26);
		Write.doubleArrayList(directory+"macd0.txt",MACD.macdHistogram(data, startIndex, endIndex, ema12a, ema26a), erase);

		ArrayList<Double> ema12b = EMA.exponentialMovingAverages(data, startIndex, endIndex, 12*Variables.PIM);
		ArrayList<Double> ema26b = EMA.exponentialMovingAverages(data, startIndex, endIndex, 26*Variables.PIM);
		Write.doubleArrayList(directory+"macd1.txt",MACD.macdHistogram(data, startIndex, endIndex, ema12b, ema26b), erase);

	}


	private static void marketPriceCorrelationIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		for (String symbol : Variables.SYMBOLS)
			Write.doubleArrayList(directory+"mcPrice" + symbol + ".txt",MarketCorrelation.compareMarketPrices(data, symbol,startIndex, endIndex), erase);

	}


	private static void marketVolumeCorrelationIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		for (String symbol : Variables.SYMBOLS)
			Write.doubleArrayList(directory+"mcVolume" + symbol + ".txt",MarketCorrelation.compareMarketVolumes(data, symbol, startIndex, endIndex), erase);
		
	}


	private static void topTrendLineIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"topTrend0.txt", Trend.trendLines(data, startIndex, endIndex, 1, 10, true), erase);
		Write.doubleArrayList(directory+"topTrend1.txt", Trend.trendLines(data, startIndex, endIndex, 1, 50, true), erase);
		Write.doubleArrayList(directory+"topTrend2.txt", Trend.trendLines(data, startIndex, endIndex, 10, Variables.PIM, true), erase);
		Write.doubleArrayList(directory+"topTrend3.txt", Trend.trendLines(data, startIndex, endIndex, 25, Variables.PIM, true), erase);
		Write.doubleArrayList(directory+"topTrend4.txt", Trend.trendLines(data, startIndex, endIndex, 10, Variables.PIM, true), erase);
		Write.doubleArrayList(directory+"topTrend5.txt", Trend.trendLines(data, startIndex, endIndex, 15, Variables.PIM, true), erase);
		Write.doubleArrayList(directory+"topTrend6.txt", Trend.trendLines(data, startIndex, endIndex, 1, 10*Variables.PIM, true), erase);
		Write.doubleArrayList(directory+"topTrend7.txt", Trend.trendLines(data, startIndex, endIndex, 10, 10*Variables.PIM, true), erase);
		Write.doubleArrayList(directory+"topTrend8.txt", Trend.trendLines(data, startIndex, endIndex, 1, 50*Variables.PIM, true), erase);
		Write.doubleArrayList(directory+"topTrend9.txt", Trend.trendLines(data, startIndex, endIndex, 10, 50*Variables.PIM, true), erase);
		Write.doubleArrayList(directory+"topTrend10.txt", Trend.trendLines(data, startIndex, endIndex, 25, 50*Variables.PIM, true), erase);
		Write.doubleArrayList(directory+"topTrend11.txt", Trend.trendLines(data, startIndex, endIndex, 50, 50*Variables.PIM, true), erase);

	}


	private static void bottomTrendLineIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"bottomTrend0.txt", Trend.trendLines(data, startIndex, endIndex, 1, 10, false), erase);
		Write.doubleArrayList(directory+"bottomTrend1.txt", Trend.trendLines(data, startIndex, endIndex, 1, 50, false), erase);
		Write.doubleArrayList(directory+"bottomTrend2.txt", Trend.trendLines(data, startIndex, endIndex, 10, Variables.PIM, false), erase);
		Write.doubleArrayList(directory+"bottomTrend3.txt", Trend.trendLines(data, startIndex, endIndex, 25, Variables.PIM, false), erase);
		Write.doubleArrayList(directory+"bottomTrend4.txt", Trend.trendLines(data, startIndex, endIndex, 10, Variables.PIM, false), erase);
		Write.doubleArrayList(directory+"bottomTrend5.txt", Trend.trendLines(data, startIndex, endIndex, 15, Variables.PIM, false), erase);
		Write.doubleArrayList(directory+"bottomTrend6.txt", Trend.trendLines(data, startIndex, endIndex, 1, 10*Variables.PIM, false), erase);
		Write.doubleArrayList(directory+"bottomTrend7.txt", Trend.trendLines(data, startIndex, endIndex, 10, 10*Variables.PIM, false), erase);
		Write.doubleArrayList(directory+"bottomTrend8.txt", Trend.trendLines(data, startIndex, endIndex, 1, 50*Variables.PIM, false), erase);
		Write.doubleArrayList(directory+"bottomTrend9.txt", Trend.trendLines(data, startIndex, endIndex, 10, 50*Variables.PIM, false), erase);
		Write.doubleArrayList(directory+"bottomTrend10.txt", Trend.trendLines(data, startIndex, endIndex, 25, 50*Variables.PIM, false), erase);
		Write.doubleArrayList(directory+"bottomTrend11.txt", Trend.trendLines(data, startIndex, endIndex, 50, 50*Variables.PIM, false), erase);

	}


	private static void onBalanceVolumeIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"obv0.txt", OBV.obvScores(data, startIndex, endIndex, 10, 3), erase);
		Write.doubleArrayList(directory+"obv1.txt", OBV.obvScores(data, startIndex, endIndex, 15, 3), erase);
		Write.doubleArrayList(directory+"obv2.txt", OBV.obvScores(data, startIndex, endIndex, 25, 3), erase);
		Write.doubleArrayList(directory+"obv3.txt", OBV.obvScores(data, startIndex, endIndex, 50, 3), erase);
		Write.doubleArrayList(directory+"obv4.txt", OBV.obvScores(data, startIndex, endIndex, 100, 3), erase);
		Write.doubleArrayList(directory+"obv5.txt", OBV.obvScores(data, startIndex, endIndex, 150, 3), erase);
		Write.doubleArrayList(directory+"obv6.txt", OBV.obvScores(data, startIndex, endIndex, 200, 3), erase);
		Write.doubleArrayList(directory+"obv7.txt", OBV.obvScores(data, startIndex, endIndex, 10*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obv8.txt", OBV.obvScores(data, startIndex, endIndex, 15*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obv9.txt", OBV.obvScores(data, startIndex, endIndex, 25*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obv10.txt", OBV.obvScores(data, startIndex, endIndex, 50*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obv11.txt", OBV.obvScores(data, startIndex, endIndex, 100*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obv12.txt", OBV.obvScores(data, startIndex, endIndex, 150*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obv13.txt", OBV.obvScores(data, startIndex, endIndex, 175*Variables.PIM, 3), erase);

	}


	private static void onBalanceVolumeMovingAverageIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"obvMA0.txt", OBV.obvMAScores(data, startIndex, endIndex, 10, 3), erase);
		Write.doubleArrayList(directory+"obvMA1.txt", OBV.obvMAScores(data, startIndex, endIndex, 15, 3), erase);
		Write.doubleArrayList(directory+"obvMA2.txt", OBV.obvMAScores(data, startIndex, endIndex, 25, 3), erase);
		Write.doubleArrayList(directory+"obvMA3.txt", OBV.obvMAScores(data, startIndex, endIndex, 50, 3), erase);
		Write.doubleArrayList(directory+"obvMA4.txt", OBV.obvMAScores(data, startIndex, endIndex, 100, 3), erase);
		Write.doubleArrayList(directory+"obvMA5.txt", OBV.obvMAScores(data, startIndex, endIndex, 150, 3), erase);
		Write.doubleArrayList(directory+"obvMA6.txt", OBV.obvMAScores(data, startIndex, endIndex, 200, 3), erase);
		Write.doubleArrayList(directory+"obvMA7.txt", OBV.obvMAScores(data, startIndex, endIndex, 10*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obvMA8.txt", OBV.obvMAScores(data, startIndex, endIndex, 15*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obvMA9.txt", OBV.obvMAScores(data, startIndex, endIndex, 25*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obvMA10.txt", OBV.obvMAScores(data, startIndex, endIndex, 50*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obvMA11.txt", OBV.obvMAScores(data, startIndex, endIndex, 100*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obvMA12.txt", OBV.obvMAScores(data, startIndex, endIndex, 150*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"obvMA13.txt", OBV.obvMAScores(data, startIndex, endIndex, 175*Variables.PIM, 3), erase);

	}


	private static void relativeStrengthIndexIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"rsi0.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 9), erase);
		Write.doubleArrayList(directory+"rsi1.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 14), erase);
		Write.doubleArrayList(directory+"rsi2.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 25), erase);
		Write.doubleArrayList(directory+"rsi3.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 50), erase);
		Write.doubleArrayList(directory+"rsi4.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 100), erase);
		Write.doubleArrayList(directory+"rsi5.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 150), erase);
		Write.doubleArrayList(directory+"rsi6.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 200), erase);
		Write.doubleArrayList(directory+"rsi7.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 9*Variables.PIM), erase);
		Write.doubleArrayList(directory+"rsi8.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 14*Variables.PIM), erase);
		Write.doubleArrayList(directory+"rsi9.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 25*Variables.PIM), erase);
		Write.doubleArrayList(directory+"rsi10.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 50*Variables.PIM), erase);
		Write.doubleArrayList(directory+"rsi11.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 100*Variables.PIM), erase);
		Write.doubleArrayList(directory+"rsi12.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 150*Variables.PIM), erase);
		Write.doubleArrayList(directory+"rsi13.txt", RSI.relativeStrengthIndex(data, startIndex, endIndex, 175*Variables.PIM), erase);

	}


	private static void relativeStrengthIndexMovingAverageIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"rsiMA0.txt", RSI.rsiMA(data, startIndex, endIndex, 9, 1, 10), erase);
		Write.doubleArrayList(directory+"rsiMA1.txt", RSI.rsiMA(data, startIndex, endIndex, 14, 1, 10), erase);
		Write.doubleArrayList(directory+"rsiMA2.txt", RSI.rsiMA(data, startIndex, endIndex, 25, 1, 10), erase);
		Write.doubleArrayList(directory+"rsiMA3.txt", RSI.rsiMA(data, startIndex, endIndex, 9*Variables.PIM, 1, 10), erase);
		Write.doubleArrayList(directory+"rsiMA4.txt", RSI.rsiMA(data, startIndex, endIndex, 14*Variables.PIM, 1, 10), erase);
		Write.doubleArrayList(directory+"rsiMA5.txt", RSI.rsiMA(data, startIndex, endIndex, 25*Variables.PIM, 1, 10), erase);
		Write.doubleArrayList(directory+"rsiMA6.txt", RSI.rsiMA(data, startIndex, endIndex, 9*Variables.PIM, Variables.PIM, 10), erase);
		Write.doubleArrayList(directory+"rsiMA7.txt", RSI.rsiMA(data, startIndex, endIndex, 14*Variables.PIM, Variables.PIM, 10), erase);
		Write.doubleArrayList(directory+"rsiMA8.txt", RSI.rsiMA(data, startIndex, endIndex, 25*Variables.PIM, Variables.PIM, 10), erase);
		Write.doubleArrayList(directory+"rsiMA9.txt", RSI.rsiMA(data, startIndex, endIndex, 9*Variables.PIM, 10*Variables.PIM, 10), erase);
		Write.doubleArrayList(directory+"rsiMA10.txt", RSI.rsiMA(data, startIndex, endIndex, 14*Variables.PIM, 10*Variables.PIM, 10), erase);
		Write.doubleArrayList(directory+"rsiMA11.txt", RSI.rsiMA(data, startIndex, endIndex, 25*Variables.PIM, 10*Variables.PIM, 10), erase);

	}


	private static void stochasticMomentumIndexIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"smi0.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 9), erase);
		Write.doubleArrayList(directory+"smi1.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 14), erase);
		Write.doubleArrayList(directory+"smi2.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 25), erase);
		Write.doubleArrayList(directory+"smi3.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 50), erase);
		Write.doubleArrayList(directory+"smi4.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 100), erase);
		Write.doubleArrayList(directory+"smi5.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 150), erase);
		Write.doubleArrayList(directory+"smi6.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 200), erase);
		Write.doubleArrayList(directory+"smi7.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 9*Variables.PIM), erase);
		Write.doubleArrayList(directory+"smi8.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 14*Variables.PIM), erase);
		Write.doubleArrayList(directory+"smi9.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 25*Variables.PIM), erase);
		Write.doubleArrayList(directory+"smi10.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 50*Variables.PIM), erase);
		Write.doubleArrayList(directory+"smi11.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 100*Variables.PIM), erase);
		Write.doubleArrayList(directory+"smi12.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 150*Variables.PIM), erase);
		Write.doubleArrayList(directory+"smi13.txt", SMI.stochasticMomentumIndex(data, startIndex, endIndex, 175*Variables.PIM), erase);

	}


	private static void volatilityIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"volatility0.txt", Volatility.volatility(data, startIndex, endIndex, 14), erase);
		Write.doubleArrayList(directory+"volatility1.txt", Volatility.volatility(data, startIndex, endIndex, 25), erase);
		Write.doubleArrayList(directory+"volatility2.txt", Volatility.volatility(data, startIndex, endIndex, 50), erase);
		Write.doubleArrayList(directory+"volatility3.txt", Volatility.volatility(data, startIndex, endIndex, 150), erase);
		Write.doubleArrayList(directory+"volatility4.txt", Volatility.volatility(data, startIndex, endIndex, 200), erase);
		Write.doubleArrayList(directory+"volatility5.txt", Volatility.volatility(data, startIndex, endIndex, 14*Variables.PIM), erase);
		Write.doubleArrayList(directory+"volatility6.txt", Volatility.volatility(data, startIndex, endIndex, 25*Variables.PIM), erase);
		Write.doubleArrayList(directory+"volatility7.txt", Volatility.volatility(data, startIndex, endIndex, 50*Variables.PIM), erase);
		Write.doubleArrayList(directory+"volatility8.txt", Volatility.volatility(data, startIndex, endIndex, 100*Variables.PIM), erase);
		Write.doubleArrayList(directory+"volatility9.txt", Volatility.volatility(data, startIndex, endIndex, 150*Variables.PIM), erase);
		Write.doubleArrayList(directory+"volatility10.txt", Volatility.volatility(data, startIndex, endIndex, 175*Variables.PIM), erase);

	}

	private static void volumeStrengthIndicators(String directory, TechnicalData data, int startIndex, int endIndex, boolean erase) {

		Write.doubleArrayList(directory+"vs0.txt", VS.volStrength(data, startIndex, endIndex, 10, 3), erase);
		Write.doubleArrayList(directory+"vs1.txt", VS.volStrength(data, startIndex, endIndex, 25, 3), erase);
		Write.doubleArrayList(directory+"vs2.txt", VS.volStrength(data, startIndex, endIndex, 50, 3), erase);
		Write.doubleArrayList(directory+"vs3.txt", VS.volStrength(data, startIndex, endIndex, 100, 3), erase);
		Write.doubleArrayList(directory+"vs4.txt", VS.volStrength(data, startIndex, endIndex, 150, 3), erase);
		Write.doubleArrayList(directory+"vs5.txt", VS.volStrength(data, startIndex, endIndex, 200, 3), erase);
		Write.doubleArrayList(directory+"vs6.txt", VS.volStrength(data, startIndex, endIndex, 10*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"vs7.txt", VS.volStrength(data, startIndex, endIndex, 25*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"vs8.txt", VS.volStrength(data, startIndex, endIndex, 50*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"vs9.txt", VS.volStrength(data, startIndex, endIndex, 100*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"vs10.txt", VS.volStrength(data, startIndex, endIndex, 150*Variables.PIM, 3), erase);
		Write.doubleArrayList(directory+"vs11.txt", VS.volStrength(data, startIndex, endIndex, 175*Variables.PIM, 3), erase);

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data Management
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Convert text file to ArrayList<Double> and return all indicator scores
	 * @param symbol Symbol that is being invested in
	 * @param indicator Technical analysis indicator
	 * @return ArrayList of double values from text file
	 */
	public static ArrayList<Double> getIndicatorScores(String symbol, String indicator) {

		String directory = Variables.LEVEL_1 + symbol + "/" + indicator + ".txt";
		return Read.getDoubleArrayList(directory);

	}


	/** Converts indicator score results to 1-100 range */
	public static void normalizeIndicators() {


		for (String symbol : Variables.SYMBOLS) {

			for (String indicator : Variables.INDICATORS) {

				System.out.println("Normalizing: " + symbol + "-" + indicator);
				
				// Get list of current indicator scores for symbol-indicator combination
				ArrayList<Double> indicatorScores = getIndicatorScores(symbol, indicator);

				// ArrayList with converted indicator scores
				ArrayList<Double> newList = new ArrayList<Double>();

				// 1. Get Current and Goal Indicator Score Minimum and Maximum Values //////////////


				double[] range = getIndicatorRange(symbol, indicator);
				double curMin = range[0];
				double curMax = range[1];


				// 2. Convert all indicator scores to new range ////////////////////////////////////


				for (int i = 0; i < indicatorScores.size(); i++) {

					newList.add(
							normalize(indicatorScores.get(i), curMin, curMax)
							);
				}


				// 3. Overwrite New Indicator List to File /////////////////////////////////////////


				Write.doubleArrayList(Variables.LEVEL_1 + symbol + "/" + indicator + ".txt", newList, true);
			} 
		}
	}


	/** Convert current indicator score to normalized value */
	private static double normalize(double value, double curMin, double curMax) {

		double goalMin = Variables.mMinIndicatorRange;
		double goalMax = Variables.mMaxIndicatorRange;

		return (goalMax-goalMin)/(curMax-curMin)*(value-curMin)+goalMin;
	}


	/**
	 * This method is used in the Neural Network Training step (Level 3: Threshold Abstraction)
	 * Method uses this to set the range that buy/sell thresholds are tested under.
	 * @param symbol Symbol that is being invested in
	 * @param indicator Technical analysis indicator
	 * @return Array containing maximum and minimum technical analysis scores in list
	 */
	public static double[] getIndicatorRange(String symbol, String indicator) {


		// Initialize necessary variables
		ArrayList<Double> list = getIndicatorScores(symbol, indicator);

		double max = list.get(0);
		double min = list.get(0);

		for (Double value : list) {
			if (max < value) max = value;
			if (min > value) min = value;
		}

		double[] node = {min, max};
		return node;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Error Checking
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Attempt to read from each symbol-indicator combination and recall technical analysis if error */
	public static void authenticate(String start, String end) {

		for (String symbol : Variables.SYMBOLS) {
			for (String indicator : Variables.INDICATORS)  {

				try {

					ArrayList<Double> list = getIndicatorScores(symbol, indicator);
					
					// Recall Technical Analysis if List Size is 0
					while (list.size() == 0) {
						System.out.println("Authentication Building: " + symbol + "(" + indicator + ")");
						
						TechnicalData data = new TechnicalData(symbol);
						int startIndex = data.getIndexByTimeStamp(start);
						int endIndex = data.getIndexByTimeStamp(end);

						technicalAnalysis(data, symbol, startIndex, endIndex, true);
						list = getIndicatorScores(symbol, indicator);
					}
					
				} catch (Exception e) {

					TechnicalData data = new TechnicalData(symbol);
					int startIndex = data.getIndexByTimeStamp(start);
					int endIndex = data.getIndexByTimeStamp(end);

					technicalAnalysis(data, symbol, startIndex, endIndex, true);

				}	
			}
		}

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {
		level_1_Training("2013","2015");
	}


}
