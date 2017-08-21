package org.bibalex.eol;

import org.gbif.dwca.io.ArchiveFile;
import org.gbif.dwca.record.Record;
import org.gbif.dwca.record.RecordIterator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mina.Edward on 9/7/2016.
 * Mocking class for the ArchiveFile, It read the records from in memory array list instead of reading it from a file
 */
public class MyArchiveFile extends ArchiveFile {

    ArrayList<Record> recordList = new ArrayList<Record>();

    public void addRecord(Record record) {
        this.recordList.add(record);
    }

    @Override
    public Iterator<Record> iterator() {
        return recordList.iterator();
    }
}
