package de.codecentric.ddt.configuration;

/**
 * FileComparisonResult is the result-enumeration of a comparison between two files.
 * See: FolderHelper, FileHelper
 * @author Gero Niessen
 */
public enum FileComparisonResult {
        /**
         * Both files are exactly equal.
         */
	EQUAL {
		@Override
		public String toString() {
			return "Equal";
		}
	},
        /**
         * Deviations between both files
         */
	DIFFERENT {
		@Override
		public String toString() {
			return "Different";
		}
	},
        /*
         * The reference file could not be found for comparing to the other file
         */
	MISSING_REFERENCE_FILE {
		@Override
		public String toString() {
			return "MISSING REFERENCE FILE";
		}
	},
        /**
         * THe other file could not be found for comparing it to the reference file
         */
	MISSING_OTHER_FILE {
		@Override
		public String toString() {
			return "Missing other file";
		}
	},
        /*
         * Neither the reference file, nor the othr file could be found for comparing them
         */
	MISSING_BOTH_FILES {
		@Override
		public String toString() {
			return "MISSING BOTH FILES";
		}
	},
        /**
         * An unknown exception has occurred
         */
	UNKNOWN {
		@Override
		public String toString() {
			return "UNKNOWN";
		}
	}
}