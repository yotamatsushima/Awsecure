package awstools.check;

import awstools.AWSClients;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;

import com.amazonaws.services.identitymanagement.model.InstanceProfile;

import com.amazonaws.services.identitymanagement.model.PolicyDetail;
import com.amazonaws.services.identitymanagement.model.RoleDetail;

import com.amazonaws.services.identitymanagement.model.UserDetail;
import com.amazonaws.services.identitymanagement.model.VirtualMFADevice;
import java.net.URLDecoder;
import java.util.HashMap;

public class CheckIAM
  extends CheckService
{
  private AmazonIdentityManagementClient client;
  private HashMap<String, String> accesskeyIsUseUser = new HashMap();
  private HashMap<String, String> mfaIsUseUser = new HashMap();
  
  public String display()
  {
    displayRoleList();
    displayAccountPassowrdPolicy();
    return getHtmlString();
  }
  
  private void displayRoleList()
  {
    writeln("\nAIM Role List", true, 1);
    for (RoleDetail role : this.client.getAccountAuthorizationDetails().getRoleDetailList())
    {
      try
      {
        writeln("rolename(" + role.getRoleName() + ")", true, 2);
        String output = "attached instance:";
        for (InstanceProfile instanceprofile : role.getInstanceProfileList()) {
          output = output + instanceprofile.getInstanceProfileName();
        }
        writeln(output, false, 3);
      }
      catch (Exception e) {}
      for (PolicyDetail policy : role.getRolePolicyList()) {
        try
        {
          writeln("policy(" + policy.getPolicyName() + "):" + URLDecoder.decode(policy.getPolicyDocument(), "utf-8"), false, 3);
        }
        catch (Exception e) {}
      }
      closeFolder(2);
    }
    closeFolder(1);
    writeln("\n User Role List", true, 1);
    for (UserDetail user : this.client.getAccountAuthorizationDetails().getUserDetailList())
    {
      try
      {
        writeln("rolename(" + user.getUserName() + "):", true, 2);
        for (PolicyDetail policy : user.getUserPolicyList()) {
          writeln("policy(" + policy.getPolicyName() + "):" + URLDecoder.decode(policy.getPolicyDocument(), "utf-8").replaceAll("\n", "").replaceAll(" ", ""), false, 3);
        }
        for (String group : user.getGroupList()) {
          writeln("groupName:" + group, false, 3);
        }
      }
      catch (Exception e) {}
      if (this.accesskeyIsUseUser.get(user.getUserName()) != null) {
        writeln("AccessKey exist!" + setAlert(), false, 3);
      }
      if (this.mfaIsUseUser.get(user.getUserName()) == null) {
        writeln("MFA is not used!" + setAlert(), false, 3);
      }
      closeFolder(2);
    }
    closeFolder(1);
  }
  
  private void displayAccountPassowrdPolicy()
  {
    writeln("\nintense Password information", true, 1);
    if (this.client.getAccountPasswordPolicy().getPasswordPolicy().getMinimumPasswordLength().intValue() < 8) {
      writeln("You MUST set confing of password  as 8 characters and more!!!! " + setNG(), false, 2);
    }
    if (!this.client.getAccountPasswordPolicy().getPasswordPolicy().getRequireLowercaseCharacters().booleanValue()) {
      writeln("You MUST set confing of password  including one lower character and more. " + setNG(), false, 2);
    }
    if (!this.client.getAccountPasswordPolicy().getPasswordPolicy().getRequireUppercaseCharacters().booleanValue()) {
      writeln("You MUST set confing of password  including one upper character and more. " + setNG(), false, 2);
    }
    if (!this.client.getAccountPasswordPolicy().getPasswordPolicy().getRequireSymbols().booleanValue()) {
      writeln("You MUST set confing of password  including one symbol and more. " + setNG(), false, 2);
    }
    if (!this.client.getAccountPasswordPolicy().getPasswordPolicy().getRequireNumbers().booleanValue()) {
      writeln("You MUST set confing of password  including one number and more. " + setNG(), false, 2);
    }
    closeFolder(1);
  }
  
  private void loadAccessKeys()
  {
    for (AccessKeyMetadata access : this.client.listAccessKeys().getAccessKeyMetadata()) {
      if (access.getStatus().toLowerCase().equals("active")) {
        this.accesskeyIsUseUser.put(access.getUserName(), "active");
      }
    }
  }
  
  private void loadMFA()
  {
    for (VirtualMFADevice mfa : this.client.listVirtualMFADevices().getVirtualMFADevices()) {
      this.mfaIsUseUser.put(mfa.getUser().getUserName(), "use");
    }
  }
  
  public void load(AWSClients clients, Regions regions)
  {
    this.client = ((AmazonIdentityManagementClient)clients.getClient("iam"));
    this.client.setRegion(regions);
    loadAccessKeys();
    loadMFA();
  }
}
