package com.example.student.stock;

public class StockData {
	private String code;
	private String name;
	private int lastTrade;
	private int start;
	private int max;
	private int min;
	private int price;
	private int volumn;
	private int totalVolumn;
	private int change;// 상승하락폭
	private double changeRate;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLastTrade() {
		return lastTrade;
	}

	public void setLastTrade(int lastTrade) {
		this.lastTrade = lastTrade;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		if (price > max) {
			max = price;
		}
		if (price < min) {
			min = price;
		}
		change = price - lastTrade;
		if (lastTrade != 0)
			changeRate = Double.parseDouble(String.format("%.2f",
					((double) change / (double) lastTrade) * 100));

		this.price = price;
	}

	public int getVolumn() {
		return volumn;
	}

	public void setVolumn(int volumn) {
		totalVolumn += volumn;
		this.volumn = volumn;
	}

	public int getChange() {
		return change;
	}

	public double getChangeRate() {
		return changeRate;
	}

	public int getTotalVolumn() {
		return totalVolumn;
	}

	public void setTotalVolumn(int totalVolumn) {
		this.totalVolumn = totalVolumn;
	}

}
