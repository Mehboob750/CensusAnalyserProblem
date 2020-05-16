package censusanalyser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;
import static java.nio.file.Files.newBufferedReader;

public class IndiaCensusAdapter extends CensusAdapter {
    @Override
    public Map<String, CensusDAO> loadCensusData(String... csvFilePath) throws CensusAnalyserException {
        Map<String, CensusDAO> csvFileMap = super.loadCensusData(IndiaCensusCSV.class,csvFilePath[0]);
        this.loadIndianStateCode(csvFileMap, csvFilePath[1]);
        return csvFileMap;
    }

    public int loadIndianStateCode(Map<String, CensusDAO> csvFileMap, String... csvFilePath) throws CensusAnalyserException {
        try (Reader reader = newBufferedReader(Paths.get(String.valueOf(csvFilePath)));){
            ICSVBuilder csvBuilder=CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaStateCodeCSV> stateCSVIterator = csvBuilder.getCSVFileIterator(reader,IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> csvIterable = () -> stateCSVIterator;
            StreamSupport.stream(csvIterable.spliterator(), false)
                    .filter(csvState -> csvFileMap.get(csvState.stateName) != null)
                    .forEach(censusCSV -> csvFileMap.get(censusCSV.stateName).state = censusCSV.stateCode);
            return csvFileMap.size();
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