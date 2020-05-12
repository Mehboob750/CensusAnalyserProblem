package censusanalyser;

import java.io.Reader;
import java.util.Iterator;

public interface ICSVBuilder <E> {
    public <E> Iterator<E> getCSVFileIterator(Reader reader, Class csvClass) throws CensusAnalyserException;
}
