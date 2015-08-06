package com.example.student.lbs;

import java.io.Serializable;

public class FenceVO implements Serializable {
	private int fenceNo;
	private String fenceName;
	private double fenceLatitude;
	private double fenceLongitude;
	private int fenceRadius;
	private String fenceAddress;
	
	public int getFenceNo() {
		return fenceNo;
	}
	public void setFenceNo(int fenceNo) {
		this.fenceNo = fenceNo;
	}
	public String getFenceName() {
		return fenceName;
	}
	public void setFenceName(String fenceName) {
		this.fenceName = fenceName;
	}
	public double getFenceLatitude() {
		return fenceLatitude;
	}
	public void setFenceLatitude(double fenceLatitude) {
		this.fenceLatitude = fenceLatitude;
	}
	public double getFenceLongitude() {
		return fenceLongitude;
	}
	public void setFenceLongitude(double fenceLongitude) {
		this.fenceLongitude = fenceLongitude;
	}
	public int getFenceRadius() {
		return fenceRadius;
	}
	public void setFenceRadius(int fenceRadius) {
		this.fenceRadius = fenceRadius;
	}
	public String getFenceAddress() {
		return fenceAddress;
	}
	public void setFenceAddress(String fenceAddress) {
		this.fenceAddress = fenceAddress;
	}
	
	
}

