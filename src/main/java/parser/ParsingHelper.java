package parser;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.Math.*;

public class ParsingHelper {

    private static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static Predicate<Row> checkIfValidRow = row ->
            row.getCell(9).getCellType() == CellType.STRING &&
                    row.getCell(36).getCellType() == CellType.NUMERIC &&
                    row.getCell(37).getCellType() == CellType.NUMERIC;

    private static Function<Row, Accident> makeAccident = (row) -> {
        double longitude = row.getCell(36).getNumericCellValue();
        double latitude = row.getCell(37).getNumericCellValue();
        LocalDateTime time = LocalDateTime.parse(row.getCell(9).getStringCellValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        return new Accident(latitude, longitude, time);
    };

    public static Function<String, List<Accident>> accidentParser = (path) -> {
        List<Accident> accidents = new ArrayList<>();
        try {
            FileInputStream excelFile = new FileInputStream(new File(path));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);

            accidents = asStream(datatypeSheet.iterator())
                    .filter(checkIfValidRow)
                    .map(makeAccident)
                    .collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return accidents;
    };
}
