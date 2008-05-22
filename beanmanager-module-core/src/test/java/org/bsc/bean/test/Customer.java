package org.bsc.bean.test;

public class Customer {

	private String firstName;
	private String lastName;
	private int id;
	private int accountId;
        private boolean vip = false;

        
        @Override
        public String toString() {
            return new StringBuilder(100)
                    .append("customerId=").append(id)
                    .append(";firstName=").append(firstName)
                    .append(";lastName=").append(lastName)
                    .append(";vip=").append(vip)
                    .toString();
        }

	public final String getFirstName() {
		return firstName;
	}
	public final void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public final String getLastName() {
		return lastName;
	}
	public final void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public final int getCustomerId() {
		return id;
	}
	public final void setCustomerId(int id) {
		this.id = id;
	}

        public final int getAccountId() {
		return accountId;
	}
	public final void setAccountId(int value) {
		this.accountId = value;
	}

        public boolean isVip() {
            return vip;
        }

        public void setVip(boolean vip) {
            this.vip = vip;
        }
	
}
