package parser;

public class App {

    public static void main(String[] args) {
        TerminalOptions options = new TerminalOptions(args);
        String path = options.getFlagValue("path");

        PostgresWriter manager = new PostgresWriter();
        System.out.println("Connecting with database");
        manager.writeAccidents(path, ParsingHelper.accidentParser);
    }
}



