package gov.eehc.holding.client.common;

import java.time.format.DateTimeFormatter;

/**
 * @author Mawaziny
 */
public final class Constants {

    public static final int OP_INSERT = 2;
    public static final int OP_UPDATE_AFTER = 4;
    public static final int OP_DELETE = 1;
    public static final int PAGE_SIZE = 100;

    public static final String HOLDING_COMPANY_URL = "http://localhost:8080/changes";
    public static final int SENDING_TRIALS = 3;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private Constants() {
    }
}
