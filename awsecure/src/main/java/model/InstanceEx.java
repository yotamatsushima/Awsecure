package model;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.SecurityGroup;
import java.util.ArrayList;
import java.util.List;

public class InstanceEx
{
  private Instance instance = new Instance();
  private List<SecurityGroup> securityGroups = new ArrayList();
  
  public void setInstance(Instance i)
  {
    this.instance = i;
  }
  
  public Instance getInstance()
  {
    return this.instance;
  }
  
  public void addSG(SecurityGroup sg)
  {
    this.securityGroups.add(sg);
  }
  
  public List<SecurityGroup> getSecurityGroups()
  {
    return this.securityGroups;
  }
}
