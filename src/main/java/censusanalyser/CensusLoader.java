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

    public Map<String, CensusDAO> loadCensusData(CensusAnalyser.Country country,String... csvFilePath) throws CensusAnalyserException {
        if(country.equals(CensusAnalyser.Country.INDIA))
            return this.loadCensusData(IndiaCensusCSV.class,csvFilePath);
        else if (country.equals(CensusAnalyser.Country.US))
            return this.loadCensusData(USCensusCSV.class,csvFilePath);
        else throw new CensusAnalyserException(CensusAnalyserException.ExceptionType.INVALID_COUNTRY,"Invalid Country");
    }

    private <E> Map<String, CensusDAO> loadCensusData(Class<E> censusCsvClass, String... csvFilePath) throws CensusAnalyserException {
        try(Reader reader = newBufferedReader(Paths.get(csvFilePath[0]));) {
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
            if(csvFilePath.length==1)
                return csvFileMap;
            this.loadIndianStateCode(csvFileMap, csvFilePath[1]);
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
