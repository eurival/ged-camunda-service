package br.com.interglobal.ged_camunda_service.delagate;

 

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("aprovarDocumentoDelegate")
public class AprovarDocumentoDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String documentoId = (String) execution.getVariable("documentoId");
        String usuario = (String) execution.getVariable("usuario");
        log.info("Aprovando documento {} pelo usuário {}", documentoId, usuario);

        // Aqui você pode chamar o backend principal (ged-api) via REST
        // Exemplo: RestTemplate.postForObject("http://ged-api/documentos/{id}/aprovar", ...)
    }
}
