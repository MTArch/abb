package in.gov.abdm.abha.enrollment.services.database.dependent_account_relationship.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.client.AbhaDBDependentAccountRelationshipFClient;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.services.database.dependent_account_relationship.DependentAccountRelationshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DependentAccountRelationshipServiceImpl extends AbhaDBClient implements DependentAccountRelationshipService {

    @Autowired
    AbhaDBDependentAccountRelationshipFClient abhaDBDependentAccountRelationshipFClient;

    public static final String ABHA_APP = "ABHA_APP";

    //    @Override
    public Mono<DependentAccountRelationshipDto> createDependentAccountEntity(List<DependentAccountRelationshipDto> dependentAccountRelationshipList) {
        return abhaDBDependentAccountRelationshipFClient.createDependentRelationships(dependentAccountRelationshipList)
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public List<DependentAccountRelationshipDto> prepareDependentAccount(LinkParentRequestDto linkParentRequestDto) {
        List<DependentAccountRelationshipDto> list = new ArrayList<>();
        DependentAccountRelationshipDto dependentAccountDto = new DependentAccountRelationshipDto();
        if (linkParentRequestDto.getParentAbhaRequestDtoList() != null && linkParentRequestDto.getParentAbhaRequestDtoList().size() > 0) {
            for (int i = 0; i < linkParentRequestDto.getParentAbhaRequestDtoList().size(); i++) {
                dependentAccountDto.setParentHealthIdNumber(linkParentRequestDto.getParentAbhaRequestDtoList().get(i).getABHANumber());
                dependentAccountDto.setDependentHealthIdNumber(linkParentRequestDto.getChildAbhaRequestDto().getABHANumber());
                dependentAccountDto.setRelatedAs(linkParentRequestDto.getParentAbhaRequestDtoList().get(i).getRelationship().getValue());
                dependentAccountDto.setRelationshipProofDocumentLocation(linkParentRequestDto.getParentAbhaRequestDtoList().get(i).getDocument());
                dependentAccountDto.setCreatedBy(ABHA_APP);
                dependentAccountDto.setUpdatedBy(ABHA_APP);
                dependentAccountDto.setCreatedDate(LocalDateTime.now());
                dependentAccountDto.setUpdatedDate(LocalDateTime.now());
                list.add(dependentAccountDto);
            }
        }
        return list;
    }

}
