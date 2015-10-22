package org.project.openbaton.nubomedia.api;


import com.google.gson.Gson;
import org.project.openbaton.nubomedia.api.messages.BuildingStatus;
import org.project.openbaton.nubomedia.api.openshift.ConfigReader;
import org.project.openbaton.nubomedia.api.openshift.beans.*;
import org.project.openbaton.nubomedia.api.openshift.exceptions.UnauthorizedException;
import org.project.openbaton.nubomedia.api.openshift.json.ImageStreamConfig;
import org.project.openbaton.nubomedia.api.openshift.json.RouteConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Properties;


/**
 * Created by maa on 27/09/2015.
 */

@Service
public class OpenshiftManager {

    @Autowired private Gson mapper;
    @Autowired private SecretManager secretManager;
    @Autowired private ImageStreamManager imageStreamManager;
    @Autowired private BuildManager buildManager;
    @Autowired private DeploymentManager deploymentManager;
    @Autowired private ServiceManager serviceManager;
    @Autowired private RouteManager routeManager;
    @Autowired private AuthenticationManager authManager;
    private Properties config;
    private Logger logger;
    private String openshiftBaseURL;
    private String kubernetesBaseURL;


    @PostConstruct
    private void init() throws IOException {
        this.config = ConfigReader.loadProperties();
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.openshiftBaseURL = config.getProperty("baseURL") + "/oapi/v1/namespaces/";
        this.kubernetesBaseURL = config.getProperty("baseURL") + "/api/v1/namespaces/";
    }

    public String authenticate(String username, String password) throws UnauthorizedException {

        return this.authManager.authenticate(config.getProperty("baseURL"),username,password);

    }

    public String buildApplication(String token, String appID, String appName, String namespace,String gitURL,int[] ports,int[] targetPorts,String[] protocols, int replicasnumber, String secretName, String mediaServerGID){

        HttpHeaders creationHeader = new HttpHeaders();
        creationHeader.add("Authorization","Bearer " + token);
        creationHeader.add("Content-type","application/json");

        logger.info("Starting build app " + appName + " in project " + namespace);

        ResponseEntity<String> appBuilEntity = imageStreamManager.makeImageStream(openshiftBaseURL, appName, namespace, creationHeader);
        if(!appBuilEntity.getStatusCode().is2xxSuccessful()){
            logger.debug("Failed creation of imagestream " + appBuilEntity.toString());
            return appBuilEntity.getBody();
        }

        ImageStreamConfig isConfig = mapper.fromJson(appBuilEntity.getBody(),ImageStreamConfig.class);

        appBuilEntity = buildManager.createBuild(openshiftBaseURL, appName, namespace, gitURL, isConfig.getStatus().getDockerImageRepository(), creationHeader, secretName, mediaServerGID,config.getProperty("vnfmIP"),config.getProperty("vnfmPort"));
        if(!appBuilEntity.getStatusCode().is2xxSuccessful()){
            logger.debug("Failed creation of buildconfig " + appBuilEntity.toString());
            return appBuilEntity.getBody();
        }

        appBuilEntity = deploymentManager.makeDeployment(openshiftBaseURL, appName, isConfig.getStatus().getDockerImageRepository(), targetPorts, protocols, replicasnumber, namespace, creationHeader);
        if(!appBuilEntity.getStatusCode().is2xxSuccessful()){
            logger.debug("Failed creation of deploymentconfig " + appBuilEntity.toString());
            return appBuilEntity.getBody();
        }

        appBuilEntity = serviceManager.makeService(kubernetesBaseURL, appName, namespace, ports, targetPorts, protocols, creationHeader);
        if(!appBuilEntity.getStatusCode().is2xxSuccessful()){
            logger.debug("Failed creation of service " + appBuilEntity.toString());
            return appBuilEntity.getBody();
        }

        appBuilEntity = routeManager.makeRoute(openshiftBaseURL, appID, appName, namespace, creationHeader);
        if(!appBuilEntity.getStatusCode().is2xxSuccessful()){
            logger.debug("Failed creation of route " + appBuilEntity.toString());
            return appBuilEntity.getBody();
        }

        RouteConfig route = mapper.fromJson(appBuilEntity.getBody(),RouteConfig.class);

        return route.getSpec().getHost();

    }

    public HttpStatus deleteApplication(String token, String appName, String namespace){

        HttpHeaders deleteHeader = new HttpHeaders();
        deleteHeader.add("Authorization","Bearer " + token);

        HttpStatus res = imageStreamManager.deleteImageStream(openshiftBaseURL, appName, namespace, deleteHeader);
        if (!res.is2xxSuccessful()) return res;

        res = buildManager.deleteBuild(openshiftBaseURL, appName, namespace, deleteHeader);
        if (!res.is2xxSuccessful()) return res;

        res = deploymentManager.deleteDeployment(openshiftBaseURL, appName, namespace, deleteHeader);
        if (!res.is2xxSuccessful()) return res;

        res = deploymentManager.deletePodsRC(kubernetesBaseURL, appName, namespace, deleteHeader);
        if (!res.is2xxSuccessful()) return res;

        res = serviceManager.deleteService(kubernetesBaseURL, appName, namespace, deleteHeader);
        if (!res.is2xxSuccessful()) return res;

        res = routeManager.deleteRoute(openshiftBaseURL,appName,namespace,deleteHeader);

        return res;
    }

    public String createSecret (String token, String privateKey, String namespace){

        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("Authorization","Bearer " + token);
        return secretManager.createSecret(kubernetesBaseURL, namespace, privateKey, authHeader);
    }

    public HttpStatus deleteSecret(String token ,String secretName, String namespace){
        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("Authorization","Bearer " + token);

        HttpStatus entity = secretManager.deleteSecret(kubernetesBaseURL, secretName,namespace,authHeader);
        return entity;
    }

    public BuildingStatus getStatus (String token, String appName, String namespace){

        BuildingStatus res = BuildingStatus.INITIALISED;
        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("Authorization","Bearer " + token);

        BuildingStatus status = buildManager.getApplicationStatus(openshiftBaseURL, appName, namespace, authHeader);

        switch (status){
            case BUILDING:
                res = BuildingStatus.BUILDING;
                break;
            case FAILED:
                res = BuildingStatus.FAILED;
                break;
            case BUILD_OK:
                res = deploymentManager.getDeployStatus(kubernetesBaseURL,appName,namespace,authHeader);
                break;
        }

        return res;
    }

    public String getBuildLogs(String token, String appName,String namespace){

        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("Authorization","Bearer " + token);
        return buildManager.getBuildLogs(openshiftBaseURL, appName, namespace, authHeader);
    }

}
