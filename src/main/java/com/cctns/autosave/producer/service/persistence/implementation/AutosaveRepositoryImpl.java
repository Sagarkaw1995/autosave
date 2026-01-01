package com.cctns.autosave.producer.service.persistence.implementation;

import com.cctns.autosave.producer.service.core.repository.AutosaveRepository;
import org.springframework.stereotype.Component;

@Component
public class AutosaveRepositoryImpl implements AutosaveRepository {

    private final ArrestSavedFormJpaRepo arrestSavedFormJpaRepo;
    private final ComplaintSavedFormJpaRepo complaintSavedFormJpaRepo;
    private final FirSavedFormJpaRepo firSavedFormJpaRepo;
    private final MissingPersonSavedFormJpaRepo missingPersonSavedFormJpaRepo;
    private final MlcSavedFormJpaRepo mlcSavedFormJpaRepo;
    private final NcrSavedFormJpaRepo ncrSavedFormJpaRepo;
    private final UidbSavedFormJpaRepo uidbSavedFormJpaRepo;
    private final UifpSavedFormJpaRepo uifpSavedFormJpaRepo;

    public AutosaveRepositoryImpl(ArrestSavedFormJpaRepo arrestSavedFormJpaRepo, ComplaintSavedFormJpaRepo complaintSavedFormJpaRepo, FirSavedFormJpaRepo firSavedFormJpaRepo, MissingPersonSavedFormJpaRepo missingPersonSavedFormJpaRepo, MlcSavedFormJpaRepo mlcSavedFormJpaRepo, NcrSavedFormJpaRepo ncrSavedFormJpaRepo, UidbSavedFormJpaRepo uidbSavedFormJpaRepo, UifpSavedFormJpaRepo uifpSavedFormJpaRepo) {
        this.arrestSavedFormJpaRepo = arrestSavedFormJpaRepo;
        this.complaintSavedFormJpaRepo = complaintSavedFormJpaRepo;
        this.firSavedFormJpaRepo = firSavedFormJpaRepo;
        this.missingPersonSavedFormJpaRepo = missingPersonSavedFormJpaRepo;
        this.mlcSavedFormJpaRepo = mlcSavedFormJpaRepo;
        this.ncrSavedFormJpaRepo = ncrSavedFormJpaRepo;
        this.uidbSavedFormJpaRepo = uidbSavedFormJpaRepo;
        this.uifpSavedFormJpaRepo = uifpSavedFormJpaRepo;
    }


    @Override
    public Integer updateComplaintSaveForm(String complainantName, Long savedNum) {
        return complaintSavedFormJpaRepo.updateComplainantNameByComplSavedNum(complainantName, savedNum);
    }

    @Override
    public Integer updateFirSaveForm(String complainantName, Long savedNum) {
        return firSavedFormJpaRepo.updateComplainantNameByFirSavedNum(complainantName, savedNum);
    }

    @Override
    public Integer updateMissingPersonSaveForm(String complainantName, Long savedNum) {
        return missingPersonSavedFormJpaRepo.updateComplainantNameByMpersSavedNum(complainantName, savedNum);
    }

    @Override
    public Integer updateNcrSaveForm(String complainantName, Long savedNum) {
        return ncrSavedFormJpaRepo.updateComplainantNameByNcrSavedNum(complainantName, savedNum);
    }

    @Override
    public Integer updateUifpSaveForm(String complainantName, Long savedNum) {
        return uifpSavedFormJpaRepo.updateInformantNameBySavedNum(complainantName, savedNum);
    }

    @Override
    public Integer updateMlcSaveForm(String injuredName, String mlcType, String mlcSubType, Long mlcSavedNum) {
        return mlcSavedFormJpaRepo.updateInjuredNameAndMlcTypeAndMlcSubTypeByMlcSavedNum(injuredName, mlcType,mlcSubType,mlcSavedNum);
    }

    @Override
    public Integer updateUidbSaveForm(String complainantName, Long savedNum) {
        return uidbSavedFormJpaRepo.updateInformantNameByUidbSavedNum(complainantName, savedNum);
    }

    @Override
    public Integer updateArrestSaveForm(String complainantName, Long savedNum) {
        return arrestSavedFormJpaRepo.updateAccusedNameByArrestSavedNum(complainantName, savedNum);
    }
}
