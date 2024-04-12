package in.gov.abdm.abha.enrollment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.constant.ABHAConstants;
import in.gov.abdm.abha.enrollment.configuration.filters.ClientFilter;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.link.parent.Relationship;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAChildProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.ChildAbhaRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.ParentAbhaRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.response.LinkParentResponseDto;
import in.gov.abdm.abha.enrollment.model.profile.children.ChildrenProfiles;
import in.gov.abdm.abha.enrollment.services.enrol.child.EnrolChildService;
import in.gov.abdm.abha.enrollment.services.link.parent.LinkParentService;
import in.gov.abdm.constant.Gender;
import io.grpc.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static in.gov.abdm.abha.constant.ABHAConstants.APIKEY;
import static in.gov.abdm.abha.constant.ABHAConstants.AUTHORIZATION;
import static in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.*;
import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;
import static in.gov.abdm.constant.ABDMConstant.TIMESTAMP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ProfileController.class)
@ActiveProfiles(profiles = "test")
public class ProfileControllerTests {
    @Autowired
    private WebTestClient webClient;
    /*@MockBean
    ClientFilter clientFilter;*/
    @MockBean
    LinkParentService linkParentService;
    @MockBean
    EnrolChildService enrolChildService;
    private LinkParentRequestDto linkParentRequestDto;
    private LinkParentResponseDto linkParentResponseDto;
    private ConsentDto consentDto;
    private ChildrenProfiles childrenProfiles;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        linkParentRequestDto=new LinkParentRequestDto();
        linkParentResponseDto=new LinkParentResponseDto();
        childrenProfiles= ChildrenProfiles.builder().build();
        childrenProfiles=new ChildrenProfiles(ABHA_NUMBER_VALID,MOBILE_NUMBER_VALID,ABHA_ADDRESS_VALID,1,Arrays.asList(new ABHAChildProfileDto()));
        consentDto=new ConsentDto();
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        linkParentRequestDto.setTxnId(TRANSACTION_ID_VALID);
        linkParentRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL));
        linkParentRequestDto.setConsentDto(consentDto);
        linkParentRequestDto.setChildAbhaRequestDto(new ChildAbhaRequestDto(ABHA_NUMBER_VALID));
        linkParentRequestDto.setParentAbhaRequestDtoList(Arrays.asList(new ParentAbhaRequestDto(ABHA_NUMBER_VALID,"Name","2000", Gender.M.getValue(),"9872829929",EMAIL_VALID, Relationship.MOTHER,"document.txt")));
        linkParentResponseDto.setTxnId(TRANSACTION_ID_VALID);
    }
    @AfterEach
    void tearDown(){
        linkParentRequestDto=null;
        linkParentResponseDto=null;
    }
    @Test
    @WithMockUser
    public void linkDependentAccountTest() throws JsonProcessingException {
        ObjectMapper objMapper = new ObjectMapper();
        String jsonString = objMapper.writeValueAsString(linkParentRequestDto);
        String request = "{\"txnId\":\"09afedef-34fe-51fc-89ab-0123456789ab\",\"scope\":[\"parent-abha-link\"],\"ParentAbha\":[{\"ABHANumber\":\"12-3456-7898-1234\",\"name\":\"Name\",\"yearOfBirth\":\"2000\",\"gender\":\"M\",\"mobile\":\"9872829929\",\"email\":\"abc@abc.com\",\"relationship\":\"mother\",\"document\":\"document.txt\"}],\"ChildAbha\":{\"ABHANumber\":\"12-3456-7898-1234\"},\"consent\":{\"code\":\"abha-enrollment\",\"version\":\"1.4\"}}";
        Mockito.when(linkParentService.linkDependentAccount(any())).thenReturn(Mono.just(linkParentResponseDto));
        webClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.PROFILE_ENDPOINT + URIConstant.LINK_PARENT_ENDPOINT)
                .header(TIMESTAMP, TIMESTAMP_HEADER_VALUE )
                .header(REQUEST_ID, REQUEST_ID_VALUE)
               // .header(AUTHORIZATION,"Bearer AUTHORIZATION")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(TRANSACTION_ID_VALID);
        //.jsonPath("$.message").isEqualTo(CommonTestData.SUCCESS_MESSAGE);

    }
    @Test
    @WithMockUser
    public void getChildrenTest() throws JsonProcessingException {
        ObjectMapper objMapper = new ObjectMapper();
        String jsonString = objMapper.writeValueAsString(linkParentRequestDto);
        String request = "{\"txnId\":\"09afedef-34fe-51fc-89ab-0123456789ab\",\"scope\":[\"parent-abha-link\"],\"ParentAbha\":[{\"ABHANumber\":\"12-3456-7898-1234\",\"name\":\"Name\",\"yearOfBirth\":\"2000\",\"gender\":\"M\",\"mobile\":\"9872829929\",\"email\":\"abc@abc.com\",\"relationship\":\"mother\",\"document\":\"document.txt\"}],\"ChildAbha\":{\"ABHANumber\":\"12-3456-7898-1234\"},\"consent\":{\"code\":\"abha-enrollment\",\"version\":\"1.4\"}}";
        Mockito.when(enrolChildService.validateChildHeaders(any())).thenReturn(Mono.just(true));
        Mockito.when(enrolChildService.getChildren(any())).thenReturn(Mono.just(childrenProfiles));
        webClient.mutateWith(csrf())
                .get()
                .uri(URIConstant.PROFILE_ENDPOINT + URIConstant.PARENT_CHILDREN_ENDPOINT)
                .header(TIMESTAMP, TIMESTAMP_HEADER_VALUE )
                .header(REQUEST_ID, REQUEST_ID_VALUE)
                .header(X_TOKEN,TOKEN_VALID)
                // .header(AUTHORIZATION,"Bearer AUTHORIZATION")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
                //.jsonPath("$.txnId").isEqualTo(TRANSACTION_ID_VALID);
        //.jsonPath("$.message").isEqualTo(CommonTestData.SUCCESS_MESSAGE);

    }
}
