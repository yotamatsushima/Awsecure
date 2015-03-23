package awstools.check;

import awstools.AWSClients;
import awstools.Tools;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;

import com.amazonaws.services.ec2.model.DescribeSnapshotsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Snapshot;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.VolumeAttachment;
import java.util.HashMap;

import java.util.List;

public class CheckEBS
  extends CheckService
{
  private AmazonEC2Client ec2client;
  private HashMap<String, String> kvInstance = new HashMap();
  
  public void load(AWSClients clients, Regions regions)
  {
    this.ec2client = ((AmazonEC2Client)clients.getClient("ec2"));
    List<Reservation> reserves = this.ec2client.describeInstances().getReservations();
    for (Reservation reserve : reserves) {
      for (Instance instance : reserve.getInstances()) {
        if (instance.getSubnetId() != null) {
          this.kvInstance.put(instance.getInstanceId(), Tools.getTagValue(instance.getTags(), "NAME"));
        }
      }
    }
  }
  
  public String display()
  {
    writeln("\nEBS information", true, 1);
    
    DescribeVolumesResult descEbsResult = this.ec2client.describeVolumes();
    DescribeSnapshotsResult descSnapReslut = this.ec2client.describeSnapshots();
    for (Volume volume : descEbsResult.getVolumes())
    {
      String attachInstanceName = "";
      for (VolumeAttachment attachinstance : volume.getAttachments())
      {

        if (this.kvInstance.get(attachinstance.getInstanceId()) != null) {
          attachInstanceName = attachInstanceName + "," + (String)this.kvInstance.get(attachinstance.getInstanceId()) + "(" + attachinstance.getInstanceId() + ")";
        }
      }
      VolumeAttachment attachinstance;
      writeln("volume:" + Tools.getTagValue(volume.getTags(), "name") + "(" + volume.getVolumeId() + "):attach instance:" + attachInstanceName, false, 2);
      int snapcount = 0;
      for (Snapshot snap : descSnapReslut.getSnapshots()) {
        if (volume.getVolumeId().equals(snap.getVolumeId())) {
          snapcount++;
        }
      }
      if (snapcount == 0) {
        writeln("This volume have no EBS Snapshot.  check it." + setAlert(), false, 3);
      } else if (snapcount > 3) {
        writeln("This volume have " + snapcount + " EBS Snapshot.  It's too match." + setAlert(), false, 3);
      } else {
        writeln("This volume have " + snapcount + " EBS Snapshot.", false, 3);
      }
    }
    closeFolder(1);
    
    return getHtmlString();
  }
}
