package org.bahmni.datamigration;

public class AddressService {
    private MasterTehsils masterTehsils;
    private AmbiguousTehsils ambiguousTehsils;
    private CorrectedTehsils correctedTehsils;

    public AddressService(MasterTehsils masterTehsils, AmbiguousTehsils ambiguousTehsils, CorrectedTehsils correctedTehsils) {
        this.masterTehsils = masterTehsils;
        this.ambiguousTehsils = ambiguousTehsils;
        this.correctedTehsils = correctedTehsils;
    }

    public FullyQualifiedTehsil getTehsilFor(FullyQualifiedTehsil tehsilFromPatientRecord) {
        String correctedTehsil = correctedTehsils.correctedTehsil(tehsilFromPatientRecord.getTehsil());
        FullyQualifiedTehsil matchingMasterTehsil = masterTehsils.getFullyQualifiedTehsil(correctedTehsil);
        if (ambiguousTehsils.contains(matchingMasterTehsil.getTehsil())) {
            return new FullyQualifiedTehsil(matchingMasterTehsil.getTehsil(),
                    tehsilFromPatientRecord.getDistrict(), tehsilFromPatientRecord.getState());
        }
        return matchingMasterTehsil;
    }
}