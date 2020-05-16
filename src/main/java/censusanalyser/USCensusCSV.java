package censusanalyser;

import com.opencsv.bean.CsvBindByName;

public class USCensusCSV {

        @CsvBindByName(column = "State", required = true)
        public String state;

        @CsvBindByName(column = "State Id", required = true)
        public String stateId;

        @CsvBindByName(column = "Population", required = true)
        public int population;

        @CsvBindByName(column = "Housing units", required = true)
        public int housingUnits;

        @CsvBindByName(column = "Total area", required = true)
        public double totalArea;

        @CsvBindByName(column = "Water area", required = true)
        public double waterArea;

        @CsvBindByName(column = "Land area", required = true)
        public double landArea;

        @CsvBindByName(column = "Population Density", required = true)
        public double populationDensity;

        @CsvBindByName(column = "Housing Density", required = true)
        public double housingDensity;

        public USCensusCSV() { }

        public USCensusCSV(String state, int population, double populationDensity, double totalArea) {
                this.state=state;
                this.population=population;
                this.populationDensity=populationDensity;
                this.totalArea=totalArea;
        }

        @Override
        public String toString() {
                return "CSVUSCensus{" +
                        "State='" + state + '\'' +
                        ", State Id='" + stateId + '\'' +
                        ", Population='" + population + '\'' +
                        ", Housing units='" + housingUnits + '\'' +
                        ", Total area='" + totalArea + '\'' +
                        ", Water area='" + waterArea + '\'' +
                        ", Land area='" + landArea + '\'' +
                        ", Population Density='" + populationDensity + '\'' +
                        ", Housing Density='" + housingDensity + '\'' +
                        '}';
        }
}