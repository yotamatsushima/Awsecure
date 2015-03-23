package awstools.check;

import awstools.AWSClients;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.support.AWSSupportClient;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorCheckResultRequest;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorCheckResultResult;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorChecksRequest;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorChecksResult;
import com.amazonaws.services.support.model.TrustedAdvisorCheckDescription;


public class CheckSupport
  extends CheckService
{
  private AWSSupportClient supportclient;
  
  public void load(AWSClients clients, Regions regions)
  {
    this.supportclient = new AWSSupportClient(new DefaultAWSCredentialsProviderChain());
  }
  
  public String display()
  {
    writeln("\nTrustedAdvisor information", true, 1);
    DescribeTrustedAdvisorChecksRequest tacrequest = new DescribeTrustedAdvisorChecksRequest();
    tacrequest.setLanguage("ja");
    DescribeTrustedAdvisorChecksResult tacresult = this.supportclient.describeTrustedAdvisorChecks(tacrequest);
    for (TrustedAdvisorCheckDescription tacheck : tacresult.getChecks())
    {
      DescribeTrustedAdvisorCheckResultRequest tacresultrequest = new DescribeTrustedAdvisorCheckResultRequest();
      tacresultrequest.setCheckId(tacheck.getId());
      
      DescribeTrustedAdvisorCheckResultResult trustedadvisor = this.supportclient.describeTrustedAdvisorCheckResult(tacresultrequest);
      if ((trustedadvisor.getResult().getStatus().equals("error")) || (trustedadvisor.getResult().getStatus().equals("not_available"))) {
        writeln(trustedadvisor.getResult().getStatus() + ":" + tacheck.getName() + setAlert(), false, 2);
      }
    }
    closeFolder(1);
    return getHtmlString();
  }
}
