package censusanalyser;

import com.google.gson.Gson;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class CensusAnalyser {
    static List<IndiaCensusDAO> censusList=null;

    public CensusAnalyser() {
        this.censusList = new ArrayList<IndiaCensusDAO>();
    }

    public int loadIndiaCensusData(String csvFilePath) throws  CensusAnalyserException {
        try( Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder csvBuilder=CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaCensusCSV> csvFileIterator=csvBuilder.getCSVFileIterator(reader,IndiaCensusCSV.class);
            while(csvFileIterator.hasNext()){
               this.censusList.add( new IndiaCensusDAO(csvFileIterator.next()));
            }
            return censusList.size();
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

    private <E>int getCount(Iterator <E> iterator){
        Iterable<E> csvIterable=() -> iterator;
        int numOfEnteries=(int) StreamSupport.stream(csvIterable.spliterator(),false).count();
        return numOfEnteries;
    }

    public String getStateWiseSortedCensusData()throws CensusAnalyserException {
        Comparator<IndiaCensusDAO>censusComparator=Comparator.comparing(census->census.state);
        return getSortedCensusData(censusComparator);
    }

    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<IndiaCensusDAO>censusComparator=Comparator.comparing(census->census.population);
        return getSortedCensusData(censusComparator);
    }

    public String getPopulationDensityWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<IndiaCensusDAO>censusComparator=Comparator.comparing(census->census.densityPerSqKm);
        return getSortedCensusData(censusComparator);
    }

    public String getAreaWiseSortedCensusData() throws CensusAnalyserException {
        Comparator<IndiaCensusDAO>censusComparator=Comparator.comparing(census->census.areaInSqKm);
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

    private static void sort(Comparator<IndiaCensusDAO> censusComparator) {
        for(int i=0;i<censusList.size()-1;i++){
            for(int j=0;j<censusList.size()-i-1;j++){
                IndiaCensusDAO census1=censusList.get(j);
                IndiaCensusDAO census2=censusList.get(j+1);
                if (censusComparator.compare(census1,census2)>0) {
                    censusList.set(j,census2);
                    censusList.set(j+1,census1);
                }
            }
        }
    }
}
