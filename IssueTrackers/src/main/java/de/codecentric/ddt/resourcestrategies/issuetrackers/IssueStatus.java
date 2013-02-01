package de.codecentric.ddt.resourcestrategies.issuetrackers;

public enum IssueStatus {

	OPEN {
                @Override
		public String toString() {
			return "Open";
		}
	},

	CLOSED {
            @Override
		public String toString() {
			return "Closed";
		}
	}
}

