package com.cctns.autosave.producer.service.core.repository;

public interface AutosaveRepository {

    /**
     * Updates the Complaint draft
     *
     * @param complainantName
     * @param savedNum
     * @return
     */
    public Integer updateComplaintSaveForm(String complainantName, Long savedNum);

    public Integer updateFirSaveForm(String complainantName, Long savedNum);

    public Integer updateMissingPersonSaveForm(String complainantName, Long savedNum);

    public Integer updateNcrSaveForm(String complainantName, Long savedNum);

    public Integer updateUifpSaveForm(String complainantName, Long savedNum);

    public Integer updateMlcSaveForm(String injuredName, String mlcType, String mlcSubType, Long mlcSavedNum);

    public Integer updateUidbSaveForm(String complainantName, Long savedNum);

    public Integer updateArrestSaveForm(String complainantName, Long savedNum);

    public Integer updateStrangerRollSaveForm(String complainantName, Long savedNum);
}
