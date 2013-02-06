package de.codecentric.ddt.resourcestrategies.issuetrackers;

public enum IssueStatus {

        /**
         * The issue is still open
         */
	OPEN {
                @Override
		public String toString() {
			return "Open";
		}
	},
        
        /**
         * The issue has been closed
         */
	CLOSED {
            @Override
		public String toString() {
			return "Closed";
		}
	}
}

