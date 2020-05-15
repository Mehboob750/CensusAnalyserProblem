package censusanalyser;

public class CensusDAO {
    public String state;
    public int population;
    public int densityPerSqKm;
    public int areaInSqKm;


    public double totalArea;
    public double populationDensity;
    public String stateId;


    public CensusDAO(IndiaCensusCSV indiaCensusCSV) {
        state = indiaCensusCSV.state;
        areaInSqKm = indiaCensusCSV.areaInSqKm;
        densityPerSqKm = indiaCensusCSV.densityPerSqKm;
        population = indiaCensusCSV.population;
    }

    public CensusDAO(USCensusCSV USCensus) {
        state = USCensus.state;
        population = USCensus.population;
        totalArea = USCensus.totalArea;
        populationDensity = USCensus.populationDensity;
        stateId = USCensus.stateId;
    }

}
