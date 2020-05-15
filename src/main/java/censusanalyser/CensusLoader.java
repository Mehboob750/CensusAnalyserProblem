package censusanalyser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;
import static java.nio.file.Files.newBufferedReader;

public class CensusLoader {
    public <E> Map<String, CensusDAO> loadCensusData(String csvFilePath, Class<E> censusCsvClass) throws CensusAnalyserException {
        try(Reader reader = newBufferedReader(Paths.get(csvFilePath));) {
            Map<String,CensusDAO> csvFileMap = new HashMap<>();
            ICSVBuilder csvBuilder=CSVBuilderFactory.createCSVBuilder();
            Iterator<E> csvFileIterator=csvBuilder.getCSVFileIterator(reader, censusCsvClass);
            Iterable<E> csvIterable=()->csvFileIterator;
            if (censusCsvClass.getName().equals("censusanalyser.IndiaCensusCSV")) {
                StreamSupport.stream(csvIterable.spliterator(), false)
                        .map(IndiaCensusCSV.class::cast)
                        .forEach(censusList -> csvFileMap.put(censusList.state, new CensusDAO(censusList)));
            } else if (censusCsvClass.getName().equals("censusanalyser.USCensusCSV")) {
                StreamSupport.stream(csvIterable.spliterator(), false)
                        .map(USCensusCSV.class::cast)
                        .forEach(censusList -> csvFileMap.put(censusList.state, new CensusDAO(censusList)));
            }
            return csvFileMap;
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),e.type.name());
        } catch (RuntimeException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.INCORRECT_INPUT_EXCEPTION);
        }
    }
}
