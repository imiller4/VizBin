package lu.uni.lcsb.vizbin;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * This class is used for processing command line options.
 * 
 * @author Piotr Gawron
 * 
 */
public class CommandLineOptions {
	/**
	 * Default class logger.
	 */
	private final Logger				logger						= Logger.getLogger(CommandLineOptions.class);
	private static final String	THREAD_PARAM			= "t";

	private static final String	K_MER_PARAM				= "k";

	private static final String	CUTOFF_PARAM			= "c";

	private static final String	OUTPUT_FILE_PARAM	= "o";

	private static final String	INPUT_FILE_PARAM	= "i";

	private final static String	HEADER						= "";
	private final static String	FOOTER						= "";
	Options											options;
	CommandLine									cmd;

	boolean											ok;

	/**
	 * Default constructor.
	 * 
	 * @param args
	 *          parameters passed to the program
	 * @throws ParseException
	 *           thrown when there is a problem with parameters
	 */
	public CommandLineOptions(String[] args) throws ParseException {
		options = new Options();
		options.addOption(createOption(false, false, "h", "help", "print this help menu", null));
		options.addOption(createOption(true, true, INPUT_FILE_PARAM, "input", "input file in fasta format", "input-file"));
		options.addOption(createOption(true, true, OUTPUT_FILE_PARAM, "output", "output file containing coordinates", "output-file"));
		options.addOption(createOption(true, false, CUTOFF_PARAM, "cut-off", "minimum conting length [default=" + Config.DEFAULT_CONTIG_LENGTH + "]", "cut-off"));
		options.addOption(createOption(true, false, K_MER_PARAM, "k-mer", "k-mer length [default=" + Config.DEFAULT_KMER_LENGTH + "]", "length"));
		options.addOption(createOption(true, false, THREAD_PARAM, "thread", "number of threads [default=" + Config.DEFAULT_THREAD_NUM + "]", "number"));

		CommandLineParser parser = new BasicParser();
		try {
			cmd = parser.parse(options, args);
			ok = true;
		} catch (MissingOptionException e) {
			ok = false;
		}

	}

	private Option createOption(boolean arg, boolean required, String abbreviation, String name, String description, String paramName) {
		OptionBuilder.hasArg(arg);
		OptionBuilder.isRequired(required);
		OptionBuilder.withDescription(description);
		OptionBuilder.withLongOpt(name);
		if (paramName != null) {
			OptionBuilder.withArgName(paramName);
		} else {
			OptionBuilder.withArgName(null);
		}
		Option option = OptionBuilder.create(abbreviation);
		return option;
	}

	public boolean isValid() {
		return ok;
	}

	public void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		System.out.println("Console usage: ");
		System.out.println("");
		formatter.printHelp("myapp", HEADER, options, FOOTER, true);

	}

	public ProcessParameters getParameters() {
		ProcessParameters params = new ProcessParameters();
		if (cmd.getOptionValue(INPUT_FILE_PARAM) != null) {
			params = params.inputFastaFile(cmd.getOptionValue(INPUT_FILE_PARAM));
		}
		if (cmd.getOptionValue(OUTPUT_FILE_PARAM) != null) {
			params = params.outputFile(cmd.getOptionValue(OUTPUT_FILE_PARAM));
		}
		if (cmd.getOptionValue(CUTOFF_PARAM) != null) {
			try {
				Integer i = Integer.valueOf(cmd.getOptionValue(CUTOFF_PARAM));
				params = params.contigLength(i);
			} catch (NumberFormatException e) {
				logger.warn("Problem with parameter: " + cmd.getOptionValue(CUTOFF_PARAM) + ". Integer value expected. Using default.");
			}
		}
		if (cmd.getOptionValue(K_MER_PARAM) != null) {
			try {
				Integer i = Integer.valueOf(cmd.getOptionValue(K_MER_PARAM));
				params = params.kMerLength(i);
			} catch (NumberFormatException e) {
				logger.warn("Problem with parameter: " + cmd.getOptionValue(K_MER_PARAM) + ". Integer value expected. Using default.");
			}
		}
		if (cmd.getOptionValue(THREAD_PARAM) != null) {
			try {
				Integer i = Integer.valueOf(cmd.getOptionValue(THREAD_PARAM));
				params = params.threads(i);
			} catch (NumberFormatException e) {
				logger.warn("Problem with parameter: " + cmd.getOptionValue(THREAD_PARAM) + ". Integer value expected. Using default.");
			}
		}
		return params;
	}
}