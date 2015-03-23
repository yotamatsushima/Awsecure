package awstools.check;

import awstools.AWSClients;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.UserIdGroupPair;

import java.util.List;

public abstract class CheckService
{
  private final String ALERT_STRING = "[###ALERT###]";
  private final String NG_STRING = "[###NG###]";
  private String htmlString = "";
  private int ngCount = 0;
  private int alertCount = 0;
  
  public String setAlert()
  {
    this.alertCount += 1;
    return "[###ALERT###]";
  }
  
  public String setNG()
  {
    this.ngCount += 1;
    return "[###NG###]";
  }
  
  public int getNGCount()
  {
    return this.ngCount;
  }
  
  public int getAlertcount()
  {
    return this.alertCount;
  }
  
  public void writeln(String print, boolean folder, int level)
  {
    write(print + "\n", folder, level);
  }
  
  public void write(String print, boolean folder, int level)
  {
    System.out.print(repeat(" ", level * 4) + print);
    if (folder) {
      createFolder(print);
    } else {
      createFile(print);
    }
  }
  
  private void createFolder(String name)
  {
    this.htmlString = (this.htmlString + "<li class='expandable'><div class='hitarea expandable-hitarea'></div>" + name + "<ul style='display: none;'>\n");
  }
  
  private void createFile(String name)
  {
    this.htmlString = (this.htmlString + "<li>" + name + "</li>\n");
  }
  
  public void closeFolder(int i)
  {
    this.htmlString += "</ul></li>\n";
  }
  
  public abstract String display();
  
  public String getHtmlString()
  {
    return this.htmlString;
  }
  
  public abstract void load(AWSClients paramAWSClients, Regions paramRegions);
  
  public void displaySecurityGroup(List<SecurityGroup> sglist, int level)
  {
    for (SecurityGroup security : sglist)
    {
      writeln("inbound security group(" + security.getGroupName() + ")", true, level + 1);
      for (IpPermission inbound : security.getIpPermissions())
      {
        String output = "";
        if (inbound.getFromPort() != null) {
          output = output + " port:" + inbound.getFromPort() + "-" + inbound.getToPort();
        } else {
          output = output + " port:!!!ANY!!!";
        }
        if ((inbound.getIpRanges().isEmpty()) && (inbound.getUserIdGroupPairs().isEmpty()))
        {
          output = output + "!!!ANY!!!";
        }
        else
        {
          for (String range : inbound.getIpRanges()) {
            output = output + " iprange:" + range;
          }
          for (UserIdGroupPair useridgrouppairs : inbound.getUserIdGroupPairs()) {
            output = output + " source_ip group_id:" + useridgrouppairs.getGroupId();
          }
        }
        if ((inbound.getFromPort() == null) && (inbound.getUserIdGroupPairs().isEmpty())) {
          for (String range : inbound.getIpRanges()) {
            if (range.equals("0.0.0.0/0"))
            {
              output = output + setNG();
              break;
            }
          }
        }
        writeln(output, false, level + 2);
      }
      closeFolder(level + 1);
      
      writeln("outbound security group(" + security.getGroupName() + ")", true, level + 1);
      for (IpPermission outbound : security.getIpPermissionsEgress())
      {
        String output = "";
        if (outbound.getFromPort() != null) {
          output = output + " port:" + outbound.getFromPort() + "-" + outbound.getToPort();
        } else {
          output = output + " port:!!!ANY!!!";
        }
        if ((outbound.getIpRanges().isEmpty()) && (outbound.getUserIdGroupPairs().isEmpty()))
        {
          output = output + "iprange:!!!ANY!!!";
        }
        else
        {
          for (String range : outbound.getIpRanges()) {
            output = output + " iprange:" + range;
          }
          for (UserIdGroupPair useridgrouppairs : outbound.getUserIdGroupPairs()) {
            output = output + " source_ip group_id:" + useridgrouppairs.getGroupId();
          }
        }
        if ((outbound.getFromPort() == null) && (outbound.getUserIdGroupPairs().isEmpty())) {
          for (String range : outbound.getIpRanges()) {
            if (range.equals("0.0.0.0/0"))
            {
              output = output + setNG();
              break;
            }
          }
        }
        writeln(output, false, level + 2);
      }
      closeFolder(level + 1);
    }
  }
  
  private String repeat(String s, int count)
  {
    String repeatStrings = s;
    for (int i = 0; i < count; i++) {
      repeatStrings = repeatStrings.concat(s);
    }
    return repeatStrings;
  }
}
