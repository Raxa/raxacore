package org.bahmni.address.sanitiser;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bahmni.datamigration.AllLookupValues;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static org.apache.commons.lang.StringEscapeUtils.escapeCsv;

@Service
public class AddressSanitiser {

    private final LavensteinsDistance lavensteinsDistance;
    private final AddressHierarchy hierarchy;
    private String csvInputLocation = "/Users/arathyja/Bhamni/csv";
    private int sanitisationDistance = 2;

    public AddressSanitiser(LavensteinsDistance lavensteinsDistance, AddressHierarchy hierarchy) {
        this.lavensteinsDistance = lavensteinsDistance;
        this.hierarchy = hierarchy;
    }

    public SanitizerPersonAddress sanitiseByVillageAndTehsil(SanitizerPersonAddress personAddress) {
        LavensteinMatch<String> closestMatch = lavensteinsDistance.getClosestMatch(personAddress.getVillage(), hierarchy.getAllVillages());
        String closestMatchVillage = closestMatch.matchValue();
        List<SanitizerPersonAddress> addresses = hierarchy.getAllAddressWithVillageName(closestMatchVillage);

        if (addresses.size() > 1) {
            return lavensteinsDistance.getClosestMatch(personAddress.getTehsil(), addresses, AddressField.TEHSIL);
        }
        return addresses.get(0);
    }

    public LavensteinMatch<SanitizerPersonAddress> sanitiseByTehsil(String tehsil, List<String> allTehsils, List<String[]> rows) throws IOException {
        LavensteinMatch<String> closestMatch = lavensteinsDistance.getClosestMatch(tehsil, allTehsils);
        String closestMatchTehsil = closestMatch.matchValue();
        SanitizerPersonAddress addresses = getAllAddressWithTehsilName(closestMatchTehsil, rows);
        return new LavensteinMatch<SanitizerPersonAddress>(addresses, closestMatch.getDistance());
    }

    private SanitizerPersonAddress getAllAddressWithTehsilName(String closestMatchTehsil, List<String[]> rows) throws IOException {
            for(String[] row: rows){
                if(row[2].equals(closestMatchTehsil)){
                    return new SanitizerPersonAddress("",row[2],row[1],row[0]);
                }
            }
            return null;
    }

    public void sanitiseAddressesInFile() throws IOException {
        List<String[]> tehsilToDistrictMapRows = readCsv(new File(csvInputLocation, "Tehsil2District.csv"), true);
        List<String[]> registrationMasterRows = readCsv(new File(csvInputLocation, "RegistrationMaster.csv"), true);
        AllLookupValues districtLookup = new AllLookupValues(csvInputLocation, "LU_District.csv");
        AllLookupValues stateLookup = new AllLookupValues(csvInputLocation, "LU_State.csv");
        HashMap<String, LavensteinMatch<SanitizerPersonAddress>> unSanitisedTehsilMatchMap = new HashMap<String, LavensteinMatch<SanitizerPersonAddress>>();

        List<String> allTehsils = new ArrayList<String>();
        for (String[] row: tehsilToDistrictMapRows){
            allTehsils.add(row[2]);
        }

        List<SanitizerPersonAddress> sanitisedTehsilsAddresses = new ArrayList<SanitizerPersonAddress>();
        for (String[] row : registrationMasterRows) {
            String tehsil = WordUtils.capitalizeFully(row[35]);
            String district = WordUtils.capitalizeFully(districtLookup.getLookUpValue(row[26], 2));
            String village = WordUtils.capitalizeFully(row[10]);
            String stateId = districtLookup.getLookUpValue(row[26],0);
            String state = stateId !=null ? WordUtils.capitalizeFully(stateLookup.getLookUpValue(stateId)) : "";
            SanitizerPersonAddress oldAddress = new SanitizerPersonAddress(village, tehsil == null ? "" : tehsil, district == null ? "": district, state);
            SanitizerPersonAddress sanitisedAddress;
            if(StringUtils.isBlank(tehsil)){
                sanitisedAddress = oldAddress;
            } else {
                LavensteinMatch<SanitizerPersonAddress> lavensteinMatch = sanitiseByTehsil(tehsil, allTehsils, tehsilToDistrictMapRows);
                SanitizerPersonAddress matchedTehsilAddress = lavensteinMatch.matchValue();
                if (lavensteinMatch.getDistance() <= sanitisationDistance){
                    sanitisedAddress = new SanitizerPersonAddress(village, matchedTehsilAddress.getTehsil(), matchedTehsilAddress.getDistrict(), matchedTehsilAddress.getState());
                } else {
                    unSanitisedTehsilMatchMap.put(tehsil, lavensteinMatch);
                    sanitisedAddress = oldAddress;
                }
            }
            if(!sanitisedAddress.isEmpty()){
                sanitisedTehsilsAddresses.add(sanitisedAddress);
            }
        }
        File csvOutputLocation = new File(csvInputLocation, "output");
        csvOutputLocation.mkdirs();
        writeSanitisedTehsilsCsv(csvOutputLocation, sanitisedTehsilsAddresses);
        writeUnSanitiseTehsiDistanceCsv(csvOutputLocation, unSanitisedTehsilMatchMap);
    }

    private void writeSanitisedTehsilsCsv(File csvLocation, List<SanitizerPersonAddress> sanitisedTehsilsAddresses) throws IOException {
        FileWriter writer = null;
        Set<SanitizerPersonAddress> distinctTehsilsAddresses = new HashSet<SanitizerPersonAddress>(sanitisedTehsilsAddresses);
        try {
            writer = new FileWriter(new File(csvLocation, String.format("SanitisedAddressEntriesForDistance-%d.csv", sanitisationDistance)));
            writer.append("State,District,Tehsil,village,\n");
            for (SanitizerPersonAddress address : distinctTehsilsAddresses) {
                writer.append(String.format("%s,%s,%s,%s,\n", escapeCsv(address.getState()), escapeCsv(address.getDistrict()), escapeCsv(address.getTehsil()) ,escapeCsv(address.getVillage())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }

    private void writeUnSanitiseTehsiDistanceCsv(File csvLocation, HashMap<String, LavensteinMatch<SanitizerPersonAddress>> tehsilMatchMap) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(csvLocation, String.format("UnSanitisedTehsilsForDistance-%d.csv", sanitisationDistance)));
            writer.append("oldTehsil,newTehsil,distance,\n");
            for (Map.Entry<String, LavensteinMatch<SanitizerPersonAddress>> entry  : tehsilMatchMap.entrySet()) {
                LavensteinMatch<SanitizerPersonAddress> lavensteinMatch = entry.getValue();
                writer.append(String.format("%s,%s,%s,\n", escapeCsv(entry.getKey()), escapeCsv(lavensteinMatch.matchValue().getTehsil()), escapeCsv(String.valueOf(lavensteinMatch.getDistance()))));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }

    private List<String[]> readCsv(File file, boolean ignoreHeader) throws IOException {
        if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(file), ',','"', '\0');
            if(ignoreHeader) csvReader.readNext();
            return csvReader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(csvReader != null) csvReader.close();
        }
    }

    public static void main(String[] args) throws IOException {
        AddressSanitiser addressSanitiser = new AddressSanitiser(new LavensteinsDistance(),null);
        addressSanitiser.sanitiseAddressesInFile();
    }
}
