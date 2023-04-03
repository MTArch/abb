package in.gov.abdm.abha.enrollment.services.database.dependent_account_relationship.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.client.AbhaDBDependentAccountRelationshipFClient;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.services.database.dependent_account_relationship.DependentAccountRelationshipService;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class DependentAccountRelationshipServiceImpl extends AbhaDBClient implements DependentAccountRelationshipService {

    @Autowired
    AbhaDBDependentAccountRelationshipFClient abhaDBDependentAccountRelationshipFClient;

    public static final String PARSER_EXCEPTION_OCCURRED_DURING_PARSING = "Parser Exception occurred during parsing :";
    public static final String EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB = "Exception in parsing Invalid value of DOB: {}";
    public static final String ABHA_APP = "ABHA_APP";
    private DateFormat KYC_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

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
