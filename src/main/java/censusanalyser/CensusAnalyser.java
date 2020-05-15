package censusanalyser;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.StreamSupport;

public class CensusAnalyser {
    static List<CensusDAO> censusList=null;
    Map<String, CensusDAO> csvFileMap = null;
    Map <String, USCensusCSV> usCensusCSVMap = null;
    public CensusAnalyser() {
        this.censusList = new ArrayList<CensusDAO>();
        this.csvFileMap =new HashMap<String, CensusDAO>();
        this.usCensusCSVMap=new HashMap<>();
    }

    public int loadIndiaCensusData(String csvFilePath) throws  CensusAnalyserException {
        try( Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder csvBuilder=CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaCensusCSV> csvFileIterator=csvBuilder.getCSVFileIterator(reader,IndiaCensusCSV.class);
           Iterable<IndiaCensusCSV> csvIterable=()->csvFileIterator;
           StreamSupport.stream(csvIterable.spliterator(),false).
                   forEach(censusList->csvFileMap.put(censusList.state,new CensusDAO(censusList)));
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

    public int loadIndianStateCode(String csvFilePath) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));){
            ICSVBuilder csvBuilder=CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaStateCodeCSV> stateCSVIterator = csvBuilder.getCSVFileIterator(reader,IndiaStateCodeCSV.class);
            return getCount(stateCSVIterator);
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

    public int loadUSCensusData(String usCensusCsvFilePath) throws CensusAnalyserException {
        try( Reader reader = Files.newBufferedReader(Paths.get(usCensusCsvFilePath));) {
            ICSVBuilder csvBuilder=CSVBuilderFactory.createCSVBuilder();
            Iterator<USCensusCSV> csvFileIterator=csvBuilder.getCSVFileIterator(reader,USCensusCSV.class);
            Iterable<USCensusCSV> csvIterable=()->csvFileIterator;
            StreamSupport.stream(csvIterable.spliterator(),false).
                    forEach(censusList->csvFileMap.put(censusList.state,new CensusDAO(censusList)));
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

    private <E>int getCount(Iterator <E> iterator){
        Iterable<E> csvIterable=() -> iterator;
        int numOfEnteries=(int) StreamSupport.stream(csvIterable.spliterator(),false).count();
        return numOfEnteries;
    }

    public String getStateWiseSortedCensusData()throws CensusAnalyserException {
        Comparator<CensusDAO>censusComparator=Comparator.comparing(census->census.state);
        return getSortedCensusData(censusComparator);
    }

    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<CensusDAO>censusComparator=Comparator.comparing(census->census.population);
        return getSortedCensusData(censusComparator);
    }

    public String getPopulationDensityWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<CensusDAO>censusComparator=Comparator.comparing(census->census.densityPerSqKm);
        return getSortedCensusData(censusComparator);
    }

    public String getAreaWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<CensusDAO>censusComparator=Comparator.comparing(census->census.areaInSqKm);
        return getSortedCensusData(censusComparator);
    }

    public String getSortedCensusData(Comparator censusSort) throws CensusAnalyserException {
        if (censusList==null||censusList.size()==0) {
            throw new CensusAnalyserException("No Census Data",CensusAnalyserException.ExceptionType.UNABLE_TO_PARSE);
        }
        sort(censusSort);
        String sortedCensusJson=new Gson().toJson(this.censusList);
        return sortedCensusJson;
    }

    private static void sort(Comparator<CensusDAO> censusComparator) {
        for(int i=0;i<censusList.size()-1;i++){
            for(int j=0;j<censusList.size()-i-1;j++){
                CensusDAO census1=censusList.get(j);
                CensusDAO census2=censusList.get(j+1);
                if (censusComparator.compare(census1,census2)>0) {
                    censusList.set(j,census2);
                    censusList.set(j+1,census1);
                }
            }
        }
    }

}
