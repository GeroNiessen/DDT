package de.codecentric.ddt.web.applicationchecks;

public enum FileComparisonResult {
	EQUAL {
		@Override
		public String toString() {
			return "Equal";
		}
	},
	DIFFERENT {
		@Override
		public String toString() {
			return "Different";
		}
	},
	MISSING_REFERENCE_FILE {
		@Override
		public String toString() {
			return "MISSING REFERENCE FILE";
		}
	},
	MISSING_OTHER_FILE {
		@Override
		public String toString() {
			return "MISSING OTHER FILE";
		}
	},
	MISSING_BOTH_FILES {
		@Override
		public String toString() {
			return "MISSING BOTH FILES";
		}
	},
	UNKNOWN {
		@Override
		public String toString() {
			return "UNKNOWN";
		}
	}
}