package exams;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StockExams {

	public static BigDecimal calculateDividendYield(Stock stock) throws Exception {
		BigDecimal commonDividendYield = BigDecimal.ZERO;
		BigDecimal preferredDividendYield = BigDecimal.ZERO;
		try {
			commonDividendYield = stock.getLastDivident().divide(
					stock.getTickerPrice(), 4, RoundingMode.CEILING);
			preferredDividendYield = stock.getFixedDivident()
					.multiply(stock.getParValue())
					.divide(stock.getTickerPrice(), 4, RoundingMode.CEILING);
		} catch (Exception exception) {
			throw new Exception(
					"Error calculating the dividend yield for stock "
							+ stock.getSymbol());
		}
		return stock.getType() == StockType.PREFERRED ? preferredDividendYield
				: commonDividendYield;
	}

	public static BigDecimal calculatePERatio(Stock stock) throws Exception {
		BigDecimal peRatio = BigDecimal.ZERO;
		try {
			BigDecimal dy = calculateDividendYield(stock);
			if (dy.compareTo(BigDecimal.ZERO) <= 0) {
				throw new Exception("Can not calculate dividend for stock "
						+ stock.getSymbol());
			}

			peRatio = stock.getTickerPrice()
					.divide(dy, 4, RoundingMode.CEILING);
		} catch (Exception exception) {
			throw new Exception("Error calculating the P/E Ratio for stock "
					+ stock.getSymbol());
		}
		return peRatio;
	}

	public static void recordTrade(int quantity, Indicator indicator,
			BigDecimal price, Stock stock) throws Exception {
		try {
			// Meaning some way to get session (e.g hibernate)
			// Session session =
			// getHibernateTemplate().getSessionFactory().getSession();
			Session session = new Session();
			Trade trade = new Trade();
			trade.setQuantity(quantity);
			trade.setIndicator(indicator);
			trade.setPrice(price);
			trade.setStock(stock.getSymbol());
			session.save(trade);
		} catch (Exception e) {
			throw new Exception("cannot save trade ");
		}

	}

	public static BigDecimal calculateStockPrice(Stock stock) throws Exception {
		// use database to get all trades for the specific stock for the last 15 minutes
		List<StockExams.Trade> trades = getDummyTrades();
		
		BigDecimal calculatedPrice = BigDecimal.ZERO;
		int sumOfQuantities = 0;
		try {
			for (Trade trade : trades) {
				calculatedPrice = calculatedPrice.add(trade.getPrice()
						.multiply(new BigDecimal(trade.getQuantity())));
				sumOfQuantities += trade.getQuantity();
			}
		} catch (Exception e) {
			throw new Exception("Cannot calculate stock price for stock "
					+ stock.getSymbol());
		}

		return calculatedPrice.divide(new BigDecimal(sumOfQuantities), 4,
				RoundingMode.CEILING);
	}

	public static BigDecimal calculateGBSEAllShareIndex() {
		// use database to get all prices
		List<Double> prices = getDummyPrices();
		Double multOfPrices = 1d;
		for (Double price : prices) {
			multOfPrices = multOfPrices * price;
		}
		return BigDecimal.valueOf(Math.pow(multOfPrices,
				BigDecimal.ONE.divide(new BigDecimal(prices.size())).doubleValue()));
	}
	
	
	

	private static class Stock {
		
		public static Stock getInstance() {
			return new Stock();			
		}

		public BigDecimal getLastDivident() {
			// Return dummy lastDivident
			return new BigDecimal(new Random().nextDouble());
		}

		public BigDecimal getTickerPrice() {
			// Return dummy tickerPrice
			return new BigDecimal(new Random().nextDouble());
		}

		public BigDecimal getFixedDivident() {
			// Return dummy fixedDivident
			return new BigDecimal(new Random().nextDouble());
		}

		public BigDecimal getParValue() {
			// Return dummy parValue
			return new BigDecimal(new Random().nextDouble());
		}

		public String getSymbol() {
			// Return dummy symbol
			return "dummy";
		}

		public StockType getType() {
			// Return dummy type
			return new Random().nextBoolean() ? StockType.PREFERRED
					: StockType.COMMON;
		}

	}

	private static class Trade {
		
		public static Trade getInstance() {
			return new Trade();			
		}

		public void setQuantity(int quantity) {
		}

		public BigDecimal getPrice() {
			// Return dummy price
			return new BigDecimal(new Random().nextDouble());
		}

		public int getQuantity() {
			// Return dummy parValue
			return new Random().nextInt();
		}

		public void setStock(String symbol) {
		}

		public void setPrice(BigDecimal price) {
		}

		public void setIndicator(Indicator indicator) {
		}

	}

	private enum StockType {
		PREFERRED, COMMON;
	}

	private enum Indicator {
		BUY, SELL;
	}

	private static class Session {
		
		public void save(Trade trade) {
			// Save in Database
		}

	}
	
	private static Stock getStock() {
		return Stock.getInstance();
	}
	
	private static List<Trade> getDummyTrades() {
		List<StockExams.Trade> trades = new ArrayList<StockExams.Trade>();
		Trade t = new Trade();
		t.setIndicator(new Random().nextBoolean() ? Indicator.BUY
				: Indicator.SELL);
		t.setPrice(new BigDecimal(new Random().nextDouble()));
		t.setQuantity(new Random().nextInt());
		t.setStock("dummy");
		trades.add(t);		
		return trades;
	}
	
	private static List<Double> getDummyPrices() {
		List<Double> prices = new ArrayList<Double>();
		for(int i=0;i<100;i++) {
			prices.add(new Double(new Random().nextInt(100)) + 1d);
		}		
		return prices;
	}
	
	
	public static void main(String[] args) {
		if(args==null || args[0]==null) {
			return;
		}
		if(args[0].equals("1")) {
			try {
				System.out.println("The calculated Divident Yield is "+calculateDividendYield(getStock())); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("2")) {
			try {
				System.out.println("The calculated P/E Ratio is "+calculatePERatio(getStock())); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("3")) {
			try {
				recordTrade(new Random().nextInt(), new Random().nextBoolean()?Indicator.BUY:Indicator.SELL, new BigDecimal(new Random().nextDouble()), getStock());
				System.out.println("Record saved successfully!!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("4")) {
			try {
				System.out.println("The calculated Stock price is "+calculateStockPrice(getStock())); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("5")) {
			try {
				System.out.println("The calculated GBSE All Share Index is "+calculateGBSEAllShareIndex()); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}

}
