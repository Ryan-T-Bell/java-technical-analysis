package model;

/**
 * Class used to store all project variables
 * 
 * @author Ryan Bell
 */
public class Variables {

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Website Variables
	////////////////////////////////////////////////////////////////////////////////////////////////

	
	public static final String YAHOO = "https://finance.yahoo.com/quote/IVV/history?p=";
	public static final String YAHOO_Prefix = "https://finance.yahoo.com/quote/";
	public static final String YAHOO_Suffix = "/history?period1=958708800&period2=1504411200&interval=1d&filter=history&frequency=1d";
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Directory Variables
	////////////////////////////////////////////////////////////////////////////////////////////////

	
	// Directory in Dropbox that Project is Located
	public static final String MODEL_BUILDER = System.getProperty("user.dir") + "/";

	// Directory on computer where training data is stored (Desktop Training Folder)
	public static final String DESKTOP = System.getProperty("user.home") + "/Desktop/Training/";
	
	// CHANGE TO E:/Training/ If Using Desktop Hard Drive
	public static final String TRAINING =  "E:/Training/"; // DESKTOP;
	

	// Directory in Dropbox of important variables
	public static final String VARIABLES = MODEL_BUILDER + "Variables/";

	// Directory location of ETFDATA in Dropbox
	public static final String ETFDATA = MODEL_BUILDER + "ETFData/";
	public static final String TEMP_DATA = TRAINING + "TempData/";

	// Directory location of Neural Network Layers (In Desktop Training Folder)
	public static final String LEVEL_1 = TRAINING + "Level 1 TechnicalAnalysis/";
	public static final String LEVEL_2 = TRAINING + "Level 2 ProfileSelection/";
	public static final String LEVEL_3 = TRAINING + "Level 3 ThresholdEvaluation/";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// General Multiple-Purpose Variables
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static double mPositionSize = 10000.0;			// Variable used for N/N training position size
	public static double mCommission = 7.95;				// Cost to sell ETF within 30 days of purchase
	public static final int NP = 5;							// Node Period: Length in minutes of each node in TechnicalData list
	public static final int PIM = 77;						// Period Index Manipulator (How many periods are in 1 average trading day)

	// Number of threads running along best performance
	public static final int mWorkingThreads = Runtime.getRuntime().availableProcessors();

	// Trading day starting time (0930 EST)

	public static final int HourStart = 9;
	public static final int MinuteStart = 30;

	// Trading day end time (1600 EST)

	public static final int HourEnd = 16;
	public static final int MinuteEnd = 00;

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// DATA VARIABLES: All Exchange Traded Funds (ETF) Symbols from File
	////////////////////////////////////////////////////////////////////////////////////////////////

	
	public static final String[] SYMBOLS = {
			"EEM","FXE","FXI","GLD","IVV","IYR","SDS","SH","SSO","XLB","XLE",
			"XLF","XLI","XLK","XLP","XLU","XLV","XLY","XME","XOP","XRT"
			};
	
	
	public static final String[] SYMBOLS2 = {
			"DIA","EEM","EFA","ERX","EWZ","FAS","FAZ","FXE","FXI","GDX",
			"GLD","HYG","IVV","IWF","IWM","IWO","IYR","JNK","LQD","MDY",
			"OIH","QLD","QQQ","RSX","SDS","SH","SHY","SLV","SPY","SSO",
			"TLT","TNA","TZA","UPRO","USO","VTI","VWO","VXX","XLB","XLE",
			"XLF","XLI","XLK","XLP","XLU","XLV","XLY","XME","XOP","XRT"
			};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// LEVEL 1: Indicator Score Variables
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	

	public static String[] INDICATORS = {
			"bollingerBand0","bollingerBand1","bollingerBand2","bollingerBand3","bollingerBand4",
			"bollingerBand5","bollingerBand6","bollingerBand7","bollingerBand8","bollingerBand9",
			"bollingerBand10","bollingerBand11","bollingerBand12","candlestick0v0","candlestick0v1"
			,"candlestick0v2","candlestick0v3","candlestick0v4","candlestick0v5","candlestick0v6",
			"candlestick0v7","candlestick0v8","candlestick0v9","candlestick0v10","candlestick0v11",
			"candlestick0v12","candlestick0v13","candlestick0v14","candlestick0v15","candlestick0v16",
			"candlestick0v17","candlestick0v18","candlestick1v0","candlestick1v1","candlestick1v2",
			"candlestick1v3","candlestick1v4","candlestick1v5","candlestick1v6","candlestick1v7",
			"candlestick1v8","candlestick1v9","candlestick1v10","candlestick1v11","candlestick1v12",
			"candlestick1v13","candlestick1v14","candlestick1v15","candlestick1v16","candlestick1v17",
			"candlestick1v18","candlestick2v0","candlestick2v1","candlestick2v2","candlestick2v3",
			"candlestick2v4","candlestick2v5","candlestick2v6","candlestick2v7","candlestick2v8",
			"candlestick2v9","candlestick2v10","candlestick2v11","candlestick2v12","candlestick2v13",
			"candlestick2v14","candlestick2v15","candlestick2v16","candlestick2v17","candlestick2v18",
			"candlestick3v0","candlestick3v1","candlestick3v2","candlestick3v3","candlestick3v4",
			"candlestick3v5","candlestick3v6","candlestick3v7","candlestick3v8","candlestick3v9",
			"candlestick3v10","candlestick3v11","candlestick3v12","candlestick3v13","candlestick3v14",
			"candlestick3v15","candlestick3v16","candlestick3v17","candlestick3v18","candlestick4v0",
			"candlestick4v1","candlestick4v2","candlestick4v3","candlestick4v4","candlestick4v5",
			"candlestick4v6","candlestick4v7","candlestick4v8","candlestick4v9","candlestick4v10",
			"candlestick4v11","candlestick4v12","candlestick4v13","candlestick4v14","candlestick4v15",
			"candlestick4v16","candlestick4v17","candlestick4v18","ema0","ema1","ema2","ema3","ema4",
			"ema5","ema6","ema7","ema8","ema9","ema10","ema11","ema12","emaCross0","emaCross1","emaCross2"
			,"emaCross3","emaCross4","emaCross5","emaCross6","emaCross7","emaCross8","emaCross9","emaCross10"
			,"fr0","fr1","fr2","fr3","fr4","fr5","fr6","fr7","fr8","fr9","fr9","fr10","fr11","ma0","ma1","ma2"
			,"ma3","ma4","ma5","ma6","ma7","ma8","ma9","ma10","ma11","ma12","maCross0","maCross1","maCross2",
			"maCross3","maCross4","maCross5","maCross6","maCross7","maCross8","maCross9","maCross10","macd0",
			"macd1","mcPriceEEM","mcPriceFXE","mcPriceFXI","mcPriceGLD","mcPriceIVV","mcPriceIYR","mcPriceSDS",
			"mcPriceSH","mcPriceSSO","mcPriceXLB","mcPriceXLE","mcPriceXLF","mcPriceXLI","mcPriceXLK","mcPriceXLP",
			"mcPriceXLU","mcPriceXLV","mcPriceXLY","mcPriceXME","mcPriceXOP","mcPriceXRT","mcVolumeEEM",
			"mcVolumeFXE","mcVolumeFXI","mcVolumeGLD","mcVolumeIVV","mcVolumeIYR","mcVolumeSDS","mcVolumeSH",
			"mcVolumeSSO","mcVolumeXLB","mcVolumeXLE","mcVolumeXLF","mcVolumeXLI","mcVolumeXLK","mcVolumeXLP",
			"mcVolumeXLU","mcVolumeXLV","mcVolumeXLY","mcVolumeXME","mcVolumeXOP","mcVolumeXRT","topTrend0","topTrend1","topTrend2",
			"topTrend3","topTrend4","topTrend5","topTrend6","topTrend7","topTrend8","topTrend9","topTrend10","topTrend11",
			"bottomTrend0","bottomTrend1","bottomTrend2","bottomTrend3","bottomTrend4","bottomTrend5","bottomTrend6",
			"bottomTrend7","bottomTrend8","bottomTrend9","bottomTrend10","bottomTrend11","dydx","dydx2","obv0","obv1",
			"obv2","obv3","obv4","obv5","obv6","obv7","obv8","obv9","obv10","obv11","obv12","obv13","obvMA0","obvMA1",
			"obvMA2","obvMA3","obvMA4","obvMA5","obvMA6","obvMA7","obvMA8","obvMA9","obvMA10","obvMA11","obvMA12","obvMA13",
			"rsi0","rsi1","rsi2","rsi3","rsi4","rsi5","rsi6","rsi7","rsi8","rsi9","rsi10","rsi11","rsi12","rsi13","rsiMA0",
			"rsiMA1","rsiMA2","rsiMA3","rsiMA4","rsiMA5","rsiMA6","rsiMA7","rsiMA8","rsiMA9","rsiMA10","rsiMA11","smi0","smi1",
			"smi2","smi3","smi4","smi5","smi6","smi7","smi8","smi9","smi10","smi11","smi12","smi13","volatility0","volatility1",
			"volatility2","volatility3","volatility4","volatility5","volatility6","volatility7","volatility8","volatility9",
			"volatility10","vs0","vs1","vs2","vs3","vs4","vs5","vs6","vs7","vs8","vs9","vs10","vs11"
	};

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// LEVEL 3: Threshold Abstraction Variables
	////////////////////////////////////////////////////////////////////////////////////////////////
	

	public static final double mIterationsDelta = 200.0;									// Number of iterations to test 1 buy/sell threshold over

	public static final double mMinIndicatorRange = 1.0;
	public static final double mMaxIndicatorRange = 100.0;
	
	public static final int mFixedBarMin = 10;												// Minimum Value for Fixed Bar Entry / Exit
	public static final int mFixedBarMax = 500 * PIM;										// Fixed Bar Maximum Value
	public static final int mFixedBarDelta = (int) (mFixedBarMax / mIterationsDelta);		// Fixed Bar Change per Iteration
	
	public static final double mRandomMin = 0.01;											// Minimum Value for Random Entry / Exit
	public static final double mRandomMax = 0.2;											// Highest Random Entry/Exit Value
	public static final double mRandomDelta = mRandomMax / mIterationsDelta;				// Random Threshold Change per Iteration
	
	public static final int mDecimal = 4;
	
	public static String[] THRESHOLD_ABSTRACTIONS = {
			"Entry/Long","Exit/Long",
			//"Entry/Short","Exit/Short"
	};
	
	public static void main(String[] args) {
	}
}
