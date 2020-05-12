package censusanalyser;

import com.opencsv.bean.CsvBindByName;

public class IndiaStateCodeCSV {
    @CsvBindByName(column = "State Name", required = true)
    public String stateName;
    @CsvBindByName(column = "StateCode", required = true)
    public String StateCode;

    @Override
    public String toString() {
        return "IndiaStateCodeCSV{" +
                "state='" + stateName + '\'' +
                ", StateCode='" + StateCode + '\'' +
                '}';
    }
}
