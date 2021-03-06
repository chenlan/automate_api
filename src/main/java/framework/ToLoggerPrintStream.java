package framework;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
//import org.apache.log4j.Logger;

/**
 * A wrapper class which takes a logger as constructor argument and offers a
 * PrintStream whose flush method writes the written content to the supplied
 * logger (debug level).
 * <p>
 * Usage:<br>
 * initializing in @BeforeClass of the unit test:
 * <p>
 * <pre>
 * ToLoggerPrintStream loggerPrintStream = new ToLoggerPrintStream(myLog);
 * RestAssured.config = RestAssured.config().logConfig(new LogConfig(loggerPrintStream.getPrintStream(), true));
 * </pre>
 * <p>
 * will redirect all log outputs of a ValidatableResponse to the supplied
 * logger:
 * <p>
 * <pre>
 * resp.then().log().all(true);
 * </pre>
 *
 * @author Heri Bender
 * @version 1.0 (28.10.2015)
 */
public class ToLoggerPrintStream {
    /**
     * Logger for this class
     */
    private Logger myLog;
    private PrintStream myPrintStream;

    /**
     * @return printStream
     * @throws UnsupportedEncodingException
     */
    public PrintStream getPrintStream() {
        if (myPrintStream == null) {
            OutputStream output = new OutputStream() {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                @Override
                public void write(int b) throws IOException {

                    baos.write(b);

                }

                /**
                 * @see java.io.OutputStream#flush()
                 */
                @Override
                public void flush() {

                    String log = this.baos.toString().trim();

                    if (!StringUtils.isBlank(log)) {
                        myLog.info(log);
                        baos = new ByteArrayOutputStream();
                    }
                }
            };
            // true: autoflush
            // must be set!
            myPrintStream = new PrintStream(output, true);

        }
        return myPrintStream;
    }

    /**
     * Constructor
     *
     * @param logger
     */
    public ToLoggerPrintStream(Logger logger) {
        super();
        myLog = logger;
    }
}
