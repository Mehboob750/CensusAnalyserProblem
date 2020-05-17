package censusanalyser;

import com.google.gson.Gson;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.nio.file.Files.newBufferedReader;

public class CensusAnalyser {
    static List<CensusDAO> censusList = null;
    Map<String, CensusDAO> csvFileMap = null;
    Map<String, USCensusCSV> usCensusCSVMap = null;
    private Country country;

    public CensusAnalyser(Country country) {
        this.country = country;
    }

    public enum Country {INDIA, US}

    public int loadCensusData(String... csvFilePath) throws CensusAnalyserException {
        csvFileMap = new CensusAdapterFactory().loadCensusData(country, csvFilePath);
        return csvFileMap.size();
    }

    private <E> int getCount(Iterator<E> iterator) {
        Iterable<E> csvIterable = () -> iterator;
        int numOfEnteries = (int) StreamSupport.stream(csvIterable.spliterator(), false).count();
        return numOfEnteries;
    }

    public String getStateWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<CensusDAO> censusComparator = Comparator.comparing(census -> census.state);
        return getSortedCensusData(censusComparator);
    }

    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<CensusDAO> censusComparator = Comparator.comparing(census -> census.population);
        return getSortedCensusData(censusComparator);
    }

    public String getPopulationDensityWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<CensusDAO> censusComparator = Comparator.comparing(census -> census.populationDensity);
        return getSortedCensusData(censusComparator);
    }

    public String getAreaWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<CensusDAO> censusComparator = Comparator.comparing(census -> census.areaInSqKm);
        return getSortedCensusData(censusComparator);
    }

    public String getSortedCensusData(Comparator<CensusDAO> censusSort) throws CensusAnalyserException {
        if (csvFileMap == null || csvFileMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.UNABLE_TO_PARSE);
        }
        List sortCensusData = csvFileMap.values().stream().
                sorted(censusSort).
                map(censusDAO -> censusDAO.getCensusDTO(country)).
                collect(Collectors.toList());
        String sortedDataInJsonFormat = new Gson().toJson(sortCensusData);
        return sortedDataInJsonFormat;
    }
}