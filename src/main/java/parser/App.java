package parser;

public class App {

    public static void main(String[] args) throws Exception{

        if(args.length != 1)
            System.out.println("Not correct amount of parameters!!!");
        else {
            PostgresWriter manager = new PostgresWriter();
            System.out.println("Connecting with database");
            String path = args[0];
            manager.writeAccidents(path, ParsingHelper.accidentParser);
        }
    }
}



