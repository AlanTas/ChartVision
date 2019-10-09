package com.taslabs.chartvision;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.taslabs.chartvision.records.ParsedNdefRecord;
import com.taslabs.chartvision.records.TextRecord;

import java.util.ArrayList;
import java.util.List;


public class NdefMessageParser {

        private NdefMessageParser() {
        }

        public static List<ParsedNdefRecord> parse(NdefMessage message) {
            return getRecords(message.getRecords());
        }

        public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
            List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();

            for (final NdefRecord record : records) {
                if (TextRecord.isText(record)) {
                    elements.add(TextRecord.parse(record));
                } else {
                    elements.add(new ParsedNdefRecord() {
                        public String str() {
                            return new String(record.getPayload());
                        }
                    });
                }
            }

            return elements;
        }
}

