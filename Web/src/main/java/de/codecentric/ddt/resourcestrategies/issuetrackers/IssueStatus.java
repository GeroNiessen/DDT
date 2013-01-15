package de.codecentric.ddt.resourcestrategies.issuetrackers;

public enum IssueStatus {

	OPEN {
		public String toString() {
			return "Open";
		}
	},

	CLOSED {
		public String toString() {
			return "Closed";
		}
	}
}

