package com.sbt.Keep.Data;

import java.util.Date;

public class Expense {
	private double amount;
	private Date date;
	private String id;
	private String item;
	private String paidBy;
	private UploadStatus status;
	private String type;

	public Expense(final double amount) {
		super();

		this.amount = amount;
		date = new Date();
		item = "By android";
		paidBy = "河童";
		status = UploadStatus.Pending;
	}

	public double getAmount() {
		return amount;
	}

	public Date getDate() {
		return date;
	}

	public String getId() {
		return id;
	}

	public String getItem() {
		return item;
	}

	public String getPaidBy() {
		return paidBy;
	}

	public UploadStatus getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	public void setAmount(final double amount) {
		this.amount = amount;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setItem(final String item) {
		this.item = item;
	}

	public void setPaidBy(final String paidBy) {
		this.paidBy = paidBy;
	}

	public void setStatus(final UploadStatus status) {
		this.status = status;
	}

	public void setType(final String type) {
		this.type = type;
	}

}
